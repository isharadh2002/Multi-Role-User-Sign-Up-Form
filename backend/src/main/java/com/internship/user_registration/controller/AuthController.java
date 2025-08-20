package com.internship.user_registration.controller;

import com.internship.user_registration.dto.ApiResponseDto;
import com.internship.user_registration.dto.LoginRequestDto;
import com.internship.user_registration.dto.LoginResponseDto;
import com.internship.user_registration.service.AuthService;
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
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for authentication operations
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "APIs for user authentication")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        log.info("Login attempt for email: {}", loginRequest.getEmail());

        try {
            LoginResponseDto loginResponse = authService.authenticate(loginRequest);

            return ResponseEntity.ok(
                    ApiResponseDto.success("Login successful", loginResponse)
            );

        } catch (RuntimeException e) {
            log.error("Login failed for email {}: {}", loginRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(401)
                    .body(ApiResponseDto.error("Invalid email or password"));
        } catch (Exception e) {
            log.error("Unexpected error during login", e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDto.error("Login failed due to server error"));
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get current authenticated user details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User details retrieved",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<LoginResponseDto>> getCurrentUser(Authentication authentication) {
        log.debug("Getting current user details for: {}", authentication.getName());

        try {
            LoginResponseDto currentUser = authService.getCurrentUser(authentication.getName());

            return ResponseEntity.ok(
                    ApiResponseDto.success("User details retrieved", currentUser)
            );

        } catch (Exception e) {
            log.error("Error getting current user details", e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDto.error("Failed to retrieve user details"));
        }
    }
}