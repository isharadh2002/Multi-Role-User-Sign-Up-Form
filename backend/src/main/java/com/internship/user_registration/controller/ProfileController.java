package com.internship.user_registration.controller;

import com.internship.user_registration.dto.ApiResponseDto;
import com.internship.user_registration.dto.UserRegistrationDto;
import com.internship.user_registration.dto.UserResponseDto;
import com.internship.user_registration.dto.ValidationErrorDto;
import com.internship.user_registration.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for user profile management
 */
@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Profile", description = "APIs for user profile management")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class ProfileController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get user profile", description = "Get current user's profile information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<UserResponseDto>> getProfile(Authentication authentication) {
        log.debug("Getting profile for user: {}", authentication.getName());

        return userService.findUserByEmail(authentication.getName())
                .map(user -> ResponseEntity.ok(
                        ApiResponseDto.success("Profile retrieved successfully", user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping
    @Operation(summary = "Update user profile", description = "Update current user's profile information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<UserResponseDto>> updateProfile(
            @Valid @RequestBody UserRegistrationDto updateDto,
            BindingResult bindingResult,
            Authentication authentication) {

        log.info("Updating profile for user: {}", authentication.getName());

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            List<ValidationErrorDto> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(this::mapFieldError)
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Validation failed", errors));
        }

        try {
            UserResponseDto updatedUser = userService.updateUserProfile(authentication.getName(), updateDto);

            return ResponseEntity.ok(
                    ApiResponseDto.success("Profile updated successfully", updatedUser)
            );

        } catch (RuntimeException e) {
            log.error("Profile update failed for user {}: {}", authentication.getName(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during profile update", e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDto.error("Profile update failed due to server error"));
        }
    }

    /**
     * Helper method to map Spring validation errors to our custom error DTO
     */
    private ValidationErrorDto mapFieldError(FieldError fieldError) {
        return ValidationErrorDto.builder()
                .field(fieldError.getField())
                .message(fieldError.getDefaultMessage())
                .rejectedValue(fieldError.getRejectedValue())
                .build();
    }
}