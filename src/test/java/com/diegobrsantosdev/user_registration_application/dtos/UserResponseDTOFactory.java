package com.diegobrsantosdev.user_registration_application.dtos;

import com.diegobrsantosdev.user_registration_application.models.Gender;
import com.diegobrsantosdev.user_registration_application.models.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public class UserResponseDTOFactory {
    public static UserResponseDTO sample() {
        return new UserResponseDTO(
                1,
                "João Silva",
                "joao@email.com",
                "12345678901",
                "12345678",
                "11999998888",
                "Rua Alpha",
                "100",
                "Apto 12",
                "Centro",
                "São Paulo",
                "SP",
                "01001000",
                Gender.MALE,
                LocalDate.of(1990, 5, 15),
                null,
                true,
                LocalDateTime.now(),
                LocalDateTime.now(),
                Set.of(Role.USER),
                false
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
                "12345678",
                "11999998888",
                "Rua Alpha",
                "100",
                "Apto 12",
                "Centro",
                "São Paulo",
                "SP",
                "01001000",
                Gender.MALE,
                LocalDate.of(1990, 5, 15),
                null,
                true,
                LocalDateTime.now(),
                LocalDateTime.now(),
                Set.of(Role.USER),
                false
        );
    }
}
