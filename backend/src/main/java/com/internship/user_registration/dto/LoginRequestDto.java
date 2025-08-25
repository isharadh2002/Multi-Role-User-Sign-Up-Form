package com.internship.user_registration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User login credentials")
@Builder
public class LoginRequestDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Schema(description = "User's email address", example = "john.doe@example.com")
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "Password is required")
    @Schema(description = "User's password", example = "SecureP@ss123")
    @JsonProperty("password")
    private String password;
}