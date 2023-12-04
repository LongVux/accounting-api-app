package com.outwork.accountingapiapp.exceptions;

import com.outwork.accountingapiapp.models.payload.responses.ApiError;
import com.outwork.accountingapiapp.services.MessageService;
import com.outwork.accountingapiapp.utils.Util;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {
    public static final String ERROR_MSG_INVALID_DATA = "Dữ liệu không hợp lệ";
    public static final String ERROR_MSG_INVALID_REQUEST_PATH_VARIABLE = "Dữ liệu path variables không hợp lệ";
    public static final String ERROR_MSG_DUPLICATED_DATA = "Dữ liệu trùng lặp";
    public static final String UNKNOWN_ERROR_FIELD = "Trường không xác định";

    @Autowired
    private MessageService messageService;

    @Autowired
    private Util util;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status,
            WebRequest request
    ) {
        List<String> errors = new ArrayList<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.add(util.getSimpleMessage(error.getField(), error.getDefaultMessage(), request.getLocale())));

        ex.getBindingResult()
                .getGlobalErrors()
                .forEach(error -> errors.add(util.getSimpleMessage(error.getObjectName(), error.getDefaultMessage(), request.getLocale())));

        ApiError apiError =
                new ApiError(HttpStatus.BAD_REQUEST, ERROR_MSG_INVALID_DATA, errors);

        return handleExceptionInternal(
                ex, apiError, headers, apiError.getStatus(), request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(
            MissingPathVariableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {

        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                ERROR_MSG_INVALID_REQUEST_PATH_VARIABLE,
                Collections.singletonList(ex.getVariableName())
        );

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicatedValueException.class)
    public ResponseEntity<ApiError> handleDuplicatedValueException(
            DuplicatedValueException ex,
            WebRequest request
    ) {

        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                ERROR_MSG_DUPLICATED_DATA,
                Collections.singletonList(ex.getMessage())
        );

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<ApiError> handleInvalidDataException(
            InvalidDataException ex,
            WebRequest request
    ) {

        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                ERROR_MSG_INVALID_DATA,
                Collections.singletonList(ex.getMessage())
        );

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            // get the property name, the invalid value, and the message from the violation
            String property = violation.getPropertyPath().toString();
            String value = violation.getInvalidValue().toString();
            String message = violation.getMessage();
            // add a formatted error message to the list
            errors.add(String.format("%s: %s (%s)",
                    messageService.getMessage(property, request.getLocale()),
                    messageService.getMessage(value, request.getLocale()),
                    messageService.getMessage(message, request.getLocale())
            ));

            System.out.printf("%s: %s (%s)%n",
                    messageService.getMessage(property, request.getLocale()),
                    messageService.getMessage(value, request.getLocale()),
                    messageService.getMessage(message, request.getLocale())
            );
        }

        // create a custom error response object
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ERROR_MSG_INVALID_DATA, errors);
        // return the error response with a status code of 400 (Bad Request)
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
}
