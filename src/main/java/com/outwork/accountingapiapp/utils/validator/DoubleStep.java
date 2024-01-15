package com.outwork.accountingapiapp.utils.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DoubleStepValidator.class)
public @interface DoubleStep {
    String message() default "{msg.err.double.step}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    double value();
}
