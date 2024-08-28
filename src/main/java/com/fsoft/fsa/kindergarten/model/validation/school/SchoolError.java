package com.fsoft.fsa.kindergarten.model.validation.school;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UserSchoolErrorValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SchoolError {
    String message() default "{school.id.invalid}";

    String errorMessage() default "{user.school.unroll}";

    String endMessage() default "{user.school.endLearning}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

