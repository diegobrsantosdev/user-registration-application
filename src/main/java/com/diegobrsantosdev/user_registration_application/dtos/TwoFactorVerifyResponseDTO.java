package com.diegobrsantosdev.user_registration_application.dtos;

public record TwoFactorVerifyResponseDTO(
        String message,
        String token
) {}
