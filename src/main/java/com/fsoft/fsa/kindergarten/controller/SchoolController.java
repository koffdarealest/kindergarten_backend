package com.fsoft.fsa.kindergarten.controller;

import com.fsoft.fsa.kindergarten.model.dto.page.PageResponse;
import com.fsoft.fsa.kindergarten.model.dto.school.*;
import com.fsoft.fsa.kindergarten.model.form.school.CreateSchoolForm;
import com.fsoft.fsa.kindergarten.model.form.school.OldImagesUpdateForm;
import com.fsoft.fsa.kindergarten.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/school")
@Validated
@Log4j2
@Tag(name = "School Controller")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SchoolController {

    private final SchoolService schoolService;
    private final EducationMethodService educationMethodService;
    private final FacilitiesService facilitiesService;
    private final UtilitiesService utilitiesService;
    private final SchoolTypeService schoolTypeService;
    private final SchoolAgeService schoolAgeService;
    private final RatingService ratingService;
    private final FeedbackService feedbackService;


    @Operation(summary = "Get list school", description = "API get list school have search and pagination")
    @GetMapping("/list")
    public PageResponse<?> getAllSchool(@RequestParam(defaultValue = "1") int pageNo,
                                        @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                        @RequestParam(name = "search", required = false) String search,
                                        @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy
    ) {
        log.info("Request get all school sortBy = " + sortBy);
        return schoolService.getListSchool(pageNo, pageSize, search, sortBy);
    }

    @Operation(summary = "Get list school", description = "API get list school have search and pagination")
    @GetMapping("/filter")
    public PageResponse<?> filterSchool(@RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
                                        @RequestParam(value = "size", defaultValue = "10") int size,
                                        @RequestParam(value = "sortBy", required = false) String sortBy,
                                        @RequestParam(name = "search", required = false) String search,
                                        @RequestParam(name = "provine", required = false) String province,
                                        @RequestParam(name = "city", required = false) String city,
                                        @RequestParam(required = false) Integer schoolType,
                                        @RequestParam(required = false) Integer schoolAge,
                                        @RequestParam(required = false) @Min(1000000) Integer feeFrom,
                                        @RequestParam(required = false) @Max(20000000) Integer feeTo,
                                        @RequestParam(required = false) Integer[] facilities,
                                        @RequestParam(required = false) Integer[] utilities
    ) {
        log.info("filter school");
        Pageable pageable = PageRequest.of(page - 1, size);
        return schoolService.filterSchool(pageable, sortBy, search, province, city, schoolType, schoolAge,
                feeFrom, feeTo, facilities, utilities);
    }

    @Operation(summary = "Get rating school", description = "API get rating of school about criteria")
    @GetMapping("/rating/{schoolId}")
    public ResponseEntity<Map<String, Object>> getRatingsByCriteriaIdAndSchoolId(
            @PathVariable Integer schoolId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        Map<String, Object> result = ratingService.getAverageRatingsBySchoolIdAndDateRange(schoolId, startDate, endDate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/feedback/{schoolId}")
    public ResponseEntity<Map<String, Object>> getFilteredFeedbacks(
            @PathVariable Integer schoolId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(defaultValue = "0,1,2,3,4,5") int[] ratingRange,
            @RequestParam(defaultValue = "1") int limit) {
        Map<String, Object> result = feedbackService.getFeedbacksBySchoolAndAverageRating(schoolId, startDate,
                endDate, ratingRange, limit);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/public/feedback/{schoolId}")
    public PageResponse<?> getFilteredFeedbacks(
            @PathVariable Integer schoolId,
            @RequestParam(defaultValue = "0,1,2,3,4,5") int[] ratingRange,
            @RequestParam(defaultValue = "1") @Min(1) int pageNo,
            @RequestParam(defaultValue = "10") int pageSize
            ) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return feedbackService.getFeedbacksBySchool(
                schoolId, pageable, ratingRange);
    }

    @Operation(summary = "Get school", description = "API get school by id")
    @GetMapping("/detail/{schoolId}")
    public SchoolDetailDTO getSchoolById(@PathVariable int schoolId) {
        return schoolService.getSchoolDetailById(schoolId);
    }

    @Operation(summary = "Update school", description = "Update school info")
    @PutMapping("/{schoolId}")
    public String updateSchool(@PathVariable int schoolId,
                               @Valid @RequestPart("data") CreateSchoolForm schoolForm,
                               @RequestPart(value = "images", required = false) List<MultipartFile> images,
                               @RequestPart(value = "oldImages", required = false) List<OldImagesUpdateForm> oldImagesUpdateForm)
            throws MessagingException, IOException {
        schoolService.updateSchool(schoolId, schoolForm, images, oldImagesUpdateForm);
        return "update user success";
    }

    @Operation(summary = "Change School status", description = "Changing school status")
    @PatchMapping("{schoolId}")
    public String changeSchoolStatus(
            @Min(1) @PathVariable int schoolId,
            @Min(1) @RequestParam int statusId
    ) throws MessagingException, IOException {
        log.info("Change School status");
        schoolService.changeStatusSchool(schoolId, statusId);
        return "School status change success";
    }

    @Operation(summary = "Add new school", description = "Add new school")
    @PostMapping("/create")
    public String createSchool(
            @Valid @RequestPart("data") CreateSchoolForm createSchoolForm,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "oldImages", required = false) List<OldImagesUpdateForm> oldImagesUpdateForms) throws MessagingException, IOException {
        log.info("Create new school");
        schoolService.createSchool(createSchoolForm, images, oldImagesUpdateForms);
        return "Create school success";
    }

    @Operation(summary = "Save draft school", description = "Save draft school")
    @PostMapping("/draft")
    public String saveDraft(@RequestPart("data") CreateSchoolForm createSchoolForm,
                            @RequestPart(value = "images", required = false) List<MultipartFile> images,
                            @RequestPart(value = "oldImages", required = false) List<OldImagesUpdateForm> oldImagesUpdateForms) {
        log.info("Save draft school");
        schoolService.saveDraftSchool(createSchoolForm, images, oldImagesUpdateForms);
        return "Save draft school success";
    }

    @Operation(summary = "Get all education method", description = "Get all education method for add new school")
    @GetMapping("/education-methods")
    public List<EducationMethodDTO> getAllEducationMethod() {
        log.info("Get all education method");
        return educationMethodService.getAllDTO();
    }

    @Operation(summary = "Get all facilities", description = "Get all facilities for add new school")
    @GetMapping("/facilities")
    public List<FacilitiesDTO> getAllFacilities() {
        log.info("Get all facilities");
        return facilitiesService.getAllDTO();
    }

    @Operation(summary = "Get all utilities", description = "Get all utilities for add new school")
    @GetMapping("/utilities")
    public List<UtilitiesDTO> getAllUtilities() {
        log.info("Get all utilities");
        return utilitiesService.getAllDTO();
    }

    @Operation(summary = "Get all school type", description = "Get all school type")
    @GetMapping("/type")
    public List<SchoolTypeDTO> GetSchoolType() {
        log.info("Get all school type");
        return schoolTypeService.getAllDTO();
    }

    @Operation(summary = "Get all Child-receiving age", description = "Get all Child-receiving age")
    @GetMapping("/age")
    public List<SchoolAgeDTO> GetSchoolAge() {
        log.info("Get all Child-receiving age");
        return schoolAgeService.getAllDTO();
    }

    @Operation(summary = "Get all school publish", description = "Get all list school has status is published")
    @GetMapping("/published")
    public List<SchoolDTO> getSchoolPublished(){
        log.info("Get All School has status is Published");
        return schoolService.getListSchoolPublished();
    }

    @Operation(summary = "Get school current learning", description = "Get school is learning")
    @GetMapping("/currentSchool")
    public ResponseEntity<?> getCurrentSchool(){
        log.info("Get All School are learning has status is Published");
        return schoolService.getCurrentSchool();
    }


    @Operation(summary = "Get school current learning", description = "Get school is learning")
    @GetMapping("/previousSchool")
    public PageResponse<?> getPreviousSchool(@RequestParam(defaultValue = "1") int pageNo,
                                               @RequestParam(name = "pageSize", defaultValue = "10") int pageSize){
        log.info("Get All School have learned has status is Published");
        return schoolService.getPreviousSchool(pageNo, pageSize);
    }
}
