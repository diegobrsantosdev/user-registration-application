package com.diegobrsantosdev.user_registration_application.services;

import com.diegobrsantosdev.user_registration_application.dtos.*;
import com.diegobrsantosdev.user_registration_application.models.Role;
import com.diegobrsantosdev.user_registration_application.models.User;
import com.diegobrsantosdev.user_registration_application.exceptions.*;
import com.diegobrsantosdev.user_registration_application.mappers.UserMapper;
import com.diegobrsantosdev.user_registration_application.repositories.UserRepository;
import com.diegobrsantosdev.user_registration_application.shared.ApiMessages;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
            throw new DuplicateCpfException("CPF already registered");
        }

        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new DuplicateEmailException("Email already registered");
        }

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));

        user.setRoles(Set.of(Role.USER));

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
                .ifPresent(u -> {
                    throw new ResourceAlreadyExistsException("CPF already registered");
                });

        userRepository.findByEmail(dto.email())
                .filter(u -> !u.getId().equals(id))
                .ifPresent(u -> {
                    throw new ResourceAlreadyExistsException("Email already registered");
                });

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

    @Transactional
    public User promoteToAdmin(Integer id) {
        User user = findById(id);

        if (user.getRoles().contains(Role.ADMIN)) {
            throw new InvalidDataException(ApiMessages.USER_ALREADY_ADMIN);
        }

        Set<Role> roles = new HashSet<>(user.getRoles());
        roles.add(Role.ADMIN);
        user.setRoles(roles);
        return save(user);
    }

    // ========= DELETE =========
    @Transactional
    public MessageResponseDTO deleteOwnUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userRepository.delete(user);
        return new MessageResponseDTO("Your account has been successfully deleted");
    }

    @Transactional
    public MessageResponseDTO deleteUserAsAdmin(Integer idToDelete, Integer adminId) {
        User user = userRepository.findById(idToDelete)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getId().equals(adminId)) {
            throw new IllegalActionException("You cannot delete yourself");
        }

        if (user.getRoles().stream().anyMatch(role -> role == Role.ADMIN)
                && userRepository.countByRoles(Role.ADMIN) == 1) {
            throw new IllegalActionException("Cannot delete the last admin");
        }


        userRepository.delete(user);
        return new MessageResponseDTO("User deleted successfully");
    }


    // ========= OTHERS =========


    private void checkDuplicate(String fieldName, Optional<?> existing) {
        if (existing.isPresent()) {
            throw new ResourceAlreadyExistsException(fieldName + " already registered. Try another");
        }
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public boolean existsByCpf(String cpf) {
        return userRepository.existsByCpf(cpf);
    }

    public boolean existsByRg(String rg) {
        return userRepository.existsByRg(rg);
    }

    public User findById(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public void deleteUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.delete(user);
    }

    public long countByRoles(Role role) {
        return userRepository.countByRoles(role);
    }

}