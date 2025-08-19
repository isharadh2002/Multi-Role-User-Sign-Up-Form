package com.internship.user_registration.exception;

import com.internship.user_registration.dto.ApiResponseDto;
import com.internship.user_registration.dto.ValidationErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Global exception handler for the application
 * Provides consistent error responses across all endpoints
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle validation errors from @Valid annotations
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        log.warn("Validation error occurred: {}", ex.getMessage());

        List<ValidationErrorDto> errors = new ArrayList<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            Object rejectedValue = ((FieldError) error).getRejectedValue();

            errors.add(new ValidationErrorDto(fieldName, errorMessage, rejectedValue));
        });

        ApiResponseDto<Object> response = ApiResponseDto.error(
                "Validation failed",
                errors
        );

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle bind exceptions (form data binding errors)
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleBindExceptions(BindException ex) {
        log.warn("Bind error occurred: {}", ex.getMessage());

        List<ValidationErrorDto> errors = new ArrayList<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();

            errors.add(new ValidationErrorDto(fieldName, errorMessage));
        });

        ApiResponseDto<Object> response = ApiResponseDto.error(
                "Data binding failed",
                errors
        );

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle illegal argument exceptions (business logic violations)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        log.warn("Illegal argument exception: {}", ex.getMessage());

        ApiResponseDto<Object> response = ApiResponseDto.error(ex.getMessage());

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle illegal state exceptions
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleIllegalStateException(
            IllegalStateException ex, WebRequest request) {

        log.error("Illegal state exception: {}", ex.getMessage());

        ApiResponseDto<Object> response = ApiResponseDto.error(
                "Application is in an invalid state. Please try again."
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * Handle runtime exceptions
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleRuntimeException(
            RuntimeException ex, WebRequest request) {

        log.error("Runtime exception occurred: {}", ex.getMessage(), ex);

        ApiResponseDto<Object> response = ApiResponseDto.error(
                "An unexpected error occurred. Please try again later."
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Object>> handleGlobalException(
            Exception ex, WebRequest request) {

        log.error("Unexpected exception occurred: {}", ex.getMessage(), ex);

        ApiResponseDto<Object> response = ApiResponseDto.error(
                "An unexpected error occurred. Please contact support if the problem persists."
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}