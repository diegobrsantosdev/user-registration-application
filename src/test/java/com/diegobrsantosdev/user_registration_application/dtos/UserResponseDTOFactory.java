package com.diegobrsantosdev.user_registration_application.dtos;

import java.time.LocalDateTime;

public class UserResponseDTOFactory {
    public static UserResponseDTO sample() {
        return new UserResponseDTO(
                1,
                "Name",
                "email@email.com",
                "12345678901",
                "81999999999",
                "Rua Teste",
                "123",
                "Apto 101",
                "Centro",
                "Recife",
                "PE",
                "50000000",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public static UserResponseDTO withCustom(
            Integer id,
            String name,
            String email,
            String cpf
    ) {
        return new UserResponseDTO(
                id,
                name,
                email,
                cpf,
                "81999999999",
                "Rua Teste",
                "123",
                "Apto 101",
                "Centro",
                "Recife",
                "PE",
                "50000000",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
