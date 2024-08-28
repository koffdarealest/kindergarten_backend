package com.fsoft.fsa.kindergarten.service;

import com.fsoft.fsa.kindergarten.model.dto.user.ParentDTO;
import com.fsoft.fsa.kindergarten.model.dto.user.ParentDetailDTO;
import com.fsoft.fsa.kindergarten.model.entity.User;
import com.fsoft.fsa.kindergarten.model.form.user.*;
import com.fsoft.fsa.kindergarten.model.dto.user.UserDTO;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import com.fsoft.fsa.kindergarten.model.validation.user.UserStatus;
import com.fsoft.fsa.kindergarten.model.dto.page.PageResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserDTO getUser(int userId);

    ParentDTO getParent(int userId);

    void createUser(CreateUserForm user) throws MessagingException, IOException, ParseException;

    PageResponse<?> getListUser(int page, int size, String sortBy, String search);

    PageResponse<?> getListParent(int page, int size, String sortBy, String search);

    String getUserName(String fullname);

    String getUserNameFromEdit(String fullname, Integer userId);

    void updateUser(int userId, UserForm userForm) throws ParseException;

    void changeStatus(int userId, UserStatus status);

    boolean isUserExistsByEmail(String email);

    boolean isUserExistsByUsername(String username);

    boolean isEmailExist(String email);

    void forgotPassword(String email) throws MessagingException, IOException;

    void isValidToken(String token);

    void resetPassword(String token, ResetPasswordForm passwords);

    void deleteUser(int userId);

    List<UserStatus> getUserStatuses();

    void register(RegisterForm registerForm);

    void updateAvatar(MultipartFile avatarFile);

    void updateParent(int parentId, UpdateParentForm parentForm, MultipartFile avatarFile) throws ParseException;

    User getUserById(int id);

    void changePassword(int id, ChangePasswordForm changePasswordForm);

    ParentDetailDTO getParentDetail(int parentId);

    Optional<User> existUser(int userId);
}
