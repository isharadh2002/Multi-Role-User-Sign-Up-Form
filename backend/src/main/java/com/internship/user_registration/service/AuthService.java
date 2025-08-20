package com.internship.user_registration.service;

import com.internship.user_registration.dto.LoginRequestDto;
import com.internship.user_registration.dto.LoginResponseDto;

/**
 * Service interface for authentication operations
 */
public interface AuthService {

    /**
     * Authenticate user and generate JWT token
     *
     * @param loginRequest login credentials
     * @return LoginResponseDto containing token and user details
     * @throws RuntimeException if credentials are invalid
     */
    LoginResponseDto authenticate(LoginRequestDto loginRequest);

    /**
     * Get current authenticated user details
     *
     * @param email user email from JWT token
     * @return LoginResponseDto with user details (without new token)
     */
    LoginResponseDto getCurrentUser(String email);
}