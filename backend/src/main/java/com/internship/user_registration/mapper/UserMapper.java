package com.internship.user_registration.mapper;

import com.internship.user_registration.dto.UserRegistrationDto;
import com.internship.user_registration.dto.UserResponseDto;
import com.internship.user_registration.entity.Role;
import com.internship.user_registration.entity.User;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Simple mapper class for converting between User entities and DTOs
 * Following the Mapper pattern for clean separation of concerns
 */
@Component
public class UserMapper {

    /**
     * Convert UserRegistrationDto to User entity
     * Note: Password hashing and role assignment should be handled in service layer
     *
     * @param dto the registration DTO
     * @return User entity (without password hash and roles)
     */
    public User toEntity(UserRegistrationDto dto) {
        if (dto == null) {
            return null;
        }

        return User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail().toLowerCase().trim())
                .phoneNumber(dto.getPhoneNumber())
                .country(dto.getCountry())
                .build();
    }

    /**
     * Convert User entity to UserResponseDto
     *
     * @param user the user entity
     * @return UserResponseDto
     */
    public UserResponseDto toResponseDto(User user) {
        if (user == null) {
            return null;
        }

        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return UserResponseDto.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .country(user.getCountry())
                .roles(roleNames)
                .createdAt(user.getCreatedAt())
                .build();
    }

    /**
     * Update existing user entity with data from DTO
     * Note: This method doesn't update password or roles
     *
     * @param user existing user entity
     * @param dto update data
     */
    public void updateEntityFromDto(User user, UserRegistrationDto dto) {
        if (user == null || dto == null) {
            return;
        }

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail().toLowerCase().trim());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setCountry(dto.getCountry());
    }

    /**
     * Extract role names from registration DTO
     * Converts role names to proper case format
     *
     * @param dto the registration DTO
     * @return set of role names in proper case
     */
    public Set<String> extractRoleNames(UserRegistrationDto dto) {
        if (dto == null || dto.getRoles() == null) {
            return Set.of();
        }

        return dto.getRoles().stream()
                .map(String::trim)
                .map(this::convertToProperCase)
                .collect(Collectors.toSet());
    }

    /**
     * Convert role name to proper case
     * Examples: "GENERAL_USER" -> "General User", "professional" -> "Professional"
     *
     * @param roleName the role name to convert
     * @return role name in proper case
     */
    private String convertToProperCase(String roleName) {
        if (roleName == null || roleName.trim().isEmpty()) {
            return roleName;
        }

        // Handle different input formats
        String normalized = roleName.trim().toLowerCase();

        // Convert underscores to spaces and capitalize each word
        String withSpaces = normalized.replace("_", " ");

        return Arrays.stream(withSpaces.split("\\s+"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                .collect(Collectors.joining(" "));
    }
}