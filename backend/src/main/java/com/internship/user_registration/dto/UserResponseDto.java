package com.internship.user_registration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User information response")
@Builder
public class UserResponseDto {

    @Schema(description = "Unique user identifier", example = "123")
    private Long userId;

    @Schema(description = "User's first name", example = "John")
    private String firstName;

    @Schema(description = "User's last name", example = "Doe")
    private String lastName;

    @Schema(description = "User's email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "User's phone number", example = "+1234567890")
    private String phoneNumber;

    @Schema(description = "User's country code", example = "US")
    private String country;

    @Schema(description = "User's assigned roles", example = "[\"General User\", \"Professional\"]")
    private Set<String> roles;

    @Schema(description = "Account creation timestamp", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
}