package com.javainuse.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MaxLengthValidator implements ConstraintValidator<MaxLength, byte[]> {

    private int maxLength;

    @Override
    public void initialize(MaxLength constraintAnnotation) {
        this.maxLength = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(byte[] value, ConstraintValidatorContext context) {
        return value == null || value.length <= maxLength;
    }
}
