package com.springbatch.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalException extends ResponseEntityExceptionHandler {

    @ExceptionHandler(FieldMissingException.class)
    public ResponseEntity<?> fieldMissingHandle(FieldMissingException ex, WebRequest request) {
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }
}
