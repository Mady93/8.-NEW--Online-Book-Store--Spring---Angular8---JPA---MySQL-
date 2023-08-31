package com.javainuse.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MaxLengthValidator.class)
public @interface MaxLength {
    String message() default "Byte array length exceeds the maximum allowed";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int value();
}
