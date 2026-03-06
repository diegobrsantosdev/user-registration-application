package com.diegobrsantosdev.user_registration_application.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@AllArgsConstructor
public class JwtProperties {
    private final String secret;
    private final long expiration;
}