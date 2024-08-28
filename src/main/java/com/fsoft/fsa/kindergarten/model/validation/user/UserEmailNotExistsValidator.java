package com.fsoft.fsa.kindergarten.model.validation.user;

import com.fsoft.fsa.kindergarten.service.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

public class UserEmailNotExistsValidator implements ConstraintValidator<UserEmailNotExist, String> {
    @Autowired
    private UserService userService;

    @SuppressWarnings("deprecation")
    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate("{user.email.blank}")
                    .addConstraintViolation();
            return false;
        }

        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate("{user.email.invalid}")
                    .addConstraintViolation();
            return false;
        }

        if (userService.isEmailExist(email)) {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate("{user.email.exist}")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
