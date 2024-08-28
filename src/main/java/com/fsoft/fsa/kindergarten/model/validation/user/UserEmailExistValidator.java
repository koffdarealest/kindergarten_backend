package com.fsoft.fsa.kindergarten.model.validation.user;

import com.fsoft.fsa.kindergarten.service.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;


public class UserEmailExistValidator implements ConstraintValidator<UserEmailExist, String> {

    @Autowired
    private UserService userService;

    @Override
    public void initialize(UserEmailExist constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {

        return userService.isEmailExist(email);
    }
}
