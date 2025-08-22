package com.internship.user_registration.controller;

import com.internship.user_registration.dto.*;
import com.internship.user_registration.service.RoleService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for admin operations
 * Only accessible by users with ADMIN role
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Management", description = "APIs for admin-only operations")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    // User Management Endpoints

    @GetMapping("/users")
    @Operation(summary = "Get all users", description = "Retrieve all registered users (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<List<UserResponseDto>>> getAllUsers() {
        log.debug("Admin getting all users");

        List<UserResponseDto> users = userService.findAllUsers();

        return ResponseEntity.ok(
                ApiResponseDto.success("Users retrieved successfully", users)
        );
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Get user by ID", description = "Retrieve specific user by ID (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<UserResponseDto>> getUserById(@PathVariable Long userId) {
        log.debug("Admin getting user by ID: {}", userId);

        return userService.findUserById(userId)
                .map(user -> ResponseEntity.ok(
                        ApiResponseDto.success("User found", user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{userId}")
    @Operation(summary = "Delete user", description = "Delete user by ID (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<Void>> deleteUser(@PathVariable Long userId) {
        log.info("Admin deleting user with ID: {}", userId);

        try {
            userService.deleteUser(userId);

            return ResponseEntity.ok(
                    ApiResponseDto.success("User deleted successfully")
            );

        } catch (RuntimeException e) {
            log.error("Failed to delete user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during user deletion", e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDto.error("User deletion failed due to server error"));
        }
    }

    // Role Management Endpoints

    @PostMapping("/roles")
    @Operation(summary = "Create role", description = "Create a new role (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Role created successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed or role already exists",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<RoleResponseDto>> createRole(
            @Valid @RequestBody RoleCreateDto roleCreateDto,
            BindingResult bindingResult) {

        log.info("Admin creating role: {}", roleCreateDto.getName());

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
            RoleResponseDto createdRole = roleService.createRole(
                    roleCreateDto.getName(),
                    roleCreateDto.getDescription()
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDto.success("Role created successfully", createdRole));

        } catch (RuntimeException e) {
            log.error("Role creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during role creation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Role creation failed due to server error"));
        }
    }

    @PutMapping("/roles/{roleId}")
    @Operation(summary = "Update role", description = "Update role by ID (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role updated successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Role not found",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<RoleResponseDto>> updateRole(
            @PathVariable Long roleId,
            @Valid @RequestBody RoleUpdateDto roleUpdateDto,
            BindingResult bindingResult) {

        log.info("Admin updating role with ID: {}", roleId);

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
            RoleResponseDto updatedRole = roleService.updateRole(
                    roleId,
                    roleUpdateDto.getName(),
                    roleUpdateDto.getDescription()
            );

            return ResponseEntity.ok(
                    ApiResponseDto.success("Role updated successfully", updatedRole)
            );

        } catch (RuntimeException e) {
            log.error("Role update failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during role update", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Role update failed due to server error"));
        }
    }

    @DeleteMapping("/roles/{roleId}")
    @Operation(summary = "Delete role", description = "Delete role by ID (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role deleted successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Role not found",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<Void>> deleteRole(@PathVariable Long roleId) {
        log.info("Admin deleting role with ID: {}", roleId);

        try {
            roleService.deleteRole(roleId);

            return ResponseEntity.ok(
                    ApiResponseDto.success("Role deleted successfully")
            );

        } catch (RuntimeException e) {
            log.error("Role deletion failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during role deletion", e);
            return ResponseEntity.status(500)
                    .body(ApiResponseDto.error("Role deletion failed due to server error"));
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