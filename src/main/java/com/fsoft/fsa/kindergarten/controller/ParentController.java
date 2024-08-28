package com.fsoft.fsa.kindergarten.controller;

import com.fsoft.fsa.kindergarten.model.dto.page.PageResponse;
import com.fsoft.fsa.kindergarten.model.dto.user.ParentDTO;
import com.fsoft.fsa.kindergarten.model.dto.user.ParentDetailDTO;
import com.fsoft.fsa.kindergarten.model.form.enroll.EnrollForm;
import com.fsoft.fsa.kindergarten.model.form.user.ChangePasswordForm;
import com.fsoft.fsa.kindergarten.model.form.user.UpdateParentForm;
import com.fsoft.fsa.kindergarten.service.UserSchoolService;
import com.fsoft.fsa.kindergarten.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;

@RestController
@RequestMapping("/parent")
@Validated
@Log4j2
@Tag(name = "Parent Controller")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ParentController {
    private final UserService userService;
    private final UserSchoolService userSchoolService;

    @Operation(summary = "Get parent list", description = "API get parent list")
    @GetMapping(path = "/list")
    public PageResponse getListParent(
            @RequestParam(required = false) String search,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") @Max(10) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy
    ) {
        log.info("Get parent list");
        return userService.getListParent(pageNo, pageSize, sortBy, search);
    }

    @Operation(summary = "Get parent", description = "API get parent by id")
    @GetMapping("/{id}")
    public ParentDTO getParent(@PathVariable int id) {
        log.info("Get parent by id = " + id);
        return userService.getParent(id);
    }

    @Operation(summary = "Enroll Parent to School", description = "API enroll Parent to school")
    @PostMapping
    public String enrollToSchool(@Valid @RequestBody EnrollForm enrollForm) {
        log.info("Enroll parent to school");
        userSchoolService.enrollToSchool(enrollForm);
        return "Enrolled the parent successfully into the school.";
    }

    @Operation(summary = "UnEnroll Parent to School", description = "API unEnroll Parent to school")
    @PatchMapping("{id}")
    public String unEnrollToSchool(
            @Min(1) @PathVariable int id,
            @RequestParam Integer school
    ) {
        log.info("UnEnroll parent to school");
        userSchoolService.UnEnrollToSchool(id, school);
        return "UnEnrolled the parent successfully into the school.";
    }

    @Operation(summary = "Update Parent", description = "API update parent")
    @PutMapping("/info/{id}")
    public String updateParent(@PathVariable int id,
                               @Valid @RequestPart("data") UpdateParentForm parentForm,
                               @RequestPart(value = "avatar", required = false) MultipartFile avatarFile) throws ParseException {
        log.info("Update parent by id = " + id);
        userService.updateParent(id, parentForm, avatarFile);
        return "Updated the parent successfully.";
    }

    @Operation(summary = "Change password for parent", description = "API change password for parent")
    @PatchMapping("/info/{id}")
    public String changePassword(@PathVariable int id,
                                 @Valid @RequestBody ChangePasswordForm changePasswordForm) {
        log.info("Change password for parent by id = " + id);
        userService.changePassword(id, changePasswordForm);
        return "Changed the password successfully.";
    }

    @Operation(summary = "Get parent detail", description = "API get parent detail by id")
    @GetMapping("/info/{id}")
    public ParentDetailDTO getParentDetail(@PathVariable int id) {
        log.info("Get parent detail by id = " + id);
        return userService.getParentDetail(id);
    }

}
