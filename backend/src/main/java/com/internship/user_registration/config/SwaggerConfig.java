package com.internship.user_registration.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI configuration
 * Provides API documentation for the user registration system
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI userRegistrationOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Registration API")
                        .description("A multi-role user registration system API built with Spring Boot")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Internship Project")
                                .email("intern@example.com")
                                .url("https://github.com/username/user-registration"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development server"),
                        new Server()
                                .url("https://api.example.com")
                                .description("Production server (when deployed)")
                ));
    }
}