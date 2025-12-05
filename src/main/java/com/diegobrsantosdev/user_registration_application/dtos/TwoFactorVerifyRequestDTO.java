package com.diegobrsantosdev.user_registration_application.dtos;

public record TwoFactorVerifyRequestDTO(
        String email,
        int code
) {}
