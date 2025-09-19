package com.diegobrsantosdev.user_registration_application.viaCep;

public record CepResponseDTO(
    String zipCode,
    String address,
    String complement,
    String neighborhood,
    String city,
    String state
) {}