package com.internship.user_registration.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationErrorDto {

    private String field;
    private String message;
    private Object rejectedValue;

    // Constructor without rejected value (for security)
    public ValidationErrorDto(String field, String message) {
        this.field = field;
        this.message = message;
    }
}