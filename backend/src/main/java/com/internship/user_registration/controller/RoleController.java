package com.internship.user_registration.controller;

import com.internship.user_registration.dto.ApiResponseDto;
import com.internship.user_registration.dto.RoleResponseDto;
import com.internship.user_registration.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
 * REST Controller for role management operations
 * Provides endpoints to retrieve available roles for registration
 */
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Role Management", description = "APIs for role retrieval and management")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @Operation(summary = "Get all available roles", description = "Retrieves all roles that can be assigned to users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<List<RoleResponseDto>>> getAllRoles() {
        log.debug("Getting all available roles");

        List<RoleResponseDto> roles = roleService.findAllRoles();

        return ResponseEntity.ok(
                ApiResponseDto.success("Roles retrieved successfully", roles)
        );
    }

    @GetMapping("/{roleId}")
    @Operation(summary = "Get role by ID", description = "Retrieves a specific role by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role found",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Role not found",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<RoleResponseDto>> getRoleById(@PathVariable Long roleId) {
        log.debug("Getting role by ID: {}", roleId);

        return roleService.findRoleById(roleId)
                .map(role -> ResponseEntity.ok(
                        ApiResponseDto.success("Role found", role)))
                .orElse(ResponseEntity.notFound()
                        .build());
    }

    @GetMapping("/names")
    @Operation(summary = "Get all role names", description = "Retrieves all available role names for form selection")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role names retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    public ResponseEntity<ApiResponseDto<Set<String>>> getAllRoleNames() {
        log.debug("Getting all role names");

        Set<String> roleNames = roleService.getAllRoleNames();

        return ResponseEntity.ok(
                ApiResponseDto.success("Role names retrieved successfully", roleNames)
        );
    }
}