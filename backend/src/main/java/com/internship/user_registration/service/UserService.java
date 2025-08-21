package com.internship.user_registration.service;

import com.internship.user_registration.dto.PasswordChangeDto;
import com.internship.user_registration.dto.UserRegistrationDto;
import com.internship.user_registration.dto.UserResponseDto;
import com.internship.user_registration.dto.UserUpdateDto;
import com.internship.user_registration.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for User operations
 * Follows the Service Layer pattern for business logic abstraction
 */
public interface UserService {

    /**
     * Register a new user
     *
     * @param registrationDto the user registration data
     * @return UserResponseDto containing user information
     * @throws RuntimeException if email or phone already exists
     */
    UserResponseDto registerUser(UserRegistrationDto registrationDto);

    /**
     * Find user by ID
     *
     * @param userId the user ID
     * @return Optional containing UserResponseDto if found
     */
    Optional<UserResponseDto> findUserById(Long userId);

    /**
     * Find user by email
     *
     * @param email the email address
     * @return Optional containing UserResponseDto if found
     */
    Optional<UserResponseDto> findUserByEmail(String email);

    /**
     * Find users by country
     *
     * @param country the country code
     * @return list of users from the specified country
     */
    List<UserResponseDto> findUsersByCountry(String country);

    /**
     * Find all users (Admin only)
     *
     * @return list of all users
     */
    List<UserResponseDto> findAllUsers();

    /**
     * Update user profile
     *
     * @param email user email
     * @param updateDto updated user data
     * @return updated UserResponseDto
     * @throws RuntimeException if user not found or validation fails
     */
    UserResponseDto updateUserProfile(String email, UserUpdateDto updateDto);

    /**
     * Delete user by ID (Admin only)
     *
     * @param userId the user ID to delete
     * @throws RuntimeException if user not found
     */
    void deleteUser(Long userId);

    /**
     * Check if email already exists
     *
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Check if phone number already exists
     *
     * @param phoneNumber the phone number to check
     * @return true if phone number exists, false otherwise
     */
    boolean existsByPhoneNumber(String phoneNumber);

    /**
     * Get count of users by role name
     *
     * @param roleName the role name
     * @return count of users with the specified role
     */
    long countUsersByRole(String roleName);

    /**
     * Validate user registration data
     * Additional business logic validation beyond bean validation
     *
     * @param registrationDto the registration data
     * @throws RuntimeException if validation fails
     */
    void validateUserRegistration(UserRegistrationDto registrationDto);

    /**
     * Change user password
     *
     * @param email user email
     * @param passwordChangeDto password change data
     * @throws RuntimeException if current password is invalid or validation fails
     */
    void changePassword(String email, PasswordChangeDto passwordChangeDto);

}