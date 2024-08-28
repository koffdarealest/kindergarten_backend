package com.fsoft.fsa.kindergarten.model.validation.user;

import com.fsoft.fsa.kindergarten.service.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UserIdValidator implements ConstraintValidator<UserIdExist, Integer> {

    @Autowired
    private UserService userService;

    @SuppressWarnings("deprecation")
    @Override
    public boolean isValid(Integer userId, ConstraintValidatorContext constraintValidatorContext) {
        if(userId == null) {
            return false;
        }
        return userService.existUser(userId).isPresent();
    }
}
