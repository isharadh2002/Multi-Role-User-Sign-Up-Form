package com.internship.user_registration.controller;

import com.internship.user_registration.dto.ApiResponseDto;
import com.internship.user_registration.dto.UserRegistrationDto;
import com.internship.user_registration.dto.UserResponseDto;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for user registration and management
 * Following RESTful API principles
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Authentication", description = "User registration and authentication endpoints")
//@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with the provided information and assigned roles"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or user already exists",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> registerUser(
            @Valid @RequestBody UserRegistrationDto registrationDto) {

        log.info("Received registration request for email: {}", registrationDto.getEmail());

        try {
            UserResponseDto userResponse = userService.registerUser(registrationDto);

            ApiResponseDto<UserResponseDto> response = ApiResponseDto.success(
                    "User registered successfully",
                    userResponse
            );

            log.info("Successfully registered user with ID: {}", userResponse.getUserId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            log.warn("Registration failed for email {}: {}", registrationDto.getEmail(), e.getMessage());

            ApiResponseDto<UserResponseDto> response = ApiResponseDto.error(e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            log.error("Unexpected error during registration for email {}: {}",
                    registrationDto.getEmail(), e.getMessage(), e);

            ApiResponseDto<UserResponseDto> response = ApiResponseDto.error(
                    "An unexpected error occurred during registration"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(
            summary = "Check if email exists",
            description = "Checks whether a user with the given email already exists"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email availability checked successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid email format")
    })
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponseDto<Boolean>> checkEmailExists(@RequestParam String email) {
        log.debug("Checking email availability for: {}", email);

        try {
            boolean exists = userService.existsByEmail(email);

            ApiResponseDto<Boolean> response = ApiResponseDto.success(
                    exists ? "Email is already taken" : "Email is available",
                    exists
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error checking email availability: {}", e.getMessage());

            ApiResponseDto<Boolean> response = ApiResponseDto.error("Error checking email availability");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(
            summary = "Get user by email",
            description = "Retrieves user information by email address"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/user")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> getUserByEmail(@RequestParam String email) {
        log.debug("Getting user by email: {}", email);

        Optional<UserResponseDto> user = userService.findUserByEmail(email);

        if (user.isPresent()) {
            ApiResponseDto<UserResponseDto> response = ApiResponseDto.success(
                    "User found",
                    user.get()
            );
            return ResponseEntity.ok(response);
        } else {
            ApiResponseDto<UserResponseDto> response = ApiResponseDto.error("User not found");
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Get all users",
            description = "Retrieves all registered users (admin functionality)"
    )
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    @GetMapping("/users")
    public ResponseEntity<ApiResponseDto<List<UserResponseDto>>> getAllUsers() {
        log.debug("Getting all users");

        try {
            List<UserResponseDto> users = userService.getAllUsers();

            ApiResponseDto<List<UserResponseDto>> response = ApiResponseDto.success(
                    "Users retrieved successfully",
                    users
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error retrieving users: {}", e.getMessage());

            ApiResponseDto<List<UserResponseDto>> response = ApiResponseDto.error(
                    "Error retrieving users"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}