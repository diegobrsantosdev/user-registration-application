package com.diegobrsantosdev.user_registration_application.mappers;

import com.diegobrsantosdev.user_registration_application.dtos.UserRegisterDTO;
import com.diegobrsantosdev.user_registration_application.dtos.UserUpdateDTO;
import com.diegobrsantosdev.user_registration_application.entities.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRegisterDTO dto) {
        if (dto == null) return null;

        return User.builder()
                .name(dto.name())
                .email(dto.email())
                .cpf(dto.cpf())
                .phone(dto.phone())
                .address(dto.address())
                .number(dto.number())
                .complement(dto.complement())
                .neighborhood(dto.neighborhood())
                .city(dto.city())
                .state(dto.state())
                .zipCode(dto.zipCode())
                .build();

    }

    public void updateEntityFromDto(User user, UserUpdateDTO dto) {
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setCpf(dto.cpf());
        user.setPhone(dto.phone());
        user.setAddress(dto.address());
        user.setNumber(dto.number());
        user.setComplement(dto.complement());
        user.setNeighborhood(dto.neighborhood());
        user.setCity(dto.city());
        user.setState(dto.state());
        user.setZipCode(dto.zipCode());
    }

    //password must be set and cryptographed out of mapper


}
