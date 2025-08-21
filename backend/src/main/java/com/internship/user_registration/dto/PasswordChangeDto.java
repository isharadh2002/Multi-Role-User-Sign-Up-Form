package com.internship.user_registration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordChangeDto {

    @NotBlank(message = "Current password is required")
    @JsonProperty("currentPassword")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_#])[A-Za-z\\d@$!%*?&_#]+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    @JsonProperty("newPassword")
    private String newPassword;

    @NotBlank(message = "Password confirmation is required")
    @JsonProperty("confirmNewPassword")
    private String confirmNewPassword;
}