package com.internship.user_registration.service.impl;

import com.internship.user_registration.dto.RoleResponseDto;
import com.internship.user_registration.entity.Role;
import com.internship.user_registration.mapper.RoleMapper;
import com.internship.user_registration.repository.RoleRepository;
import com.internship.user_registration.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service implementation for Role operations
 * Implements business logic following SOLID principles
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    public List<RoleResponseDto> findAllRoles() {
        log.debug("Finding all roles");
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<RoleResponseDto> findRoleById(Long roleId) {
        log.debug("Finding role by ID: {}", roleId);
        return roleRepository.findById(roleId)
                .map(roleMapper::toResponseDto);
    }

    @Override
    public Optional<RoleResponseDto> findRoleByName(String name) {
        log.debug("Finding role by name: {}", name);
        return roleRepository.findByName(name.toUpperCase())
                .map(roleMapper::toResponseDto);
    }

    @Override
    public Set<Role> findRoleEntitiesByNames(Set<String> roleNames) {
        log.debug("Finding role entities by names: {}", roleNames);
        Set<String> upperCaseNames = roleNames.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toSet());
        return roleRepository.findByNameIn(upperCaseNames);
    }

    @Override
    public boolean existsByName(String name) {
        log.debug("Checking if role exists by name: {}", name);
        return roleRepository.existsByName(name.toUpperCase());
    }

    @Override
    public Set<String> validateRoleNames(Set<String> roleNames) {
        log.debug("Validating role names: {}", roleNames);
        Set<String> availableRoles = getAllRoleNames();

        return roleNames.stream()
                .map(String::toString)
                .filter(roleName -> !availableRoles.contains(roleName))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getAllRoleNames() {
        log.debug("Getting all role names");
        return roleRepository.findAllRoleNames();
    }

    @Override
    @Transactional
    public RoleResponseDto createRole(String name, String description) {
        log.info("Creating new role: {}", name);

        // Check if role already exists
        if (existsByName(name)) {
            throw new RuntimeException("Role already exists: " + name);
        }

        // Create new role
        Role role = Role.builder()
                .name(name.trim())
                .description(description != null ? description.trim() : null)
                .build();

        Role savedRole = roleRepository.save(role);
        log.info("Role created successfully: {}", savedRole.getName());

        return roleMapper.toResponseDto(savedRole);
    }

    @Override
    @Transactional
    public RoleResponseDto updateRole(Long roleId, String name, String description) {
        log.info("Updating role with ID: {}", roleId);

        Role existingRole = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));

        // Update name if provided and different
        if (name != null && !name.trim().isEmpty() && !name.trim().equals(existingRole.getName())) {
            // Check if new name already exists
            if (existsByName(name)) {
                throw new RuntimeException("Role name already exists: " + name);
            }
            existingRole.setName(name.trim());
        }

        // Update description if provided
        if (description != null) {
            existingRole.setDescription(description.trim().isEmpty() ? null : description.trim());
        }

        Role updatedRole = roleRepository.save(existingRole);
        log.info("Role updated successfully: {}", updatedRole.getName());

        return roleMapper.toResponseDto(updatedRole);
    }

    @Override
    @Transactional
    public void deleteRole(Long roleId) {
        log.info("Deleting role with ID: {}", roleId);

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));

        // Check if role is in use by any users
        if (!role.getUsers().isEmpty()) {
            throw new RuntimeException("Cannot delete role '" + role.getName() + "' as it is assigned to " +
                    role.getUsers().size() + " user(s)");
        }

        // Prevent deletion of default roles
        if (isDefaultRole(role.getName())) {
            throw new RuntimeException("Cannot delete default role: " + role.getName());
        }

        roleRepository.delete(role);
        log.info("Role deleted successfully: {}", role.getName());
    }

    @Override
    @Transactional
    public void initializeDefaultRoles() {
        log.info("Initializing default roles");

        createRoleIfNotExists(Role.GENERAL_USER, Role.GENERAL_USER_DESCRIPTION);
        createRoleIfNotExists(Role.PROFESSIONAL, Role.PROFESSIONAL_DESCRIPTION);
        createRoleIfNotExists(Role.BUSINESS_OWNER, Role.BUSINESS_OWNER_DESCRIPTION);
        createRoleIfNotExists(Role.ADMIN, Role.ADMIN_DESCRIPTION);

        log.info("Default roles initialization completed");
    }

    /**
     * Helper method to create role if it doesn't exist
     *
     * @param roleName    the role name
     * @param description the role description
     */
    private void createRoleIfNotExists(String roleName, String description) {
        if (!roleRepository.existsByName(roleName)) {
            Role role = Role.builder()
                    .name(roleName)
                    .description(description)
                    .build();
            roleRepository.save(role);
            log.debug("Created role: {}", roleName);
        } else {
            log.debug("Role already exists: {}", roleName);
        }
    }

    /**
     * Check if role is a default system role
     *
     * @param roleName the role name to check
     * @return true if it's a default role, false otherwise
     */
    private boolean isDefaultRole(String roleName) {
        return Role.GENERAL_USER.equals(roleName) ||
                Role.PROFESSIONAL.equals(roleName) ||
                Role.BUSINESS_OWNER.equals(roleName) ||
                Role.ADMIN.equals(roleName);
    }
}