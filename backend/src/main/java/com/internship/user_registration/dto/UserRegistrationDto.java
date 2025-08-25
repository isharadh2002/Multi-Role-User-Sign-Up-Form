package com.internship.user_registration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "User registration request containing all required user information")
public class UserRegistrationDto {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "First name must contain only letters and spaces")
    @Schema(description = "User's first name", example = "John", minLength = 2, maxLength = 100)
    @JsonProperty("firstName")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Last name must contain only letters and spaces")
    @Schema(description = "User's last name", example = "Doe", minLength = 2, maxLength = 100)
    @JsonProperty("lastName")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Schema(description = "User's email address (must be unique)", example = "john.doe@example.com")
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_#])[A-Za-z\\d@$!%*?&_#]+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    @Schema(description = "User's password", example = "SecureP@ss123",
            minLength = 8, maxLength = 100,
            pattern = "Must contain uppercase, lowercase, number, and special character")
    @JsonProperty("password")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    @Schema(description = "Password confirmation (must match password)", example = "SecureP@ss123")
    @JsonProperty("confirmPassword")
    private String confirmPassword;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Please provide a valid phone number with country code")
    @Schema(description = "User's phone number with country code", example = "+1234567890")
    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @Pattern(regexp = "^[A-Z]{2}$", message = "Country must be a valid 2-letter ISO country code")
    @Schema(description = "User's country (ISO 3166-1 alpha-2 code)", example = "US", pattern = "^[A-Z]{2}$")
    @JsonProperty("country")
    private String country;

    @NotEmpty(message = "At least one role must be selected")
    @Size(min = 1, max = 3, message = "You can select between 1 and 3 roles")
    @Schema(description = "List of user roles",
            example = "[\"General User\", \"Professional\"]",
            allowableValues = {"General User", "Professional", "Business Owner", "Admin"})
    @JsonProperty("roles")
    private Set<String> roles;
}