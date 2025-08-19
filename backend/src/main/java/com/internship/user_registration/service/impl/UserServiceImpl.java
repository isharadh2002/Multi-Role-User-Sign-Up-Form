package com.internship.user_registration.service.impl;

import com.internship.user_registration.dto.UserRegistrationDto;
import com.internship.user_registration.dto.UserResponseDto;
import com.internship.user_registration.entity.Role;
import com.internship.user_registration.entity.User;
import com.internship.user_registration.mapper.UserMapper;
import com.internship.user_registration.repository.UserRepository;
import com.internship.user_registration.service.RoleService;
import com.internship.user_registration.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service implementation for User operations
 * Implements business logic following SOLID principles
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponseDto registerUser(UserRegistrationDto registrationDto) {
        log.info("Starting user registration for email: {}", registrationDto.getEmail());

        // Validate registration data
        validateUserRegistration(registrationDto);

        // Convert DTO to entity
        User user = userMapper.toEntity(registrationDto);

        // Hash password
        String hashedPassword = passwordEncoder.encode(registrationDto.getPassword());
        user.setPasswordHash(hashedPassword);

        // Get and assign roles
        Set<String> roleNames = userMapper.extractRoleNames(registrationDto);
        Set<Role> roles = roleService.findRoleEntitiesByNames(roleNames);

        // Validate that all requested roles were found
        if (roles.size() != roleNames.size()) {
            Set<String> foundRoleNames = roles.stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());
            Set<String> missingRoles = roleNames.stream()
                    .filter(name -> !foundRoleNames.contains(name))
                    .collect(Collectors.toSet());
            throw new RuntimeException("Invalid roles: " + missingRoles);
        }

        // Assign roles to user
        roles.forEach(user::addRole);

        // Save user
        User savedUser = userRepository.save(user);

        log.info("User registered successfully with ID: {}", savedUser.getId());
        return userMapper.toResponseDto(savedUser);
    }

    @Override
    public Optional<UserResponseDto> findUserById(Long userId) {
        log.debug("Finding user by ID: {}", userId);
        return userRepository.findById(userId)
                .map(userMapper::toResponseDto);
    }

    @Override
    public Optional<UserResponseDto> findUserByEmail(String email) {
        log.debug("Finding user by email: {}", email);
        return userRepository.findByEmail(email.toLowerCase().trim())
                .map(userMapper::toResponseDto);
    }

    @Override
    public List<UserResponseDto> findUsersByCountry(String country) {
        log.debug("Finding users by country: {}", country);
        return userRepository.findByCountry(country.toUpperCase())
                .stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByEmail(String email) {
        log.debug("Checking if email exists: {}", email);
        return userRepository.existsByEmail(email.toLowerCase().trim());
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        log.debug("Checking if phone number exists: {}", phoneNumber);
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public long countUsersByRole(String roleName) {
        log.debug("Counting users by role: {}", roleName);
        return userRepository.countByRoleName(roleName.toUpperCase());
    }

    @Override
    public void validateUserRegistration(UserRegistrationDto registrationDto) {
        log.debug("Validating user registration data");

        // Check if email already exists
        if (existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("Email already exists: " + registrationDto.getEmail());
        }

        // Check if phone number already exists (if provided)
        if (registrationDto.getPhoneNumber() != null &&
                !registrationDto.getPhoneNumber().trim().isEmpty() &&
                existsByPhoneNumber(registrationDto.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists: " + registrationDto.getPhoneNumber());
        }

        // Validate password confirmation
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            throw new RuntimeException("Password and confirm password do not match");
        }

        // Validate password length
        if(registrationDto.getPassword().length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters long");
        }

        // Validate roles
        Set<String> invalidRoles = roleService.validateRoleNames(registrationDto.getRoles());
        if (!invalidRoles.isEmpty()) {
            throw new RuntimeException("Invalid roles: " + invalidRoles);
        }

        log.debug("User registration validation completed successfully");
    }
}