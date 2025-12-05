package com.diegobrsantosdev.user_registration_application.dtos;

public record TwoFactorSetupResponseDTO(
        String secret,
        String qrCode
) {}
