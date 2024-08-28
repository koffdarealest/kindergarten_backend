package com.fsoft.fsa.kindergarten.model.validation.user;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UserEmailNotExistsValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserEmailNotExist {
    String message() default "{user.email.existed}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
