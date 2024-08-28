package com.fsoft.fsa.kindergarten.model.validation.role;

import com.fsoft.fsa.kindergarten.service.RoleService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

public class RoleExistValidator implements ConstraintValidator<RoleExist, Integer> {
    @Autowired
    private RoleService roleService;

    @SuppressWarnings("deprecation")
    @Override
    public boolean isValid(Integer roleId, ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(roleId)) {
            return false;
        }
        return roleService.roleExists(roleId);
    }
}
