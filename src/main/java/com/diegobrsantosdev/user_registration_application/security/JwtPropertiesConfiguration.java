package com.diegobrsantosdev.user_registration_application.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtPropertiesConfiguration {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    @Bean
    public JwtProperties jwtProperties() {
        return new JwtProperties(secret, expiration);
    }

    @Bean
    public JwtUtil jwtUtil(JwtProperties properties) {
        return new JwtUtil(properties);
    }
}
