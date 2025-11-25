package com.diegobrsantosdev.user_registration_application.dtos;

import com.diegobrsantosdev.user_registration_application.models.Gender;
import com.diegobrsantosdev.user_registration_application.validators.ValidCpf;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

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
    @Size(min = 11, max = 11, message = "CPF must have exactly 11 digits")
    @Pattern(regexp = "\\d{11}", message = "CPF must contain only digits")
    @ValidCpf
    String cpf,

    @NotBlank(message = "RG is required")
    String rg,

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\d{10,11}$", message = "Phone must have 10 or 11 digits (only numbers, including DDD)")
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
    @Pattern(regexp = "[A-Z]{2}", message = "State (UF) must have 2 uppercase letters (e.g. SP)")
    String state,


    @NotBlank(message = "ZIP code is required")
    @Pattern(regexp = "\\d{8}", message = "ZIP code must have 8 digits")
    String zipCode,

    @NotNull(message = "gender is required")
    Gender gender,

    @NotNull(message = "dateOfBirth is required")
    LocalDate dateOfBirth,

    String profilePictureUrl,

    @NotNull(message = "Terms acceptance is required")
    Boolean termsAccepted




    //CREATEDAT/UPDATEDAT are automatic

) {}