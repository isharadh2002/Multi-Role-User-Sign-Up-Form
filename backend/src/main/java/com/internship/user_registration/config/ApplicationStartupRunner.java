package com.internship.user_registration.config;

import com.internship.user_registration.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Application startup runner to initialize default data
 * Runs after the Spring Boot application has started
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationStartupRunner implements CommandLineRunner {

    private final RoleService roleService;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting application initialization...");

        try {
            // Initialize default roles
            roleService.initializeDefaultRoles();

            log.info("Application initialization completed successfully");

        } catch (Exception e) {
            log.error("Error during application initialization: {}", e.getMessage(), e);
            throw e; // Re-throw to prevent application startup if critical initialization fails
        }
    }
}