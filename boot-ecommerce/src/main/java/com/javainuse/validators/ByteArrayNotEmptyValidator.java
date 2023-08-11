package com.javainuse.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ByteArrayNotEmptyValidator implements ConstraintValidator<ByteArrayNotEmpty, byte[]> {
    @Override
    public boolean isValid(byte[] value, ConstraintValidatorContext context) {
        return value != null && value.length > 0;
    }
}
