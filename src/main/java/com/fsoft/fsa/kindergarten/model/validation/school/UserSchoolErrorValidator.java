package com.fsoft.fsa.kindergarten.model.validation.school;

import com.fsoft.fsa.kindergarten.service.SchoolService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UserSchoolErrorValidator implements ConstraintValidator<SchoolError, Integer> {
    @Autowired
    private SchoolService schoolService;

    private String errorMessage;
    private String endMessage;
    private String message;

    @Override
    public void initialize(SchoolError constraintAnnotation) {
        this.errorMessage = constraintAnnotation.errorMessage();
        this.endMessage = constraintAnnotation.endMessage();
        this.message = constraintAnnotation.endMessage();
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isValid(Integer schoolId, ConstraintValidatorContext context) {
        // Kiểm tra trường hợp null
        if (schoolId == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addConstraintViolation();
            return false;
        }

        // Kiểm tra lỗi SchoolError
        if (!schoolService.checkHaveSchoolParentEnroll(schoolId).isPresent()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addConstraintViolation();
            return false; // Dừng kiểm tra nếu có lỗi này
        }

        // Kiểm tra lỗi SchoolEnd
        if (!schoolService.checkParentLearningSchool(schoolId)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(endMessage)
                    .addConstraintViolation();
            return false; // Dừng kiểm tra nếu có lỗi này
        }

        return true; // Nếu không có lỗi nào
    }

}
