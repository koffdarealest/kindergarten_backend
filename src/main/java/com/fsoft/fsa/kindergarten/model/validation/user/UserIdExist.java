package com.fsoft.fsa.kindergarten.model.validation.user;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UserIdValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface UserIdExist {
        String message() default "{user.id.notExist}";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
}
