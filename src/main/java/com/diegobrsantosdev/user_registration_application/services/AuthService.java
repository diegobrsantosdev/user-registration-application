package com.diegobrsantosdev.user_registration_application.services;

import com.diegobrsantosdev.user_registration_application.dtos.AuthResponseDTO;
import com.diegobrsantosdev.user_registration_application.dtos.LoginRequestDTO;
import com.diegobrsantosdev.user_registration_application.dtos.UserRegisterDTO;
import com.diegobrsantosdev.user_registration_application.dtos.UserResponseDTO;
import com.diegobrsantosdev.user_registration_application.exceptions.InvalidCredentialsException;
import com.diegobrsantosdev.user_registration_application.models.User;
import com.diegobrsantosdev.user_registration_application.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    public AuthResponseDTO login(LoginRequestDTO request) {
        User user = userService.findByEmail(request.getEmail());

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password.");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponseDTO(token, UserResponseDTO.fromEntity(user));
    }


    public AuthResponseDTO register(UserRegisterDTO request) {
        if (userService.existsByEmail(request.email())) {
            throw new InvalidCredentialsException("Email already in use.");
        }
        if (userService.existsByCpf(request.cpf())) {
            throw new InvalidCredentialsException("CPF already in use.");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setCpf(request.cpf());
        user.setPhone(request.phone());
        user.setAddress(request.address());
        user.setNumber(request.number());
        user.setComplement(request.complement());
        user.setNeighborhood(request.neighborhood());
        user.setCity(request.city());
        user.setState(request.state());
        user.setZipCode(request.zipCode());

        user = userService.save(user);
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponseDTO(token, UserResponseDTO.fromEntity(user));
    }


}
