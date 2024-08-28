package com.fsoft.fsa.kindergarten.model.form.user;

import com.fsoft.fsa.kindergarten.model.validation.role.RoleExist;
import com.fsoft.fsa.kindergarten.model.validation.user.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserForm {
    @NotNull(message = "{user.role.blank}")
    @RoleExist
    private Integer roleId;

    @NotBlank(message = "{user.name.blank}")
    @UserNamePattern
    private String userName;

    @NotBlank(message = "{user.fullname.blank}")
    @UserFullName
    private String fullName;

    @Email
    @NotBlank(message = "{user.email.blank}")
    private String email;

    @DOBInvalid
    private String dob;

    @NotBlank(message = "{user.phone.blank}")
    @PhoneNumber
    private String phone;

    @NotNull(message = "{user.status.null}")
    @EnumValue(name = "status", enumClass = UserStatus.class)
    private String status;
}
