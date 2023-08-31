package com.javainuse.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ByteArrayNotEmptyValidator.class)
public @interface ByteArrayNotEmpty {
    String message() default "Byte array cannot be null or empty";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
