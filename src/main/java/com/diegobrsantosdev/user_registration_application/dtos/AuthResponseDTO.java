package com.diegobrsantosdev.user_registration_application.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO implements Serializable {
    private String token;
    private UserResponseDTO user;
}