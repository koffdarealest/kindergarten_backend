package com.fsoft.fsa.kindergarten.service;

import com.fsoft.fsa.kindergarten.model.dto.page.PageResponse;
import com.fsoft.fsa.kindergarten.model.dto.school.SchoolDTO;
import com.fsoft.fsa.kindergarten.model.dto.school.SchoolDetailDTO;
import com.fsoft.fsa.kindergarten.model.entity.School;
import com.fsoft.fsa.kindergarten.model.entity.SchoolStatus;
import com.fsoft.fsa.kindergarten.model.form.school.CreateSchoolForm;
import com.fsoft.fsa.kindergarten.model.form.school.OldImagesUpdateForm;
import jakarta.mail.MessagingException;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface SchoolService {
    PageResponse<?> getListSchool(int page, int size, String search, String sortBy);

    PageResponse<?> filterSchool(Pageable page, String sortBy, String search, String province, String city, Integer  schoolType,
                                 Integer schoolAge, Integer feeFrom, Integer feeTo, Integer[] facilities, Integer[] utilities);

    List<SchoolDTO> getListSchoolPublished();

    ResponseEntity<?> getCurrentSchool();

    PageResponse<?> getPreviousSchool(int pageNo, int pageSize);

    SchoolDetailDTO getSchoolDetailById(int schoolId);

    void updateSchool(int schoolId,
                      CreateSchoolForm schoolForm,
                      List<MultipartFile> images,
                      List<OldImagesUpdateForm> oldImagesUpdateForms)
            throws MessagingException, IOException;

    void changeStatusSchool(int schoolId, int statusId) throws MessagingException, IOException;

    void SendEmail(SchoolStatus schoolStatus, School school) throws MessagingException, IOException;

    void createSchool(CreateSchoolForm createSchoolForm,
                      List<MultipartFile> images,
                      List<OldImagesUpdateForm> oldImagesUpdateForms) throws MessagingException, IOException;

    void saveDraftSchool(CreateSchoolForm createSchoolForm,
                         List<MultipartFile> images,
                         List<OldImagesUpdateForm> oldImagesUpdateForms);

    List<School> getSchoolByParent(int parentId);

    School getSchoolById(int id);

    Optional<School> checkHaveSchoolParentEnroll(int schoolId);

    boolean checkParentLearningSchool(int schoolId);
}
