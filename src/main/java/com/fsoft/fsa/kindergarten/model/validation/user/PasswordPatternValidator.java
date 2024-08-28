package com.fsoft.fsa.kindergarten.model.validation.user;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordPatternValidator implements ConstraintValidator<PasswordPattern, String> {

    @Override
    public void initialize(PasswordPattern constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        if(password == null) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate("{user.password.blank}")
                    .addConstraintViolation();
            return false;
        }
        String regex = "^(?=.*[A-Za-z])(?=.*\\d).{9,}$";
        //validate password
        return password.matches(regex);
    }
}
