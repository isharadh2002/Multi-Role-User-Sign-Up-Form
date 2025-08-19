package com.internship.user_registration.service;

import com.internship.user_registration.entity.Role;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service interface for role management operations
 */
public interface RoleService {

    /**
     * Initialize default roles in the system
     * This method should be called on application startup
     */
    void initializeDefaultRoles();

    /**
     * Find role by name
     *
     * @param roleName the role name to search for
     * @return Optional containing the role if found
     */
    Optional<Role> findByName(String roleName);

    /**
     * Find roles by a set of role names
     *
     * @param roleNames set of role names to search for
     * @return set of found roles
     */
    Set<Role> findRolesByNames(Set<String> roleNames);

    /**
     * Get all available roles
     *
     * @return list of all roles
     */
    List<Role> getAllRoles();

    /**
     * Validate that all provided role names exist
     *
     * @param roleNames set of role names to validate
     * @return true if all roles exist, false otherwise
     */
    boolean validateRoles(Set<String> roleNames);

    /**
     * Get all valid role names
     *
     * @return set of all valid role names
     */
    Set<String> getAllValidRoleNames();

    /**
     * Create a new role (for admin purposes - optional)
     *
     * @param roleName the name of the role
     * @param description the description of the role
     * @return the created role
     */
    Role createRole(String roleName, String description);
}