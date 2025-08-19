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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Implementation of UserService following Service Layer pattern
 * Handles all business logic related to user operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    // Password validation pattern
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );

    @Override
    public UserResponseDto registerUser(UserRegistrationDto registrationDto) {
        log.info("Attempting to register user with email: {}", registrationDto.getEmail());

        // Validate business rules
        validateRegistrationData(registrationDto);

        // Check if user already exists
        if (userRepository.existsByEmail(registrationDto.getEmail().toLowerCase().trim())) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        if (userRepository.existsByPhoneNumber(registrationDto.getPhoneNumber())) {
            throw new IllegalArgumentException("User with this phone number already exists");
        }

        // Convert DTO to entity
        User user = userMapper.toEntity(registrationDto);

        // Hash password
        user.setPasswordHash(passwordEncoder.encode(registrationDto.getPassword()));

        // Assign roles
        Set<String> roleNames = userMapper.extractRoleNames(registrationDto);
        Set<Role> roles = roleService.findRolesByNames(roleNames);

        if (roles.size() != roleNames.size()) {
            throw new IllegalArgumentException("One or more invalid roles provided");
        }

        // Add roles to user
        for (Role role : roles) {
            user.addRole(role);
        }

        // Save user
        User savedUser = userRepository.save(user);

        log.info("Successfully registered user with ID: {} and email: {}",
                savedUser.getId(), savedUser.getEmail());

        return userMapper.toResponseDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDto> findUserByEmail(String email) {
        log.debug("Finding user by email: {}", email);

        return userRepository.findByEmail(email.toLowerCase().trim())
                .map(userMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDto> findUserById(Long userId) {
        log.debug("Finding user by ID: {}", userId);

        return userRepository.findById(userId)
                .map(userMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email.toLowerCase().trim());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        log.debug("Retrieving all users");

        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    @Override
    public boolean passwordsMatch(String password, String confirmPassword) {
        return password != null && password.equals(confirmPassword);
    }

    /**
     * Validate registration data according to business rules
     *
     * @param dto the registration data to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateRegistrationData(UserRegistrationDto dto) {
        // Password validation
        if (!isValidPassword(dto.getPassword())) {
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters and contain uppercase, lowercase, number, and special character"
            );
        }

        // Password confirmation
        if (!passwordsMatch(dto.getPassword(), dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Role validation
        Set<String> roleNames = userMapper.extractRoleNames(dto);
        if (!roleService.validateRoles(roleNames)) {
            throw new IllegalArgumentException("One or more invalid roles provided");
        }

        // Additional business validations can be added here
        validateEmailDomain(dto.getEmail());
    }

    /**
     * Optional: Validate email domain (can be extended for business requirements)
     *
     * @param email the email to validate
     */
    private void validateEmailDomain(String email) {
        // Example: Block certain domains if needed
        String domain = email.substring(email.lastIndexOf("@") + 1).toLowerCase();

        // Add any domain-specific validations here
        // For now, we'll just log it
        log.debug("Registering user with email domain: {}", domain);
    }
}