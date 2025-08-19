package com.internship.user_registration.controller;

import com.internship.user_registration.dto.ApiResponseDto;
import com.internship.user_registration.entity.Role;
import com.internship.user_registration.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * REST Controller for role management
 * Provides endpoints for retrieving available roles
 */
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Role Management", description = "Role management endpoints")
//@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class RoleController {

    private final RoleService roleService;

    @Operation(
            summary = "Get all available roles",
            description = "Retrieves all available roles for user registration"
    )
    @ApiResponse(responseCode = "200", description = "Roles retrieved successfully")
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<Role>>> getAllRoles() {
        log.debug("Getting all available roles");

        try {
            List<Role> roles = roleService.getAllRoles();

            ApiResponseDto<List<Role>> response = ApiResponseDto.success(
                    "Roles retrieved successfully",
                    roles
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error retrieving roles: {}", e.getMessage());

            ApiResponseDto<List<Role>> response = ApiResponseDto.error("Error retrieving roles");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @Operation(
            summary = "Get all valid role names",
            description = "Retrieves all valid role names for validation purposes"
    )
    @ApiResponse(responseCode = "200", description = "Role names retrieved successfully")
    @GetMapping("/names")
    public ResponseEntity<ApiResponseDto<Set<String>>> getAllRoleNames() {
        log.debug("Getting all valid role names");

        try {
            Set<String> roleNames = roleService.getAllValidRoleNames();

            ApiResponseDto<Set<String>> response = ApiResponseDto.success(
                    "Role names retrieved successfully",
                    roleNames
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error retrieving role names: {}", e.getMessage());

            ApiResponseDto<Set<String>> response = ApiResponseDto.error("Error retrieving role names");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @Operation(
            summary = "Validate roles",
            description = "Validates if the provided role names are valid"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Validation completed"),
            @ApiResponse(responseCode = "400", description = "Invalid role names provided")
    })
    @PostMapping("/validate")
    public ResponseEntity<ApiResponseDto<Boolean>> validateRoles(@RequestBody Set<String> roleNames) {
        log.debug("Validating roles: {}", roleNames);

        try {
            boolean isValid = roleService.validateRoles(roleNames);

            ApiResponseDto<Boolean> response = ApiResponseDto.success(
                    isValid ? "All roles are valid" : "Some roles are invalid",
                    isValid
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error validating roles: {}", e.getMessage());

            ApiResponseDto<Boolean> response = ApiResponseDto.error("Error validating roles");
            return ResponseEntity.badRequest().body(response);
        }
    }
}