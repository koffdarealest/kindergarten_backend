package com.fsoft.fsa.kindergarten.model.validation.user;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UserFullNameValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserFullName {
    String message() default "{user.fullName.validator}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
