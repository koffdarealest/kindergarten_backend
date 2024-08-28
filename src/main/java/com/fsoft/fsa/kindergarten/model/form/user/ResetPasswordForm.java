package com.fsoft.fsa.kindergarten.model.form.user;

import com.fsoft.fsa.kindergarten.model.validation.user.PasswordPattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResetPasswordForm {

    @PasswordPattern
    private String password;

    private String confirmPassword;
}
