package com.fsoft.fsa.kindergarten.model.validation.user;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class DOBValidator implements ConstraintValidator<DOBInvalid, String> {

    private String message;
    private String messageNull;
    private String messageInvalid;

    @Override
    public void initialize(DOBInvalid dob) {
        this.message = dob.message();
        this.messageNull = dob.messageNull();
        this.messageInvalid = dob.messageInvalid();
    }

    @Override
    public boolean isValid(String date, ConstraintValidatorContext context) {
        if (date == null || date.trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(messageNull)
                    .addConstraintViolation();
            return false;
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(date, dateFormatter);
        } catch (DateTimeParseException e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(messageInvalid)
                    .addConstraintViolation();
            return false;
        }

        if (!parsedDate.isBefore(LocalDate.now())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
