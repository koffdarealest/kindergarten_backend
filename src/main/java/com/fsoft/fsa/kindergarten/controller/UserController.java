package com.fsoft.fsa.kindergarten.controller;

import com.fsoft.fsa.kindergarten.config.Translator;
import com.fsoft.fsa.kindergarten.model.dto.page.PageResponse;
import com.fsoft.fsa.kindergarten.model.dto.user.UserDTO;
import com.fsoft.fsa.kindergarten.model.form.user.CreateUserForm;
import com.fsoft.fsa.kindergarten.model.form.user.UserForm;
import com.fsoft.fsa.kindergarten.model.validation.user.UserFullName;
import com.fsoft.fsa.kindergarten.model.validation.user.UserIdExist;
import com.fsoft.fsa.kindergarten.model.validation.user.UserStatus;
import com.fsoft.fsa.kindergarten.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/user")
@Validated
@Log4j2
@Tag(name = "User Controller")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get user list", description = "API get user list")
    @GetMapping(path = "/list")
    public PageResponse getListUser(
            @RequestParam(required = false)  String search,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") @Max(20000) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy
    ) {
        log.info("Get user list");
        return userService.getListUser(pageNo, pageSize, sortBy, search);
    }

    @Operation(summary = "Add user", description = "API create new user")
    @PostMapping
    public String addUser(@Valid @RequestBody CreateUserForm user) throws MessagingException, IOException, ParseException {
        userService.createUser(user);
        return Translator.toLocale("user.add.success");
    }

    @Operation(summary = "Get user", description = "API get user by id")
    @GetMapping("/{userId}")
    public UserDTO getUser(@PathVariable int userId) {
        return userService.getUser(userId);
    }

    @Operation(summary = "Update user", description = "Update user info")
    @PutMapping("/{userId}")
    public String updateUser(@Min(1) @PathVariable int userId, @Valid @RequestBody UserForm userForm) throws ParseException {
        userService.updateUser(userId, userForm);
        return "update user success";
    }

    @Operation(summary = "Update user status", description = "Update user status inactive or de-active")
    @PatchMapping("{userId}")
    public String changeStatus(@Min(1) @PathVariable int userId, @RequestParam UserStatus status) {
        userService.changeStatus(userId, status);
        return "User change success";
    }

    @Operation(summary = "Delete user", description = "Delete user in list user")
    @DeleteMapping("{userId}")
    public String DeleteUser(@Min(1) @PathVariable int userId) {
        userService.deleteUser(userId);
        return "Delete user successful";
    }

    @Operation(summary = "Get user status", description = "API get user status")
    @GetMapping(path = "/status")
    public List<UserStatus> getUserStatus() {
        return userService.getUserStatuses();
    }

    @Operation(summary = "Get username", description = "API get username by fullName from add user")
    @GetMapping("/username")
    public String getUserNameByFullNameAdd(
            @RequestParam()
            @NotBlank(message = "FullName is not blank")
            @UserFullName
            String fullName
    ) {
        return userService.getUserName(fullName);
    }

    @Operation(summary = "Get username", description = "API get username by fullName from edit user")
    @GetMapping("/edit/username")
    public String getUserNameByFullNameEdit(
            @RequestParam()
            @NotBlank(message = "FullName is not blank")
            @UserFullName
            String fullName,
            @UserIdExist
            Integer userId
    ) {
        return userService.getUserNameFromEdit(fullName, userId);
    }

    @Operation(summary = "Update avatar", description = "API update avatar")
    @PatchMapping("/avatar")
    public String updateAvatar(@RequestParam("avatar") MultipartFile avatar) {
        userService.updateAvatar(avatar);
        return "Update avatar success";
    }

}
