package com.internship.user_registration.service;

import com.internship.user_registration.dto.RoleResponseDto;
import com.internship.user_registration.entity.Role;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service interface for Role operations
 * Follows the Service Layer pattern for business logic abstraction
 */
public interface RoleService {

    /**
     * Find all available roles
     *
     * @return list of all roles as DTOs
     */
    List<RoleResponseDto> findAllRoles();

    /**
     * Find role by ID
     *
     * @param roleId the role ID
     * @return Optional containing the role DTO if found
     */
    Optional<RoleResponseDto> findRoleById(Long roleId);

    /**
     * Find role by name
     *
     * @param name the role name
     * @return Optional containing the role DTO if found
     */
    Optional<RoleResponseDto> findRoleByName(String name);

    /**
     * Find roles by names (for user registration)
     *
     * @param roleNames set of role names
     * @return set of role entities
     */
    Set<Role> findRoleEntitiesByNames(Set<String> roleNames);

    /**
     * Check if role exists by name
     *
     * @param name the role name
     * @return true if role exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Validate role names against available roles
     *
     * @param roleNames set of role names to validate
     * @return set of invalid role names (empty if all valid)
     */
    Set<String> validateRoleNames(Set<String> roleNames);

    /**
     * Get all available role names
     *
     * @return set of all role names
     */
    Set<String> getAllRoleNames();

    /**
     * Initialize default roles if they don't exist
     * Should be called during application startup
     */
    void initializeDefaultRoles();
}