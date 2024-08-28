package com.fsoft.fsa.kindergarten.model.validation.user;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UserNameNotExistsValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserNameNotExist {
    String message() default "{user.username.existed}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}