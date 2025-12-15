package com.diegobrsantosdev.user_registration_application.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
            .info(new Info().title("User Registration API")
            .description("""
User Registration, Authentication and Management API

This API provides a complete solution for user registration, authentication, authorization, and management, including role-based access control (RBAC) and Two-Factor Authentication (2FA) for enhanced security.

It is designed following best practices for security, scalability, and clean architecture, making it suitable for real-world production environments.

Main Features:

- Authentication & Security

- User registration and login with JWT-based authentication

- Two-Factor Authentication (2FA) using time-based codes (TOTP)

- Secure login flow with conditional 2FA validation

- Passwords securely stored using strong encryption

- JWT tokens include user roles as claims for authorization

- Sensitive data is never exposed through the API
"""
            )
            .version("v1.0"));
    }

    //http://localhost:8080/swagger-ui/index.html
}