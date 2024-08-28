package com.fsoft.fsa.kindergarten.service.imp;

import com.fsoft.fsa.kindergarten.config.exception.NoAuthorityException;
import com.fsoft.fsa.kindergarten.config.exception.PasswordsNotMatchException;
import com.fsoft.fsa.kindergarten.config.exception.ResourceNotFoundException;
import com.fsoft.fsa.kindergarten.config.exception.UserEmailExistException;
import com.fsoft.fsa.kindergarten.model.dto.feedback.FeedbackDTO;
import com.fsoft.fsa.kindergarten.model.dto.page.PageResponse;
import com.fsoft.fsa.kindergarten.model.dto.school.SchoolRatingDTO;
import com.fsoft.fsa.kindergarten.model.dto.user.ParentDTO;
import com.fsoft.fsa.kindergarten.model.dto.user.ParentDetailDTO;
import com.fsoft.fsa.kindergarten.model.dto.user.UserDTO;
import com.fsoft.fsa.kindergarten.model.entity.*;
import com.fsoft.fsa.kindergarten.model.form.user.CreateUserForm;
import com.fsoft.fsa.kindergarten.model.form.user.RegisterForm;
import com.fsoft.fsa.kindergarten.model.form.user.ResetPasswordForm;
import com.fsoft.fsa.kindergarten.model.form.user.UserForm;
import com.fsoft.fsa.kindergarten.model.form.user.*;
import com.fsoft.fsa.kindergarten.repository.IUserSchoolRepository;
import com.fsoft.fsa.kindergarten.repository.specification.TokenSpecification;
import com.fsoft.fsa.kindergarten.repository.specification.UserSpecification;
import com.fsoft.fsa.kindergarten.repository.ITokenRepository;
import com.fsoft.fsa.kindergarten.repository.IUserRepository;
import com.fsoft.fsa.kindergarten.service.*;
import com.fsoft.fsa.kindergarten.model.validation.user.UserStatus;
import com.fsoft.fsa.kindergarten.utils.BaseMapper;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "dev")
public class UserServiceImp implements UserService {

    private final ITokenRepository tokenRepository;
    private final IUserRepository userRepository;
    private final SchoolService schoolService;
    private final RoleService roleService;
    private final EmailService emailService;
    private final TokenService tokenService;
    private final IUserSchoolRepository userSchoolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final FileStorageService fileStorageService;
    private final FeedbackService feedbackService;
    private final BaseMapper baseMapper;


    @Value("${forgot.email.link}")
    private String forgotEmailLink;
    @Value("${forgot.email.subject}")
    private String forgotEmailSubject;
    @Value("${create.email.subject}")
    private String createEmailSubject;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public UserDTO getUser(int userId) {
        User user = getUserById(userId);
        return UserDTO.builder()
                .id(userId)
                .userName(user.getUserName())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .dob(user.getDob())
                .status(user.getStatus())
                .roleId(user.getRole().getId())
                .build();
    }

    @Override
    public ParentDTO getParent(int userId) {
        User parent = getUserById(userId);
        return getParentDTO(parent);
    }

    @Override
    public String getUserName(String fullName) {
        StringBuilder stringBui = new StringBuilder();

        fullName = removeAccents(fullName.toLowerCase().trim());
        String[] name = fullName.split("\\s+");

        String lastName = name[name.length - 1];
        lastName = Character.toUpperCase(lastName.charAt(0)) + lastName.substring(1);
        stringBui.append(lastName);

        for (int i = 0; i < name.length - 1; i++) {
            if (!name[i].isEmpty()) {
                stringBui.append(Character.toUpperCase(name[i].charAt(0)));
            }
        }

        List<User> userList = userRepository.findByUsername(stringBui.toString())
                .stream().filter(user -> user.getUserName().matches("^" + stringBui + "[0-9]*$")).toList();
        long countUsername = userList.stream()
                .map(user -> user.getUserName().replaceAll("\\D", ""))
                .filter(numberStr -> !numberStr.isEmpty())
                .mapToLong(Long::parseLong)
                .max()
                .orElse(0);

        stringBui.append(countUsername + 1);
        return stringBui.toString();
    }

    @Override
    public String getUserNameFromEdit(String fullName, Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.get().getFullName().equals(fullName)) {
            return user.get().getUserName();
        }
        return getUserName(fullName);
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "userList", allEntries = true),
                    @CacheEvict(value = "parentList", allEntries = true)
            }
    )
    public void createUser(CreateUserForm userForm) throws MessagingException, IOException, ParseException {
        if (isUserExistsByEmail(userForm.getEmail())) throw new UserEmailExistException("Email already have existed");
        int accountId = jwtService.getIdFromToken();
        Role role = roleService.getRoleById(userForm.getRoleId());
        String pass = UUID.randomUUID().toString().substring(0, 8);
        String passEncode = passwordEncoder.encode(pass);

        Map<String, String> map = Map.of(
                "fullName", userForm.getFullName(),
                "email", userForm.getEmail(),
                "password", pass
        );
        var userEntity = User.builder()
                .userName(userForm.getUserName())
                .fullName(userForm.getFullName())
                .email(userForm.getEmail())
                .password(passEncode)
                .dob(dateFormatter.parse(userForm.getDob()))
                .phone(userForm.getPhone())
                .status(userForm.getStatus())
                .role(role)
                .build();
        userEntity.setCreatedBy(accountId);
        userRepository.save(userEntity);
        emailService.sendEmail(userForm.getEmail(), createEmailSubject + " " + userForm.getUserName(), map, "/Templates/clientEmail.html");
    }

    @Override
    @Cacheable(value = "userList", key = "@jwtService.getIdFromToken() +  '_' + #page + '_' + #size + '_' + #search + '_' + #sortBy")
    public PageResponse<?> getListUser(int page, int size, String sortBy, String search) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(sortBy));
        Specification<User> specification = UserSpecification.containsTextInEmailNamePhoneOrStatus(search);
        Page<User> users = userRepository.findAll(specification, pageable);

        List<UserDTO> res = users.stream().map(user -> UserDTO.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .dob(user.getDob())
                .phone(user.getPhone())
                .address(buildAddress(user.getAddressLine(), user.getWard(), user.getDistrict(), user.getCity()))
                .roleId(user.getRole().getId())
                .status(user.getStatus())
                .build()
        ).toList();

        return PageResponse.builder()
                .pageNo(page)
                .pageSize(size)
                .totalElement(users.getTotalElements())
                .totalPage(users.getTotalPages())
                .item(res)
                .build();
    }

    @Override
    @Cacheable(value = "parentList", key = "@jwtService.getIdFromToken() +  '_' + #page + '_' + #size + '_' + #search + '_' + #sortBy")
    public PageResponse<?> getListParent(int page, int size, String sortBy, String search) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(sortBy));
        Specification<User> specification = UserSpecification.containsTextInEmailNamePhoneOrStatus(search)
                .and(UserSpecification.isRole(2));

        Page<User> users = userRepository.findAll(specification, pageable);
        List<ParentDTO> res = users.stream().map(this::getParentDTO).toList();

        return PageResponse.builder()
                .pageNo(page)
                .pageSize(size)
                .totalElement(users.getTotalElements())
                .totalPage(users.getTotalPages())
                .item(res)
                .build();
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "userList", allEntries = true),
                    @CacheEvict(value = "parentList", allEntries = true)
            }
    )
    public void updateUser(int userId, UserForm request) throws ParseException {
        User user = getUserById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User not exist");
        }
        if (isUserExistsByEmail(request.getEmail()) && !user.getEmail().equals(request.getEmail())) {
            throw new UserEmailExistException("Email have exist");
        }
        Role role = roleService.getRoleById(request.getRoleId());
        user.setUserName(request.getUserName());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setDob(dateFormatter.parse(request.getDob()));
        user.setPhone(request.getPhone());
        user.setRole(role);
        userRepository.save(user);
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "userList", allEntries = true),
                    @CacheEvict(value = "parentList", allEntries = true)
            }
    )
    public void changeStatus(int userId, UserStatus status) {
        int currentUserId = jwtService.getIdFromToken();
        if (userId == currentUserId) {
            return;
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setStatus(status.toString());
        userRepository.save(user);
    }

    @Override
    public boolean isUserExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean isUserExistsByUsername(String username) {
        return userRepository.existsByUserName(username);
    }

    @Override
    public boolean isEmailExist(String email) {
        return userRepository.exists(UserSpecification.hasEmail(email));
    }


    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "userList", allEntries = true),
                    @CacheEvict(value = "parentList", allEntries = true)
            }
    )
    public void deleteUser(int userId) {
        int userLogin = jwtService.getIdFromToken();
        //check can't delete myself
        if (userId == userLogin) throw new NoAuthorityException("Can't delete user");
        User user = getUserById(userId);
        user.setDeleted(true);
        userRepository.save(user);
    }

    @Override
    @Cacheable(value = "userStatus")
    public List<UserStatus> getUserStatuses() {
        return Stream.of(UserStatus.values()).collect(Collectors.toList());
    }

    @Override
    public void forgotPassword(String email) throws MessagingException, IOException {
        if (!isEmailExist(email)) {
            throw new ResourceNotFoundException("Email not found");
        }
        Map<String, String> map = new HashMap<>();
        String token = generateToken();
        tokenService.saveToken(token, email);
        map.put("subject", "Password Reset");
        map.put("email", email);
        map.put("url", forgotEmailLink + token);
        emailService.sendEmail(email, forgotEmailSubject, map, "/Templates/ForgotPassword.html");
    }

    @Override
    public void isValidToken(String token) {
        if (!tokenService.isTokenValid(token)) {
            throw new ResourceNotFoundException("Token is invalid");
        }
    }

    @Override
    public void resetPassword(String token, ResetPasswordForm passwords) {
        if (!tokenService.isTokenValid(token)) {
            throw new ResourceNotFoundException("Token is invalid");
        }
        if (!passwords.getPassword().equals(passwords.getConfirmPassword())) {
            throw new PasswordsNotMatchException("Password and Confirm password don’t match. Please try again.");
        }
        Token tk = tokenRepository.findOne(TokenSpecification.hasToken(token)).orElseThrow(() -> new ResourceNotFoundException("Token not found"));
        User user = userRepository.findById(tk.getUser().getId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setPassword(passwordEncoder.encode(passwords.getPassword()));
        userRepository.save(user);
        tokenService.softDeleteToken(token);
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    private String removeAccents(String text) {
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        //remove combining marks
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("").replaceAll("đ", "d").replaceAll("Đ", "D");
    }


    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "userList", allEntries = true),
                    @CacheEvict(value = "parentList", allEntries = true)
            }
    )
    public void register(RegisterForm registerForm) {
        if (!registerForm.getPassword().equals(registerForm.getConfirmPassword())) {
            throw new PasswordsNotMatchException("Password and Confirm password don’t match. Please try again.");
        }
        if (isUserExistsByEmail(registerForm.getEmail())) {
            throw new UserEmailExistException("Email already have existed");
        }
        Role role = roleService.getRoleById(2);
        String username = getUserName(registerForm.getFullName());
        String password = registerForm.getPassword();
        User user = User.builder()
                .userName(username)
                .fullName(registerForm.getFullName())
                .email(registerForm.getEmail())
                .password(passwordEncoder.encode(password))
                .phone(registerForm.getPhone())
                .status(UserStatus.Active.toString())
                .role(role)
                .build();
        user = userRepository.save(user);
        user.setCreatedBy(user.getId());
        userRepository.save(user);
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "userList", allEntries = true),
                    @CacheEvict(value = "parentList", allEntries = true)
            }
    )
    public void updateAvatar(MultipartFile avatarFile) {
        int userId = jwtService.getIdFromToken();
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String prefix = "avatar_" + userId;
        String fileUrl = fileStorageService.storeFile(prefix, avatarFile);
        user.setAvatar(fileUrl);
        userRepository.save(user);
    }

    @Override
    public User getUserById(int userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private ParentDTO getParentDTO(User parent) {
        //check user login is not parent
        if (parent.getRole().getId() != 2) throw new NoAuthorityException("This user is not parent");
        List<UserSchool> us = userSchoolRepository.findByUserId(parent.getId());

        List<SchoolRatingDTO> schoolDTOs = schoolService.getSchoolByParent(parent.getId()).stream().map(school -> {
            FeedbackDTO feedbacks = feedbackService.getFeedbacksBySchoolAndAverageRating(school.getId(), parent.getId());
            UserSchool userSchool = us.stream()
                    .filter(userSchools -> userSchools.getSchools().equals(school))
                    .findFirst()
                    .orElse(null);

            return SchoolRatingDTO.builder()
                    .id(school.getId())
                    .schoolName(school.getName())
                    .address(school.getAddressLine() + "," + school.getWard() + "," + school.getDistrict() + "," + school.getCity())
                    .phone(school.getPhone())
                    .email(school.getEmail())
                    .postedDate(school.getCreatedAt())
                    .status(userSchool != null && userSchool.isStatus())
                    .feedback(feedbacks)
                    .build();
        }).toList();

        boolean status = us.stream().anyMatch(UserSchool::isStatus);
        String address = buildAddress(
                parent.getAddressLine(),
                parent.getWard(),
                parent.getDistrict(),
                parent.getCity()
        );

        return ParentDTO.builder()
                .id(parent.getId())
                .fullName(parent.getFullName())
                .email(parent.getEmail())
                .dob(parent.getDob())
                .phone(parent.getPhone())
                .address(address)
                .roleId(parent.getRole().getId())
                .status(status)
                .schools(schoolDTOs)
                .avatar(parent.getAvatar())
                .build();
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "userList", allEntries = true),
                    @CacheEvict(value = "parentList", allEntries = true)
            }
    )
    public void updateParent(int id, UpdateParentForm parentForm, MultipartFile avatarFile) throws ParseException {
        User parent = getUserById(id);
        int jwtId = jwtService.getIdFromToken();
        if (id != jwtId) {
            throw new NoAuthorityException("You don't have permission to change password");
        }
        String fullName = parentForm.getFullName();
        parent.setFullName(fullName);
        parent.setUserName(getUserNameFromEdit(fullName, id));
        parent.setPhone(parentForm.getPhone());
        parent.setDob(dateFormatter.parse(parentForm.getDob()));
        parent.setAddressLine(parentForm.getAddressLine());
        parent.setWard(parentForm.getWard());
        parent.setDistrict(parentForm.getDistrict());
        parent.setCity(parentForm.getCity());
        if (avatarFile != null) {
            String prefix = "avatar_" + id;
            String fileUrl = fileStorageService.storeFile(prefix, avatarFile);
            parent.setAvatar(fileUrl);
        } else {
            parent.setAvatar(parent.getAvatar());
        }
        userRepository.save(parent);
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "userList", allEntries = true),
                    @CacheEvict(value = "parentList", allEntries = true)
            }
    )
    public void changePassword(int id, ChangePasswordForm changePasswordForm) {
        User user = getUserById(id);
        int jwtId = jwtService.getIdFromToken();
        if (id != jwtId) {
            throw new NoAuthorityException("You don't have permission to change password");
        }
        if (!passwordEncoder.matches(changePasswordForm.getOldPassword(), user.getPassword())) {
            throw new PasswordsNotMatchException("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(changePasswordForm.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public ParentDetailDTO getParentDetail(int parentId) {
        if (parentId != jwtService.getIdFromToken()) {
            throw new NoAuthorityException("You don't have permission to get parent detail");
        }
        User parent = getUserById(parentId);
        return baseMapper.convert(parent, ParentDetailDTO.class);
    }

    @Override
    public Optional<User> existUser(int userId) {
        return userRepository.findById(userId);
    }

    private static String buildAddress(String... items) {
        return Stream.of(items)
                .filter(item -> item != null && !item.isEmpty())
                .reduce((item1, item2) -> item1 + ", " + item2)
                .orElse("");
    }

}
