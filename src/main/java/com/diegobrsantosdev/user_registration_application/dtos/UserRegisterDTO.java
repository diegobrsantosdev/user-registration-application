package com.diegobrsantosdev.user_registration_application.dtos;

import jakarta.validation.constraints.*;

public record UserRegisterDTO(
    @NotBlank(message = "Name is required")
    String name,

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    String email,

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    String password,

    @NotBlank(message = "CPF is required")
    String cpf,

    @NotBlank(message = "Phone is required")
    String phone,

    @NotBlank(message = "Address is required")
    String address,

    @NotBlank(message = "Number is required")
    String number,

    String complement,

    @NotBlank(message = "Neighborhood is required")
    String neighborhood,

    @NotBlank(message = "City is required")
    String city,

    @NotBlank(message = "State is required")
    String state,

    @NotBlank(message = "ZIP code is required")
    String zipCode
) {}