package com.fsoft.fsa.kindergarten.service.imp;

import com.fsoft.fsa.kindergarten.config.exception.NoAuthorityException;
import com.fsoft.fsa.kindergarten.config.exception.ResourceNotFoundException;
import com.fsoft.fsa.kindergarten.config.exception.StatusChangeException;
import com.fsoft.fsa.kindergarten.model.dto.page.PageResponse;
import com.fsoft.fsa.kindergarten.model.dto.page.RequestPageResponse;
import com.fsoft.fsa.kindergarten.model.dto.rating.RatingSummary;
import com.fsoft.fsa.kindergarten.model.dto.request.MyRequestDTO;
import com.fsoft.fsa.kindergarten.model.dto.request.RequestDTO;
import com.fsoft.fsa.kindergarten.model.dto.school.SchoolAgeDTO;
import com.fsoft.fsa.kindergarten.model.dto.school.SchoolSummaryDTO;
import com.fsoft.fsa.kindergarten.model.entity.Request;
import com.fsoft.fsa.kindergarten.model.entity.School;
import com.fsoft.fsa.kindergarten.model.entity.User;
import com.fsoft.fsa.kindergarten.model.form.request.RequestForm;
import com.fsoft.fsa.kindergarten.repository.*;
import com.fsoft.fsa.kindergarten.repository.specification.RequestSpecification;
import com.fsoft.fsa.kindergarten.model.validation.request.RequestStatus;
import com.fsoft.fsa.kindergarten.model.validation.user.UserStatus;
import com.fsoft.fsa.kindergarten.service.RequestService;
import com.fsoft.fsa.kindergarten.service.SchoolService;
import com.fsoft.fsa.kindergarten.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImp implements RequestService {

    private final IRequestRepository requestRepository;
    private final JwtService jwtService;
    private final IUserRepository userRepository;
    private final ISchoolRepository schoolRepository;
    private final UserService userService;
    private final SchoolService schoolService;
    private final IRatingRepository ratingRepository;
    private final SchoolAgeServiceImp schoolAgeService;

    @Override
    @Cacheable(value = "requestList")
    public PageResponse<?> getListRequest(int page, int size, String search, String sortBy) {
        return getPageResponseWithSpecification(page, size, search, sortBy, true);
    }

    @Override
    @Cacheable(value = "reminder")
    public PageResponse<?> getListRequestReminder(int page, int size, String search, String sortBy) {
        return getPageResponseWithSpecification(page, size, search, sortBy, false);
    }

    @Override
    @Cacheable(value = "myRequest", key = "#pageable.pageNumber + '_' + #pageable.pageSize + '_'+@jwtService.getIdFromToken()")
    public RequestPageResponse<?> getMyRequest(Pageable pageable) {
        Integer activeUserId = jwtService.getIdFromToken();
        User userLogin = userRepository.findById(activeUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Specification requestByUserSpec = RequestSpecification.statusRequestOfUser(userLogin.getId());
        Page<Request> requests = requestRepository.findAll(requestByUserSpec, pageable);

        List<MyRequestDTO> requestDTOs = requests.stream()
                .map(request -> {
                    List<RatingSummary> ratingSummaries = ratingRepository.findAverageRatingsBySchoolId(request.getSchool().getId());
                    double totalRatingValue = ratingSummaries.stream()
                            .mapToDouble(RatingSummary::getAverageRatingValue)
                            .sum();
                    int numberOfCriteria = ratingSummaries.size();
                    double averageRating = numberOfCriteria > 0 ? totalRatingValue / numberOfCriteria : 0.0;
                    int countRating = ratingRepository.countUserRating(request.getSchool().getId());
                    School school = schoolRepository.findById(request.getSchool().getId())
                            .orElseThrow(() -> new ResourceNotFoundException("School not found"));
                    SchoolAgeDTO schoolAgeDTO = schoolAgeService.convertToDTO(school.getAge());

                    return MyRequestDTO.builder()
                            .requestId(request.getId())
                            .fullName(request.getFullName())
                            .email(request.getEmail())
                            .phone(request.getPhone())
                            .question(request.getQuestion())
                            .status(request.getStatus())
                            .requestDate(request.getCreatedAt())
                            .school(SchoolSummaryDTO.builder()
                                    .id(school.getId())
                                    .name(school.getName())
                                    .addressLine(school.getAddressLine())
                                    .ward(school.getWard())
                                    .district(school.getDistrict())
                                    .city(school.getCity())
                                    .feeTo(school.getFeeTo())
                                    .feeFrom(school.getFeeFrom())
                                    .schoolAge(schoolAgeDTO)
                                    .averageRating(averageRating)
                                    .totalRating(countRating)
                                    .build())
                            .build();
                })
                .collect(Collectors.toList());

        return RequestPageResponse.builder()
                .pageNo(pageable.getPageNumber() + 1)
                .pageSize(pageable.getPageSize())
                .totalPage(requests.getTotalPages())
                .totalElement(requests.getTotalElements())
                .openElement(getNumberRequestOfStatus("Open"))
                .item(requestDTOs)
                .build();
    }

    private PageResponse<?> getPageResponseWithSpecification(int page, int size, String search, String sortBy, boolean isReminder) {
        Integer activeUserId = jwtService.getIdFromToken();
        User userLogin = userService.getUserById(activeUserId);
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.fromString("desc"), "createdAt"));
        Specification<Request> specification;

        //check list is request reminders or just request
        if (isReminder) {
            specification = RequestSpecification.containsTextInEmailNamePhoneOrStatus(search);
        } else {
            specification = RequestSpecification.containsTextInEmailNameOrPhone(search);
        }

        //check school owner to only see requests from their school
        if (userLogin.getRole().getId() == 3) {
            specification = specification.and(RequestSpecification.belongToUser(activeUserId));
        }
        return getPageResponseForRequest(page, size, pageable, specification);
    }

    private PageResponse<?> getPageResponseForRequest(int page, int size, Pageable pageable, Specification<Request> specification) {
        Page<Request> requests = requestRepository.findAll(specification, pageable);

        List<RequestDTO> res = requests.stream().map(request -> RequestDTO.builder()
                .id(request.getId())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .question(request.getQuestion())
                .status(request.getStatus())
                .schoolName(request.getSchool().getName())
                .build()
        ).toList();

        return PageResponse.builder()
                .pageNo(page)
                .pageSize(size)
                .totalElement(requests.getTotalElements())
                .totalPage(requests.getTotalPages())
                .item(res)
                .build();
    }

    @Override
    public RequestDTO getRequest(int requestId) {
        int creatingUserId = jwtService.getIdFromToken();
        Request request = getRequestById(requestId);

        //check user login in system is school owner just see request to their school
        if (jwtService.getRoleFromToken().equals("SCHOOL_OWNER")) {
            User userLogin = userService.getUserById(creatingUserId);
            if (userLogin.getId() != request.getSchool().getSchoolOwnerId())
                throw new NoAuthorityException("No authority!");
        }

        return RequestDTO.builder()
                .id(requestId)
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .question(request.getQuestion())
                .status(request.getStatus())
                .schoolName(request.getSchool().getName())
                .build();
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "requestList", allEntries = true),
                    @CacheEvict(value = "myRequest", allEntries = true),
                    @CacheEvict(value = "reminder", allEntries = true),
                    @CacheEvict(value = "request", key = "#requestId")
            }
    )
    public void changeStatus(int requestId, RequestStatus status) {
        int creatingUserId = jwtService.getIdFromToken();
        Request request = getRequestById(requestId);

        //check user login in system is school owner just change status request to their school
        if (jwtService.getRoleFromToken().equals("SCHOOL_OWNER")) {
            User userLogin = userService.getUserById(creatingUserId);
            if (userLogin.getId() != request.getSchool().getSchoolOwnerId())
                throw new NoAuthorityException("No authority!");
        }
        //check status is closed to can't change
        if (request.getStatus().equals(RequestStatus.Closed.toString())) {
            throw new StatusChangeException("Can't Change status when status is closed");
        }

        request.setStatus(status.toString());
        request.setUpdatedBy(creatingUserId);
        requestRepository.save(request);
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "requestList", allEntries = true),
                    @CacheEvict(value = "reminder", allEntries = true),
                    @CacheEvict(value = "request", allEntries = true),
                    @CacheEvict(value = "myRequest", allEntries = true)
            }
    )
    public void createRequest(RequestForm requestForm) {
        int creatingUserId = jwtService.getIdFromToken();
        User user = userService.getUserById(creatingUserId);
        //check user is active
        if (user.getStatus().equals(UserStatus.Inactive.toString()))
            throw new ResourceNotFoundException("User not active");
        School school = schoolService.getSchoolById(requestForm.getSchoolId());

        Request requests = Request.builder()
                .fullName(requestForm.getFullName())
                .email(requestForm.getEmail())
                .phone(requestForm.getPhone())
                .question(requestForm.getQuestion())
                .status(RequestStatus.Open.toString())
                .school(school)
                .user(user)
                .build();
        requests.setCreatedBy(creatingUserId);
        requestRepository.save(requests);
    }

    @Override
    public Integer getNumberRequestOfStatus(String status) {
        Integer activeUserId = jwtService.getIdFromToken();
        User userLogin = userRepository.findById(activeUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Specification<Request> spec = RequestSpecification.getRequestsByStatusForUser(userLogin.getId(), status);
        List<Request> numOpen = requestRepository.findAll(spec);
        return numOpen.size();
    }

    public Request getRequestById(int requestId) {
        return requestRepository.findById(requestId).orElseThrow(() -> new ResourceNotFoundException("Request not found"));
    }
}
