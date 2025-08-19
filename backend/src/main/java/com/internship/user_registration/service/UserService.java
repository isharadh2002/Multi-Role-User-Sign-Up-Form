package com.internship.user_registration.service;

import com.internship.user_registration.dto.UserRegistrationDto;
import com.internship.user_registration.dto.UserResponseDto;
import com.internship.user_registration.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for user management operations
 * Following the Service Layer pattern for business logic separation
 */
public interface UserService {

    /**
     * Register a new user with the provided registration data
     *
     * @param registrationDto the user registration data
     * @return UserResponseDto containing the created user information
     * @throws IllegalArgumentException if validation fails or user already exists
     */
    UserResponseDto registerUser(UserRegistrationDto registrationDto);

    /**
     * Find user by email address
     *
     * @param email the email to search for
     * @return Optional containing UserResponseDto if found
     */
    Optional<UserResponseDto> findUserByEmail(String email);

    /**
     * Find user by ID
     *
     * @param userId the user ID to search for
     * @return Optional containing UserResponseDto if found
     */
    Optional<UserResponseDto> findUserById(Long userId);

    /**
     * Check if user exists by email
     *
     * @param email the email to check
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Check if user exists by phone number
     *
     * @param phoneNumber the phone number to check
     * @return true if user exists, false otherwise
     */
    boolean existsByPhoneNumber(String phoneNumber);

    /**
     * Get all users (for admin purposes - optional enhancement)
     *
     * @return list of all users
     */
    List<UserResponseDto> getAllUsers();

    /**
     * Validate password strength
     *
     * @param password the password to validate
     * @return true if password meets requirements
     */
    boolean isValidPassword(String password);

    /**
     * Validate that passwords match
     *
     * @param password the original password
     * @param confirmPassword the confirmation password
     * @return true if passwords match
     */
    boolean passwordsMatch(String password, String confirmPassword);
}