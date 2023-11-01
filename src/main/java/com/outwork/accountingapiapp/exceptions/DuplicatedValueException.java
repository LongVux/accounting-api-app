package com.outwork.accountingapiapp.exceptions;

public class DuplicatedValueException extends RuntimeException {
    public DuplicatedValueException(String message) {
        super(message);
    }
}
