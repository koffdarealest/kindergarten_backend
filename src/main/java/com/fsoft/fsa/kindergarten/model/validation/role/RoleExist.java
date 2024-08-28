package com.fsoft.fsa.kindergarten.model.validation.role;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RoleExistValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RoleExist {
    String message() default "{role.role.existed}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
