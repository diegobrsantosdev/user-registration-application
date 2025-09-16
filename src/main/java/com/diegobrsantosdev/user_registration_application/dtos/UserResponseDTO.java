package com.diegobrsantosdev.user_registration_application.dtos;

public record UserResponseDTO(
    Integer id,
    String name,
    String email,
    String cpf,
    String phone,
    String address,
    String number,
    String complement,
    String neighborhood,
    String city,
    String state,
    String zipCode
) {}