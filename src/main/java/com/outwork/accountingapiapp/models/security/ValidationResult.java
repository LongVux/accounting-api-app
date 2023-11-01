package com.outwork.accountingapiapp.models.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationResult {
    private boolean isValid;
    private String validateMessage;

    public boolean isNotValid () {
        return !isValid;
    }
}
