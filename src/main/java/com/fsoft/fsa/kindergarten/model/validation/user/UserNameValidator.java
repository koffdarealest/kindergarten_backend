package com.fsoft.fsa.kindergarten.model.validation.user;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.text.Normalizer;
import java.util.regex.Pattern;


public class UserNameValidator implements ConstraintValidator<UserNamePattern, String> {
    // Check first letter is Uppercase, next is letter and end is number
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Z][a-zA-Z]*\\d+$");
    // Remove accent from characters
    private static final Pattern ACCENT_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    @Override
    public void initialize(UserNamePattern userNamePattern) {
    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (username == null || username.trim().isEmpty()) return false;

        String normalized = Normalizer.normalize(username, Normalizer.Form.NFD);
        if (containsAccents(normalized)) return false;

        String result = removeAccents(normalized);
        return USERNAME_PATTERN.matcher(result).matches();
    }

    private boolean containsAccents(String text) {
        return ACCENT_PATTERN.matcher(text).find();
    }

    private String removeAccents(String text) {
        //remove combining marks
        return ACCENT_PATTERN.matcher(text)
                .replaceAll("")
                .replaceAll("đ", "d")
                .replaceAll("Đ", "D");
    }
}
