package com.internship.user_registration.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI configuration
 * Provides comprehensive API documentation for the user registration system
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI userRegistrationOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Registration API")
                        .description("A multi-role user registration system API built with Spring Boot. " +
                                "Features include user registration, authentication, profile management, " +
                                "and admin panel for user and role management.")
                        .version("1.0.0"))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development server"),
                        new Server()
                                .url("https://api.example.com")
                                .description("Production server (when deployed)")
                ))
                .components(new Components()
                        .addSecuritySchemes("JWT", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                                .description("JWT token for authentication. Format: Bearer <token>")))
                .addSecurityItem(new SecurityRequirement()
                        .addList("JWT"));
    }
}