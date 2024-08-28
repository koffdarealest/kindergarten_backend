package com.fsoft.fsa.kindergarten.model.validation.user;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class UserFullNameValidator implements ConstraintValidator<UserFullName, String> {
    @Override
    public void initialize(UserFullName constraintAnnotation) {
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext context) {
        return Pattern.compile("^[\\p{L} \\s]+$").matcher(s).matches();
    }
}
