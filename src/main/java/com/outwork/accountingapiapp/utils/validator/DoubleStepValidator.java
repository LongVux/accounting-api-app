package com.outwork.accountingapiapp.utils.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.ObjectUtils;

public class DoubleStepValidator implements ConstraintValidator<DoubleStep, Number> {
    private double step;

    @Override
    public void initialize(DoubleStep constraintAnnotation) {
        this.step = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Number number, ConstraintValidatorContext context) {
        if (ObjectUtils.isEmpty(number)) {
            return true;
        }

        double doubleValue = number.doubleValue();

        return doubleValue % step == 0;
    }
}
