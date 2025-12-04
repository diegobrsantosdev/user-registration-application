package com.diegobrsantosdev.user_registration_application.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (only for testing or REST APIs)
                .csrf(AbstractHttpConfigurer::disable)

                // Configure authorization
                .authorizeHttpRequests(auth -> auth
                        // Public routes
                        .requestMatchers(
                                "/auth/login",
                                "/auth/register",
                                "/api/v1/cep/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/h2-console/**"
                        ).permitAll()

                        // Admin routes
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Any other route requires authentication
                        .anyRequest().authenticated()
                )

                // Enable HTTP Basic authentication (for role testing)
                .httpBasic(Customizer.withDefaults())

                // Exception handling if is not authorized
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
                        )
                )

                // Allow H2 console
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        return http.build();
    }


    @Bean
    public UserDetailsService users(PasswordEncoder passwordEncoder) {
        var user = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("1234")) // cript with BCrypt
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

}