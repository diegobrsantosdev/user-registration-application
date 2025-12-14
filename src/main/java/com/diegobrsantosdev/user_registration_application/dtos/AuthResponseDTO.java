package com.diegobrsantosdev.user_registration_application.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


public record AuthResponseDTO(
        String token,
        boolean twoFactorEnabled,
        boolean requires2FA,
        UserResponseDTO user
) {}