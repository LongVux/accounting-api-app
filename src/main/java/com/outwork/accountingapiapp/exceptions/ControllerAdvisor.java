package com.outwork.accountingapiapp.exceptions;

import com.outwork.accountingapiapp.models.payload.responses.ApiError;
import com.outwork.accountingapiapp.utils.Util;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {
    public static final String ERROR_MSG_INVALID_PAYLOAD = "Dữ liệu payload không hợp lệ";
    public static final String ERROR_MSG_INVALID_REQUEST_PATH_VARIABLE = "Dữ liệu path variables không hợp lệ";

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status,
            WebRequest request
    ) {
        List<String> errors = new ArrayList<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.add(Util.getSimpleMessage(error.getField(), error.getDefaultMessage())));

        ex.getBindingResult()
                .getGlobalErrors()
                .forEach(error -> errors.add(Util.getSimpleMessage(error.getObjectName(), error.getDefaultMessage())));

        ApiError apiError =
                new ApiError(HttpStatus.BAD_REQUEST, ERROR_MSG_INVALID_PAYLOAD, errors);

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


    // }

//    @ExceptionHandler(ConstraintViolationException.class)
//    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, WebRequest
//    request) {
//        List<String> errors = new ArrayList<>();
//        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
//            // get the property name, the invalid value, and the message from the violation
//            String property = violation.getPropertyPath().toString();
//            String value = violation.getInvalidValue().toString();
//            String message = violation.getMessage();
//            // resolve the property and message codes to localized messages using the MessageSource
//            String localizedProperty = messageSource.getMessage(property, null, request.getLocale());
//            String localizedMessage = messageSource.getMessage(message, null, request.getLocale());
//            // add a formatted error message to the list
//            errors.add(String.format("%s: %s (%s)", localizedProperty, value, localizedMessage));
//        }
//
//        String localizedGeneralErrorMsg = messageSource.getMessage(AppString.ERROR_MSG_INVALID_PAYLOAD, null,
//        request.getLocale());
//
//        // create a custom error response object
//        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, localizedGeneralErrorMsg, errors);
//        // return the error response with a status code of 400 (Bad Request)
//        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
//    }
}
