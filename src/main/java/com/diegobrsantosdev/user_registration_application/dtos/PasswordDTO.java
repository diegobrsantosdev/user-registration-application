package com.diegobrsantosdev.user_registration_application.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordDTO(
        @NotBlank(message = "Field 'currentPassword' must not be blank")
        String currentPassword,

        @NotBlank(message = "Field 'newPassword' must not be blank")
        @Size(min = 8, message = "New password must have at least 8 characters")
        String newPassword

) {}
