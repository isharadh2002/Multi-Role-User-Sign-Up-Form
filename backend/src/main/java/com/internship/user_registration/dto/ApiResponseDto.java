package com.internship.user_registration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Standard API response wrapper")
public class ApiResponseDto<T> {

    @Schema(description = "Indicates if the operation was successful", example = "true")
    private boolean success;

    @Schema(description = "Human-readable message describing the result", example = "User registered successfully")
    private String message;

    @Schema(description = "Response data (varies by endpoint)")
    private T data;

    @Schema(description = "Validation errors (only present when success=false)")
    private List<ValidationErrorDto> errors;

    // Success response factory methods
    public static <T> ApiResponseDto<T> success(String message, T data) {
        return ApiResponseDto.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponseDto<T> success(String message) {
        return ApiResponseDto.<T>builder()
                .success(true)
                .message(message)
                .build();
    }

    // Error response factory methods
    public static <T> ApiResponseDto<T> error(String message) {
        return ApiResponseDto.<T>builder()
                .success(false)
                .message(message)
                .build();
    }

    public static <T> ApiResponseDto<T> error(String message, List<ValidationErrorDto> errors) {
        return ApiResponseDto.<T>builder()
                .success(false)
                .message(message)
                .errors(errors)
                .build();
    }
}