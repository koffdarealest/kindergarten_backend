package com.fsoft.fsa.kindergarten.model.validation.user;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DOBValidator.class)
public @interface DOBInvalid {
    String message() default "{user.dob.past}";

    String messageNull() default "{user.dob.blank}";

    String messageInvalid() default "{user.dob.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
