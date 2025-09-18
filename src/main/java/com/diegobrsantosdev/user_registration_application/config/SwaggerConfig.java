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
API for user registration and management.

Main features:
- User registration, query, update, deletion, password change, and paginated listing.

Notes:
- Passwords are securely stored (encrypted).
- Sensitive data is never exposed by the API.
- Using HTTPS is strongly recommended for all integrations.
"""
            )
            .version("v1.0"));
    }

    //http://localhost:8080/swagger-ui/index.html
}