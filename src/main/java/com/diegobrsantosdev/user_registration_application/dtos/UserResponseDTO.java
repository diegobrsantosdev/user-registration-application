package com.diegobrsantosdev.user_registration_application.dtos;

import com.diegobrsantosdev.user_registration_application.models.Gender;
import com.diegobrsantosdev.user_registration_application.models.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserResponseDTO(
    Integer id,
    String name,
    String email,
    String cpf,
    String rg,
    String phone,
    String address,
    String number,
    String complement,
    String neighborhood,
    String city,
    String state,
    String zipCode,
    Gender gender,
    LocalDate dateOfBirth,
    String profilePictureUrl,
    Boolean termsAccepted,
    LocalDateTime createdAt,
    LocalDateTime updatedAt

) {
    public static UserResponseDTO fromEntity(User user) {
        if (user == null) return null;
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCpf(),
                user.getRg(),
                user.getPhone(),
                user.getAddress(),
                user.getNumber(),
                user.getComplement(),
                user.getNeighborhood(),
                user.getCity(),
                user.getState(),
                user.getZipCode(),
                user.getGender(),
                user.getDateOfBirth(),
                user.getProfilePictureUrl(),
                user.getTermsAccepted(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}

