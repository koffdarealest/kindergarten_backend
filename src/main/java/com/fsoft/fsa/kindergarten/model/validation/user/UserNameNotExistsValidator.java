package com.fsoft.fsa.kindergarten.model.validation.user;

import com.fsoft.fsa.kindergarten.service.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

public class UserNameNotExistsValidator implements ConstraintValidator<UserNameNotExist, String> {
    @Autowired
    private UserService userService;

    @SuppressWarnings("deprecation")
    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(username)) {
            return true;
        }
        return !userService.isUserExistsByUsername(username);
    }
}
