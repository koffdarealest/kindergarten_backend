package com.fsoft.fsa.kindergarten.model.form.user;

import com.fsoft.fsa.kindergarten.model.validation.user.PasswordPattern;
import com.fsoft.fsa.kindergarten.model.validation.user.PhoneNumber;
import com.fsoft.fsa.kindergarten.model.validation.user.UserEmailNotExist;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterForm {

    @NotBlank(message = "field.blank")
    private String fullName;

    @NotBlank(message = "field.blank")
    @UserEmailNotExist
    private String email;

    @NotBlank(message = "field.blank")
    @PhoneNumber
    private String phone;

    @PasswordPattern
    @NotBlank(message = "field.blank")
    private String password;

    @NotBlank(message = "field.blank")
    private String confirmPassword;
}
