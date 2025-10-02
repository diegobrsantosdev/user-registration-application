package com.diegobrsantosdev.user_registration_application.services;

import com.diegobrsantosdev.user_registration_application.dtos.PasswordDTO;
import com.diegobrsantosdev.user_registration_application.dtos.UserRegisterDTO;
import com.diegobrsantosdev.user_registration_application.dtos.UserResponseDTO;
import com.diegobrsantosdev.user_registration_application.dtos.UserUpdateDTO;
import com.diegobrsantosdev.user_registration_application.entities.User;
import com.diegobrsantosdev.user_registration_application.exceptions.*;
import com.diegobrsantosdev.user_registration_application.mappers.UserMapper;
import com.diegobrsantosdev.user_registration_application.repositories.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    // ========= CREATE =========
    @Transactional
    public UserResponseDTO registerUser(UserRegisterDTO dto) {
        if (userRepository.findByCpf(dto.cpf()).isPresent()) {
            throw new DuplicateCpfException("There is already a user with this CPF");
        }

        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new DuplicateEmailException("There is already a user with this email");
        }

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));

        User savedUser = userRepository.save(user);
        return UserResponseDTO.fromEntity(savedUser);
    }


    // ========= READ =========
    public UserResponseDTO getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserResponseDTO.fromEntity(user);
    }

    public Optional<UserResponseDTO> getUserByCpf(String cpf) {
        return userRepository.findByCpf(cpf)
                .map(UserResponseDTO::fromEntity);
    }

    public Optional<UserResponseDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserResponseDTO::fromEntity);
    }

    public Page<UserResponseDTO> listAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserResponseDTO::fromEntity);
    }

    // ========= UPDATE =========
    @Transactional
    public UserResponseDTO updateUser(Integer id, @Valid UserUpdateDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userRepository.findByCpf(dto.cpf())
                .filter(u -> !u.getId().equals(id))
                .ifPresent(u -> { throw new ResourceAlreadyExistsException("CPF already registered"); });

        userRepository.findByEmail(dto.email())
                .filter(u -> !u.getId().equals(id))
                .ifPresent(u -> { throw new ResourceAlreadyExistsException("Email already registered"); });

        userMapper.updateEntityFromDto(user, dto);
        User updated = userRepository.saveAndFlush(user);
        return UserResponseDTO.fromEntity(updated);
    }

    @Transactional
    public void updatePassword(Integer userId, PasswordDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
            throw new IncorrectPasswordException("Incorrect current password");
        }

        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.saveAndFlush(user);
    }

    // ========= DELETE =========
    @Transactional
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }

    private void checkDuplicate(String fieldName, Optional<?> existing) {
        if (existing.isPresent()) {
            throw new ResourceAlreadyExistsException(fieldName + " already registered. Try another");
        }
    }


}
