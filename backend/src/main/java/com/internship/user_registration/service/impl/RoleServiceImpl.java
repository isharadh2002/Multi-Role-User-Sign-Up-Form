package com.internship.user_registration.service.impl;

import com.internship.user_registration.entity.Role;
import com.internship.user_registration.repository.RoleRepository;
import com.internship.user_registration.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Implementation of RoleService
 * Handles role management and initialization
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public void initializeDefaultRoles() {
        log.info("Initializing default roles");

        createRoleIfNotExists("General User", "Standard user with basic access");
        createRoleIfNotExists("Professional", "Professional user with extended features");
        createRoleIfNotExists("Business Owner", "Business owner with business-specific features");

        log.info("Default roles initialization completed");
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findByName(String roleName) {
        log.debug("Finding role by name: {}", roleName);
        return roleRepository.findByName(roleName);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Role> findRolesByNames(Set<String> roleNames) {
        log.debug("Finding roles by names: {}", roleNames);
        return roleRepository.findByNameIn(roleNames);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        log.debug("Retrieving all roles");
        return roleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateRoles(Set<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return false;
        }

        Set<String> validRoleNames = getAllValidRoleNames();
        return validRoleNames.containsAll(roleNames);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getAllValidRoleNames() {
        return roleRepository.findAllRoleNames();
    }

    @Override
    public Role createRole(String roleName, String description) {
        log.info("Creating new role: {} with description: {}", roleName, description);

        if (roleRepository.existsByName(roleName)) {
            throw new IllegalArgumentException("Role with name '" + roleName + "' already exists");
        }

        Role role = Role.builder()
                .name(roleName)
                .description(description)
                .build();

        Role savedRole = roleRepository.save(role);
        log.info("Successfully created role with ID: {}", savedRole.getId());

        return savedRole;
    }

    /**
     * Helper method to create a role if it doesn't already exist
     *
     * @param roleName the name of the role
     * @param description the description of the role
     */
    private void createRoleIfNotExists(String roleName, String description) {
        if (!roleRepository.existsByName(roleName)) {
            Role role = Role.builder()
                    .name(roleName)
                    .description(description)
                    .build();

            roleRepository.save(role);
            log.info("Created default role: {}", roleName);
        } else {
            log.debug("Role already exists: {}", roleName);
        }
    }
}