package com.example.Nitin.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PanValidator implements ConstraintValidator<ValidPAN, String> {

    private static final String PAN_PATTERN = "[A-Z]{5}[0-9]{4}[A-Z]";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) return true; // Allow blank, handle separately with @NotBlank
        return value.matches(PAN_PATTERN);
    }
}
