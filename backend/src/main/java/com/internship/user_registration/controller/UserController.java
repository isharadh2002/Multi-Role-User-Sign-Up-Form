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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for user registration operations
 * Follows RESTful principles and proper HTTP status codes
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Registration", description = "APIs for user registration and authentication")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"}) // Frontend URLs
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or validation error",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Email or phone number already exists",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<UserResponseDto>> registerUser(
            @Valid @RequestBody UserRegistrationDto registrationDto,
            BindingResult bindingResult) {

        log.info("Registration request received for email: {}", registrationDto.getEmail());

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            List<ValidationErrorDto> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(this::mapFieldError)
                    .collect(Collectors.toList());

            log.warn("Validation errors in registration request: {}", errors.size());
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Validation failed", errors));
        }

        try {
            // Register user
            UserResponseDto userResponse = userService.registerUser(registrationDto);

            log.info("User registered successfully with ID: {}", userResponse.getUserId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDto.success("User registered successfully", userResponse));

        } catch (RuntimeException e) {
            log.error("Registration failed for email {}: {}", registrationDto.getEmail(), e.getMessage());

            // Handle specific business logic errors
            if (e.getMessage().contains("already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponseDto.error(e.getMessage()));
            }

            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during registration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Registration failed due to server error"));
        }
    }

    @GetMapping("/check-email")
    @Operation(summary = "Check email availability", description = "Checks if an email is already registered")
    public ResponseEntity<ApiResponseDto<Boolean>> checkEmailAvailability(
            @RequestParam String email) {

        log.debug("Checking email availability for: {}", email);

        boolean exists = userService.existsByEmail(email);
        boolean available = !exists;

        return ResponseEntity.ok(
                ApiResponseDto.success(
                        available ? "Email is available" : "Email is already registered",
                        available
                )
        );
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