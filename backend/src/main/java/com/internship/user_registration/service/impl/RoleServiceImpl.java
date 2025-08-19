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
                .map(String::toUpperCase)
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
    public void initializeDefaultRoles() {
        log.info("Initializing default roles");

        createRoleIfNotExists(Role.GENERAL_USER, Role.GENERAL_USER_DESCRIPTION);
        createRoleIfNotExists(Role.PROFESSIONAL, Role.PROFESSIONAL_DESCRIPTION);
        createRoleIfNotExists(Role.BUSINESS_OWNER, Role.BUSINESS_OWNER_DESCRIPTION);

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
}