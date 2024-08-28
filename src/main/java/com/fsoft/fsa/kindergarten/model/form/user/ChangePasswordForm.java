package com.fsoft.fsa.kindergarten.model.form.user;

import com.fsoft.fsa.kindergarten.model.validation.user.PasswordPattern;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordForm {

    @NotBlank(message = "{field.blank}")
    private String oldPassword;

    @NotBlank(message = "{field.blank}")
    @PasswordPattern
    private String newPassword;
}
