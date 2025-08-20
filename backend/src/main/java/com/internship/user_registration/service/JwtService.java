package com.internship.user_registration.service;

import com.internship.user_registration.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Service interface for JWT operations
 */
public interface JwtService {

    /**
     * Generate JWT token from user entity
     *
     * @param user the user entity
     * @return JWT token string
     */
    String generateToken(User user);

    /**
     * Generate JWT token from UserDetails
     *
     * @param userDetails Spring Security UserDetails
     * @return JWT token string
     */
    String generateToken(UserDetails userDetails);

    /**
     * Extract username (email) from token
     *
     * @param token JWT token
     * @return username (email)
     */
    String extractUsername(String token);

    /**
     * Extract user ID from token
     *
     * @param token JWT token
     * @return user ID
     */
    Long extractUserId(String token);

    /**
     * Check if token is valid
     *
     * @param token JWT token
     * @param userDetails Spring Security UserDetails
     * @return true if valid, false otherwise
     */
    boolean isTokenValid(String token, UserDetails userDetails);

    /**
     * Check if token is expired
     *
     * @param token JWT token
     * @return true if expired, false otherwise
     */
    boolean isTokenExpired(String token);
}