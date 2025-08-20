package com.internship.user_registration.service.impl;

import com.internship.user_registration.entity.User;
import com.internship.user_registration.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * Custom UserDetailsService implementation for Spring Security
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);

        User user = userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        log.debug("User found with roles: {}", user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toList()));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(user.getRoles().stream()
                        .map(role -> {
                            // Convert role name to proper authority format
                            String roleName = role.getName();
                            // Handle "Admin" role specifically
                            if ("Admin".equals(roleName)) {
                                return new SimpleGrantedAuthority("ROLE_ADMIN");
                            }
                            // For other roles, convert spaces to underscores and uppercase
                            String authority = "ROLE_" + roleName.toUpperCase().replace(" ", "_");
                            log.debug("Converting role '{}' to authority '{}'", roleName, authority);
                            return new SimpleGrantedAuthority(authority);
                        })
                        .collect(Collectors.toList()))
                .build();
    }
}