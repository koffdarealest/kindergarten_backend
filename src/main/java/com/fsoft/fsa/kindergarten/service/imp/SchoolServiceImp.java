package com.fsoft.fsa.kindergarten.service.imp;

import com.fsoft.fsa.kindergarten.config.exception.FeeInvalidException;
import com.fsoft.fsa.kindergarten.config.exception.NoAuthorityException;
import com.fsoft.fsa.kindergarten.config.exception.ResourceNotFoundException;
import com.fsoft.fsa.kindergarten.model.dto.page.PageResponse;
import com.fsoft.fsa.kindergarten.model.dto.rating.RatingSummary;
import com.fsoft.fsa.kindergarten.model.dto.school.*;
import com.fsoft.fsa.kindergarten.model.entity.*;
import com.fsoft.fsa.kindergarten.model.form.school.CreateSchoolForm;
import com.fsoft.fsa.kindergarten.config.exception.StatusChangeException;
import com.fsoft.fsa.kindergarten.model.entity.School;
import com.fsoft.fsa.kindergarten.model.entity.SchoolStatus;
import com.fsoft.fsa.kindergarten.model.entity.User;
import com.fsoft.fsa.kindergarten.model.form.school.OldImagesUpdateForm;
import com.fsoft.fsa.kindergarten.repository.IRatingRepository;
import com.fsoft.fsa.kindergarten.repository.ISchoolRepository;
import com.fsoft.fsa.kindergarten.repository.IUserRepository;
import com.fsoft.fsa.kindergarten.repository.specification.SchoolSpecification;
import com.fsoft.fsa.kindergarten.service.*;
import com.fsoft.fsa.kindergarten.utils.BaseMapper;
import com.fsoft.fsa.kindergarten.utils.SchoolPropertiesForm;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SchoolServiceImp implements SchoolService {

    private final ISchoolRepository schoolRepository;
    private final SchoolStatusService statusService;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final IUserRepository userRepository;
    private final SchoolAgeService schoolAgeService;
    private final SchoolTypeService schoolTypeService;
    private final EducationMethodService educationMethodService;
    private final FacilitiesService facilitiesService;
    private final UtilitiesService utilitiesService;
    private final FileStorageService fileStorageService;
    private final BaseMapper baseMapper;
    private final IRatingRepository ratingRepository;

    @Value("${create.email.subject}")
    private String createEmailSubject;
    @Value("${reminder.email.admin}")
    private String emailAdmin;
    @Value("${reminder.submitted.link}")
    private String submitSchoolLink;

    /*
       1 - Saved
       2 - Submitted
       3 - Approved
       4 - Rejected
       5 - Published
       6 - Unpublished
       7 - Deleted
    */

    //changing school status with each status and sending email
    private void changeStatus(School school, SchoolStatus status) throws MessagingException, IOException {
        school.setStatus(status);
        schoolRepository.save(school);
        SendEmail(status, school);
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "schoolList", allEntries = true),
                    @CacheEvict(value = "school", key = "#schoolId"),
                    @CacheEvict(value = "schoolFilter", allEntries = true)
            }
    )
    public void changeStatusSchool(int schoolId, int statusId) throws MessagingException, IOException {
        School school = getSchoolById(schoolId);
        SchoolStatus status = getSchoolStatusById(statusId);
        int creatingUserId = jwtService.getIdFromToken();
        User Admin = userRepository.findById(creatingUserId).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
        school.setUpdatedBy(creatingUserId);
        //check account is not Parent role
        if (Admin.getRole().getId() == 2) throw new ResourceNotFoundException("Admin/School owner Role not found");

        switch (statusId) {
            // 1 - Saved
            case 1:
                //check this school has status is rejected
                if (school.getStatus().getId() == 4) throw new StatusChangeException("This School has Rejected!");
                    //check this school has status is deleted
                else if (school.getStatus().getId() == 7) throw new StatusChangeException("This School has Deleted!");
                school.setStatus(status);
                schoolRepository.save(school);
                break;
            // 2 - Submitted
            case 2:
                //check this school has status is submitted
                if (school.getStatus().getId() == 2) throw new StatusChangeException("This School has Submitted!");
                    //check this school has status is rejected
                else if (school.getStatus().getId() == 4) throw new StatusChangeException("This School has Rejected!");
                    //check this school has status is deleted
                else if (school.getStatus().getId() == 7) throw new StatusChangeException("This School has Deleted!");
                changeStatus(school, status);
                break;
            // 3 - Approved
            case 3:
                //check account logged is ADMIN and School status present is Submitted
                if (Admin.getRole().getId() == 1 && school.getStatus().getId() == 2) {
                    changeStatus(school, status);
                } // check school has status is Approved so can't change
                else if (school.getStatus().getId() == 3) {
                    throw new StatusChangeException("This School has Approved!");
                } else {
                    throw new StatusChangeException("Approve must requires Admin role and school status to be Submitted.");
                }
                break;
            // 4 - Rejected
            case 4:
                //check account logged is ADMIN and School status present is Submitted
                if (Admin.getRole().getId() == 1 && school.getStatus().getId() == 2) {
                    changeStatus(school, status);
                } // check school has status is Rejected so can't change
                else if (school.getStatus().getId() == 4) {
                    throw new StatusChangeException("This School has Rejected!");
                } else {
                    throw new StatusChangeException("Reject must requires Admin role and school status to be Submitted.");
                }
                break;
            // 5 - Published
            case 5:
                //School status present is Approved or Unpublished
                if (school.getStatus().getId() == 3 || school.getStatus().getId() == 6) {
                    changeStatus(school, status);
                } // check school has status is Published so can't change
                else if (school.getStatus().getId() == 5) {
                    throw new StatusChangeException("This School has Published!");
                } else {
                    throw new StatusChangeException("Publish must requires school status to be Approved or Unpublished.");
                }
                break;
            // 6 - Unpublished
            case 6:
                //School status present is Published
                if (school.getStatus().getId() == 5) {
                    school.setStatus(status);
                    schoolRepository.save(school);
                } // check school has status is Unpublished so can't change
                else if (school.getStatus().getId() == 6) {
                    throw new StatusChangeException("This School has Unpublished!");
                } else {
                    throw new StatusChangeException("Unpublish must requires school status to be Published.");
                }
                break;
            // 7 - Deleted
            case 7:
                //check account logged is SCHOOL_OWNER and School status present is Approved
                if (Admin.getRole().getId() == 3 && school.getStatus().getId() == 3) {
                    throw new StatusChangeException("Delete must requires Admin role when school status is Approved.");
                } // check school has status is Deleted so can't change
                else if (school.getStatus().getId() == 7) {
                    throw new StatusChangeException("This School has Deleted!");
                } else {
                    school.setStatus(status);
                    schoolRepository.save(school);
                }
                break;
            default:
                throw new StatusChangeException("Invalid status change request.");
        }
    }


    @Override
    public void SendEmail(SchoolStatus schoolStatus, School school) throws MessagingException, IOException {
        Map<String, String> map = new HashMap<>();
        String subject;
        int statusId = schoolStatus.getId();
        User schoolOwner = userRepository.findById(school.getSchoolOwnerId()).orElseThrow(
                () -> new ResourceNotFoundException("School Owner not found!"));
        String schoolOwnerEmail = schoolOwner.getEmail();
        map.put("schoolOwnerName", schoolOwner.getFullName());
        map.put("url", submitSchoolLink + school.getId());

        switch (statusId) {
            //2 - Submitted
            case 2:
                subject = createEmailSubject + " Review School " + school.getName();
                emailService.sendEmail(emailAdmin, subject, map, "/Templates/SubmitSchool.html");
                break;
            //3 - Approved
            case 3:
                subject = createEmailSubject + " Approved School " + school.getName();
                emailService.sendEmail(schoolOwnerEmail, subject, map, "/Templates/ApproveSchool.html");
                break;
            //  4 - Rejected
            case 4:
                subject = createEmailSubject + " Rejected School " + school.getName();
                emailService.sendEmail(schoolOwnerEmail, subject, map, "/Templates/RejectSchool.html");
                break;
            //5 - Published
            case 5:
                subject = createEmailSubject + " Published School " + school.getName();
                User userLogin = userRepository.findById(jwtService.getIdFromToken()).orElseThrow(() -> new ResourceNotFoundException("User Login not found!"));
                map.put("ChangeStatusBy", userLogin.getFullName());
                map.put("SchoolName", school.getName());
                emailService.sendEmail(schoolOwnerEmail, subject, map, "/Templates/PublishSchool.html");
                break;
            default:
                throw new StatusChangeException("Invalid Status School request.");
        }
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "schoolList", allEntries = true),
                    @CacheEvict(value = "school", key = "#createSchoolForm.id", condition = "#createSchoolForm.id != null"),
                    @CacheEvict(value = "schoolFilter", allEntries = true)
            }
    )
    public void createSchool(CreateSchoolForm createSchoolForm,
                             List<MultipartFile> images,
                             List<OldImagesUpdateForm> oldImagesUpdateForms) throws MessagingException, IOException {
        if (createSchoolForm.getFeeFrom() >= createSchoolForm.getFeeTo()) {
            throw new FeeInvalidException("From must be smaller than To");
        }
        createSchoolForm.setImages(Objects.requireNonNullElseGet(images, ArrayList::new));
        String role = jwtService.getRoleFromToken();
        int creatingUserId = jwtService.getIdFromToken();
        SchoolStatus savedStatus;
        if (role.equals("SCHOOL_OWNER")) { // default status of school by user who create school
            savedStatus = getSchoolStatusById(2);  //2 - Submitted
        } else if (role.equals("ADMIN")) {
            savedStatus = getSchoolStatusById(3);  //3 - Approved
        } else {
            throw new NoAuthorityException("No authority!");
        }
        School school;
        Integer id = createSchoolForm.getId();
        if (id != null) {
            school = getSchoolById(id);
            if (school.getStatus().getId() != 1) {
                throw new StatusChangeException("This School can't submit");
            }
            updateSchoolObject(school, createSchoolForm, savedStatus);
            List<SchoolImage> oldImages = getOldSchoolImages(oldImagesUpdateForms, school);
            mergeSchoolImages(school, oldImages);
            school.setUpdatedBy(creatingUserId);
            schoolRepository.save(school);
        } else {
            school = buildSchool(createSchoolForm, savedStatus);
            school.setCreatedBy(creatingUserId);
            school.setSchoolOwnerId(creatingUserId);
            school = schoolRepository.save(school);
            List<SchoolImage> imagesPath = storeImagesPath(createSchoolForm.getImages(), school);
            school.setImages(imagesPath);
            schoolRepository.save(school);
        }
        SendEmail(savedStatus, school);
    }

    private SchoolAge getSchoolAge(int ageId) {
        return schoolAgeService.getById(ageId);
    }

    private SchoolType getSchoolType(int typeId) {
        return schoolTypeService.getById(typeId);
    }

    private EducationMethod getEducationMethod(int methodId) {
        return educationMethodService.getById(methodId);
    }

    private Set<Facilities> getFacilities(Set<Integer> facilities) {
        return facilitiesService.getFacilitiesByIds(facilities);
    }

    private Set<Utilities> getUtilities(Set<Integer> utilities) {
        return utilitiesService.getUtilitiesByIds(utilities);
    }

    private SchoolStatus getSchoolStatusById(int id) {
        return statusService.getStatusById(id);
    }

    private SchoolPropertiesForm getSchoolProperties(CreateSchoolForm createSchoolForm) {
        SchoolAge schoolAge = Optional.ofNullable(createSchoolForm.getAge()).map(this::getSchoolAge).orElse(null);
        SchoolType schoolType = Optional.ofNullable(createSchoolForm.getType()).map(this::getSchoolType).orElse(null);
        EducationMethod educationMethod = Optional.ofNullable(createSchoolForm.getMethod()).map(this::getEducationMethod).orElse(null);
        Set<Facilities> facilities = Optional.ofNullable(createSchoolForm.getFacilities()).map(this::getFacilities).orElse(null);
        Set<Utilities> utilities = Optional.ofNullable(createSchoolForm.getUtilities()).map(this::getUtilities).orElse(null);
        return new SchoolPropertiesForm(schoolAge, schoolType, educationMethod, facilities, utilities);
    }

    private School buildSchool(CreateSchoolForm createSchoolForm, SchoolStatus status) {
        SchoolPropertiesForm properties = getSchoolProperties(createSchoolForm);

        return School.builder()
                .name(createSchoolForm.getName())
                .phone(createSchoolForm.getPhone())
                .introduction(createSchoolForm.getIntroduction())
                .feeFrom(createSchoolForm.getFeeFrom())
                .feeTo(createSchoolForm.getFeeTo())
                .age(properties.getSchoolAge())
                .type(properties.getSchoolType())
                .status(status)
                .method(properties.getEducationMethod())
                .facilities(properties.getFacilities())
                .utilities(properties.getUtilities())
                .addressLine(createSchoolForm.getAddressLine())
                .ward(createSchoolForm.getWard())
                .district(createSchoolForm.getDistrict())
                .city(createSchoolForm.getCity())
                .email(createSchoolForm.getEmail())
                .userSchools(new ArrayList<>())
                .build();
    }

    private List<SchoolImage> storeImagesPath(List<MultipartFile> images, School school) {
        if (images == null || images.isEmpty()) {
            return new ArrayList<>();
        }
        if (images.get(0).isEmpty()) {
            return new ArrayList<>();
        }
        List<SchoolImage> imagesPath = new ArrayList<>();
        String prefix = "school_" + school.getId();
        for (MultipartFile image : images) {
            String fileName = fileStorageService.storeFile(prefix, image);
            SchoolImage schoolImage = new SchoolImage();
            schoolImage.setImagePath(fileName);
            schoolImage.setSchool(school);
            imagesPath.add(schoolImage);
        }
        return imagesPath;
    }

    private List<SchoolImage> getOldSchoolImages(List<OldImagesUpdateForm> oldImagesUpdateForms, School school) {
        if (oldImagesUpdateForms == null || oldImagesUpdateForms.isEmpty()) {
            return new ArrayList<>();
        }
        List<SchoolImage> images = new ArrayList<>();
        for (OldImagesUpdateForm oldImage : oldImagesUpdateForms) {
            SchoolImage schoolImage = baseMapper.convert(oldImage, SchoolImage.class);
            schoolImage.setSchool(school);
            images.add(schoolImage);
        }
        return images;
    }

    private void mergeSchoolImages(School school, List<SchoolImage> oldImages) {
        List<SchoolImage> images = school.getImages();
        images.addAll(oldImages);
        school.setImages(images);
    }

    @Override
    public School getSchoolById(int schoolId) {
        return schoolRepository.findById(schoolId).orElseThrow(() -> new ResourceNotFoundException("School not found"));
    }

    @Override
    public Optional<School> checkHaveSchoolParentEnroll(int shoolId) {
        User userLogin = userRepository.findById(jwtService.getIdFromToken()).orElseThrow(() -> new ResourceNotFoundException("User Login not found!"));
        return schoolRepository.findHaveSchoolParentEnroll(userLogin.getId(), shoolId);
    }

    @Override
    public boolean checkParentLearningSchool(int schoolId) {
        User userLogin = userRepository.findById(jwtService.getIdFromToken()).orElseThrow(() -> new ResourceNotFoundException("User Login not found!"));
        return schoolRepository.isLearning(userLogin.getId(), schoolId);
    }


    /**
     * @param schoolId is id of school want to get info
     * @return detail information of school include average rating and total rating
     */
    @Override
    @Cacheable(value = "school", key = "#schoolId")
    public SchoolDetailDTO getSchoolDetailById(int schoolId) {
        School school = schoolRepository.findById(schoolId).orElseThrow(() ->
                new ResourceNotFoundException("School not found"));
        SchoolDetailDTO schoolDetailDTO = baseMapper.convert(school, SchoolDetailDTO.class);
        double[] averageAndTotalRating = getAverageAndTotalRatingOfSchool(school.getId());
        schoolDetailDTO.setAverageRating(averageAndTotalRating[0]);
        schoolDetailDTO.setTotalRating((int) averageAndTotalRating[1]);
        return schoolDetailDTO;
    }

    /**
     * Retrieves a paginated list of schools based on search criteria and sorting.
     *
     * @param pageNo   The current page number (starting from 0).
     * @param pageSize The size of each page, i.e., number of items per page.
     * @param search   The search string used to filter schools based on email, name, phone, or status.
     * @param sortBy   The field used to sort the results.
     * @return A PageResponse object containing the list of schools and pagination information.
     */
    @Override
    @Cacheable(value = "schoolList", key = "@jwtService.getIdFromToken() +  '_' + #pageNo + '_' + #pageSize + '_' + #search + '_' + #sortBy")
    public PageResponse<?> getListSchool(int pageNo, int pageSize, String search, String sortBy) {
        Integer activeUserId = jwtService.getIdFromToken();
        User userLogin = userRepository.findById(activeUserId).orElseThrow(() ->
                new ResourceNotFoundException("User Login not found!"));
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize,
                Sort.by(Sort.Direction.fromString("desc"), sortBy));
        Specification<School> spec = SchoolSpecification.containsTextInEmailNamePhoneOrStatus(search);
        if (userLogin.getRole().getId() == 1) {
            //neu admin => xem all va chi xem save cua no
            spec = spec.and(SchoolSpecification.isNotSaveStatusOrOwnedByUser(userLogin.getId()));
        } else if (userLogin.getRole().getId() == 3) {
            //neu school owner chi xem dc tat ca trang thai cua no
            spec = spec.and(SchoolSpecification.belongsToUser(userLogin.getId()));
        }

        Page<School> schools = schoolRepository.findAll(spec, pageable);

        List<SchoolDTO> response = schools.stream().map(school -> SchoolDTO.builder()
                .id(school.getId())
                .schoolName(school.getName())
                .postedDate(school.getCreatedAt())
                .address(school.getAddressLine() + "," + school.getWard() + "," +
                        school.getDistrict() + "," + school.getCity())
                .status(baseMapper.convert(school.getStatus(), SchoolStatusDTO.class))
                .phone(school.getPhone())
                .email(school.getEmail())
                .build()
        ).toList();
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(schools.getTotalPages())
                .totalElement(schools.getTotalElements())
                .item(response)
                .build();
    }

    @Override
    @CachePut(value = "schoolPublished")
    public List<SchoolDTO> getListSchoolPublished() {
        Integer activeUserId = jwtService.getIdFromToken();
        User userLogin = userRepository.findById(activeUserId).orElseThrow(() ->
                new ResourceNotFoundException("User Login not found!"));

        Specification<School> spec = SchoolSpecification.belongsToPublished();
        //check user login is school owner
        if (userLogin.getRole().getId() == 3) {
            spec = spec.and(SchoolSpecification.belongsToUser(activeUserId));
        }
        List<School> schools = schoolRepository.findAll(spec);

        return schools.stream().map(school -> SchoolDTO.builder()
                .id(school.getId())
                .schoolName(school.getName())
                .postedDate(school.getCreatedAt())
                .address(school.getAddressLine() + "," + school.getWard() + "," + school.getDistrict() + "," + school.getCity())
                .status(baseMapper.convert(school.getStatus(), SchoolStatusDTO.class))
                .phone(school.getPhone())
                .email(school.getEmail())
                .build()
        ).toList();
    }

    /**
     * @return info of school parent register for children are learning, include info, rating of school
     */
    @Override
    public ResponseEntity<?> getCurrentSchool() {
        Integer activeUserId = jwtService.getIdFromToken();
        User userLogin = userRepository.findById(activeUserId).orElseThrow(() ->
                new ResourceNotFoundException("User Login not found!"));

        List<School> schools = schoolRepository.findCurrentSchoolByParentId(userLogin.getId());

        List<SchoolDetailWithRatingDTO> schoolDetailsWithRatings = new ArrayList<>();

        for (School school : schools) {
            double[] averageAndTotalRating = getAverageAndTotalRatingOfSchool(school.getId());

            SchoolDetailDTO schoolDetailDTO = baseMapper.convert(school, SchoolDetailDTO.class);
            schoolDetailDTO.setAverageRating(averageAndTotalRating[0]);
            schoolDetailDTO.setTotalRating((int) averageAndTotalRating[1]);

            Double rating = ratingRepository.findAverageRatingBySchoolIdAndUserId(school.getId(), userLogin.getId());
            SchoolDetailWithRatingDTO schoolDetailWithRatingDTO = SchoolDetailWithRatingDTO.builder()
                    .schoolDetail(schoolDetailDTO)
                    .rating(rating)
                    .build();
            schoolDetailsWithRatings.add(schoolDetailWithRatingDTO);
        }
        return ResponseEntity.ok(schoolDetailsWithRatings);
    }

    /**
     * @return info of school parent register for children have learned, include info, rating of school
     */
    @Override
    public PageResponse<?> getPreviousSchool(int pageNo, int pageSize) {
        Integer activeUserId = jwtService.getIdFromToken();
        User userLogin = userRepository.findById(activeUserId).orElseThrow(() ->
                new ResourceNotFoundException("User Login not found!"));
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        Page<School> schools = schoolRepository.findPreviousSchoolByParentId(userLogin.getId(), pageable);
        List<SchoolDetailWithRatingDTO> schoolDetailsWithRatings = new ArrayList<>();

        for (School school : schools) {
            double[] averageAndTotalRating = getAverageAndTotalRatingOfSchool(school.getId());

            SchoolDetailDTO schoolDetailDTO = baseMapper.convert(school, SchoolDetailDTO.class);
            schoolDetailDTO.setAverageRating(averageAndTotalRating[0]);
            schoolDetailDTO.setTotalRating((int) averageAndTotalRating[1]);

            Double rating = ratingRepository.findAverageRatingBySchoolIdAndUserId(school.getId(), userLogin.getId());
            SchoolDetailWithRatingDTO schoolDetailWithRatingDTO = SchoolDetailWithRatingDTO.builder()
                    .schoolDetail(schoolDetailDTO)
                    .rating(rating)
                    .build();
            schoolDetailsWithRatings.add(schoolDetailWithRatingDTO);
        }

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(schools.getTotalPages())
                .totalElement(schools.getTotalElements())
                .item(schoolDetailsWithRatings)
                .build();
    }

    /**
     * @param page       include page: is number start defaul is 0, size: is limit school display in a page
     * @param search     is a string search following school name
     * @param province   is province of school want to filter
     * @param city       is city of school want to filter
     * @param schoolType is type id of school type (optional)
     * @param schoolAge  is age id of school type (optional)
     * @param feeFrom    is fee min to pay for school
     * @param feeTo      is fee max to pay for school
     * @param facilities is array contain id of facility want to filter
     * @param utilities  is array contain id of utilities want to filter
     * @return info school have satisfied of info filter and info of page and sort by date or rating or fee
     */
    @Override
    @Cacheable(value = "schoolFilter", key = "#page.pageNumber + '_' + #page.pageSize + '_' + #sortBy + '_' + #search" +
            " + '_' + #province + '_' + #city + '_' + #schoolType + '_' + #schoolAge + '_' + #feeFrom + '_' " +
            "+ #feeTo + '_' + T(java.util.Arrays).toString(#facilities) + '_' + T(java.util.Arrays).toString(#utilities)")
    public PageResponse<?> filterSchool(Pageable page, String sortBy, String search, String province, String city,
                                        Integer schoolType, Integer schoolAge, Integer feeFrom, Integer feeTo,
                                        Integer[] facilities, Integer[] utilities) {
        Specification<School> spec = SchoolSpecification.filter(search, province, city, schoolType, schoolAge,
                feeFrom, feeTo, facilities, utilities);

        Sort sorting = Sort.by(Sort.Order.desc("id"));
        boolean isRating = false;
        if (sortBy != null) {
            switch (sortBy) {
                case "newest":
                    sorting = Sort.by(Sort.Order.desc("createdAt"));
                    break;
                case "highestFee":
                    sorting = Sort.by(Sort.Order.desc("feeTo"));
                    break;
                case "lowestFee":
                    sorting = Sort.by(Sort.Order.asc("feeFrom"));
                    break;
                default:
                    isRating = true;
                    break;
            }
        } else {
            isRating = true;
        }
        Pageable pageable;
        Page<School> schoolPage = null;
        List<School> schools = new ArrayList<>();

        if (isRating) {
            pageable = PageRequest.of(page.getPageNumber(), page.getPageSize());
            schools = schoolRepository.findAll(spec);
        } else {
            pageable = PageRequest.of(page.getPageNumber(), page.getPageSize(), sorting);
            schoolPage = schoolRepository.findAll(spec, pageable);
            schools = schoolPage.getContent();
        }

        List<SchoolDetailDTO> schoolDetailDTOS = new ArrayList<>();

        for (School school : schools) {
            SchoolDetailDTO schoolDetailDTO = baseMapper.convert(school, SchoolDetailDTO.class);
            double[] averageAndTotalRating = getAverageAndTotalRatingOfSchool(school.getId());
            schoolDetailDTO.setAverageRating(averageAndTotalRating[0]);
            schoolDetailDTO.setTotalRating((int) averageAndTotalRating[1]);
            schoolDetailDTOS.add(schoolDetailDTO);
        }
        if (isRating) {
            schoolDetailDTOS.sort((a, b) -> Double.compare(b.getAverageRating(), a.getAverageRating()));
            int start = Math.min(pageable.getPageNumber() * pageable.getPageSize(), schoolDetailDTOS.size());
            int end = Math.min(start + pageable.getPageSize(), schoolDetailDTOS.size());
            List<SchoolDetailDTO> paginatedList = new ArrayList<>(schoolDetailDTOS.subList(start, end));
            return PageResponse.builder()
                    .pageNo(pageable.getPageNumber() + 1)
                    .pageSize(pageable.getPageSize())
                    .totalPage((int) Math.ceil((double) schoolDetailDTOS.size() / pageable.getPageSize()))
                    .totalElement(schoolDetailDTOS.size())
                    .item(paginatedList)
                    .build();
        } else {
            return PageResponse.builder()
                    .pageNo(pageable.getPageNumber() + 1)
                    .pageSize(pageable.getPageSize())
                    .totalPage(schoolPage.getTotalPages())
                    .totalElement(schoolPage.getTotalElements())
                    .item(schoolDetailDTOS)
                    .build();
        }
    }

    private double[] getAverageAndTotalRatingOfSchool(int schoolId) {
        List<RatingSummary> ratingSummaries = ratingRepository.findAverageRatingsBySchoolId(schoolId);
        double totalRatingValue = ratingSummaries.stream()
                .mapToDouble(RatingSummary::getAverageRatingValue)
                .sum();
        int numberOfCriteria = ratingSummaries.size();
        double averageRating = numberOfCriteria > 0 ? totalRatingValue / numberOfCriteria : 0.0;
        int countRating = ratingRepository.countUserRating(schoolId);
        return new double[]{averageRating, countRating};
    }


    private void updateSchoolObject(School school, CreateSchoolForm schoolForm, SchoolStatus status) {
        SchoolPropertiesForm properties = getSchoolProperties(schoolForm);
        List<SchoolImage> imagesPath = storeImagesPath(schoolForm.getImages(), school);
        school.setName(schoolForm.getName())
                .setPhone(schoolForm.getPhone())
                .setIntroduction(schoolForm.getIntroduction())
                .setFeeFrom(schoolForm.getFeeFrom())
                .setFeeTo(schoolForm.getFeeTo())
                .setAge(properties.getSchoolAge())
                .setType(properties.getSchoolType())
                .setStatus(status)
                .setMethod(properties.getEducationMethod())
                .setFacilities(properties.getFacilities())
                .setUtilities(properties.getUtilities())
                .setAddressLine(schoolForm.getAddressLine())
                .setWard(schoolForm.getWard())
                .setDistrict(schoolForm.getDistrict())
                .setCity(schoolForm.getCity())
                .setEmail(schoolForm.getEmail())
                .updateImages(imagesPath)
        ;
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "schoolList", allEntries = true),
                    @CacheEvict(value = "school", key = "#schoolId"),
                    @CacheEvict(value = "schoolFilter", allEntries = true)
            }
    )
    public void updateSchool(int schoolId,
                             CreateSchoolForm schoolForm,
                             List<MultipartFile> images,
                             List<OldImagesUpdateForm> oldImagesUpdateForms) {
        School school = getSchoolById(schoolId);
        if (schoolForm.getFeeFrom() >= schoolForm.getFeeTo()) {
            throw new FeeInvalidException("From must be smaller than To");
        }
        String role = jwtService.getRoleFromToken();
        int creatingUserId = jwtService.getIdFromToken();
        if (school.getSchoolOwnerId() != creatingUserId) {
            throw new NoAuthorityException("No authority to update!");
        }
        schoolForm.setImages(Objects.requireNonNullElseGet(images, ArrayList::new));
        SchoolStatus savedStatus;
        if (role.equals("SCHOOL_OWNER")) { // default status of school by user who create school
            savedStatus = getSchoolStatusById(2);                   //2 - Submitted
        } else if (role.equals("ADMIN")) {
            savedStatus = getSchoolStatusById(3);                   //3 - Approved
        } else {
            throw new NoAuthorityException("No authority!");
        }
        updateSchoolObject(school, schoolForm, savedStatus);
        List<SchoolImage> oldImages = getOldSchoolImages(oldImagesUpdateForms, school);
        mergeSchoolImages(school, oldImages);
        school.setUpdatedBy(creatingUserId);
        schoolRepository.save(school);
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "schoolList", allEntries = true),
                    @CacheEvict(value = "school", key = "#createSchoolForm.id", condition = "#createSchoolForm.id != null"),
                    @CacheEvict(value = "schoolFilter", allEntries = true)
            }
    )
    public void saveDraftSchool(CreateSchoolForm createSchoolForm,
                                List<MultipartFile> images,
                                List<OldImagesUpdateForm> oldImagesUpdateForms) {
        int creatingUserId = jwtService.getIdFromToken();
        SchoolStatus savedStatus = getSchoolStatusById(1);
        School school;
        Integer id = createSchoolForm.getId();
        createSchoolForm.setImages(Objects.requireNonNullElseGet(images, ArrayList::new));
        if (id != null) {
            school = getSchoolById(id);
            if (school.getSchoolOwnerId() != creatingUserId) {
                throw new NoAuthorityException("No authority");
            }
            updateSchoolObject(school, createSchoolForm, savedStatus);
            List<SchoolImage> oldImages = getOldSchoolImages(oldImagesUpdateForms, school);
            mergeSchoolImages(school, oldImages);
            school.setUpdatedBy(creatingUserId);
            schoolRepository.save(school);
        } else {
            school = buildSchool(createSchoolForm, savedStatus);
            school.setCreatedBy(creatingUserId);
            school.setSchoolOwnerId(creatingUserId);
            schoolRepository.save(school);
            List<SchoolImage> imagesPath = storeImagesPath(createSchoolForm.getImages(), school);
            school.setImages(imagesPath);
            schoolRepository.save(school);
        }
    }

    @Override
    public List<School> getSchoolByParent(int parentId) {
        User user = userRepository.findById(parentId).orElseThrow(() -> new ResourceNotFoundException("Parent not found!"));
        return schoolRepository.findSchoolByParentId(user.getId());
    }


}
