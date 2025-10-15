package com.diegobrsantosdev.user_registration_application.service;

import com.diegobrsantosdev.user_registration_application.dtos.*;
import com.diegobrsantosdev.user_registration_application.models.User;
import com.diegobrsantosdev.user_registration_application.exceptions.*;
import com.diegobrsantosdev.user_registration_application.mappers.UserMapper;
import com.diegobrsantosdev.user_registration_application.repositories.UserRepository;
import com.diegobrsantosdev.user_registration_application.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;


    // ========= CREATE =========
    @Test
    void registerUser_ShouldThrowDuplicateCpfException_WhenCpfExists() {
        UserRegisterDTO dto = new UserRegisterDTO(
                "Name", "email@test.com", "password", "12345678901",
                "81999999999","Rua","123", "Apto","Centro","Cidade","ST","12345678"
        );
        when(userRepository.findByCpf(dto.cpf())).thenReturn(Optional.of(new User()));

        assertThrows(DuplicateCpfException.class, () -> userService.registerUser(dto));
    }

    @Test
    void registerUser_ShouldThrowDuplicateEmailException_WhenEmailExists() {
        UserRegisterDTO dto = new UserRegisterDTO(
                "Name", "email@test.com", "password", "12345678901",
                "81999999999","Rua","123", "Apto","Centro","Cidade","ST","12345678"
        );
        when(userRepository.findByCpf(dto.cpf())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.of(new User()));

        assertThrows(DuplicateEmailException.class, () -> userService.registerUser(dto));
    }

    @Test
    void registerUser_ShouldSaveAndReturnUser_WhenDataIsValid() {
        UserRegisterDTO dto = new UserRegisterDTO(
                "Name", "email@test.com", "password", "12345678901",
                "81999999999","Rua","123", "Apto","Centro","Cidade","ST","12345678"
        );
        User userEntity = new User();
        User savedUser = new User();
        UserResponseDTO responseDTO = UserResponseDTOFactory.withCustom(1,"Name","email@test.com","12345678901");

        when(userRepository.findByCpf(dto.cpf())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
        when(userMapper.toEntity(dto)).thenReturn(userEntity);
        when(passwordEncoder.encode(dto.password())).thenReturn("encoded");
        when(userRepository.save(userEntity)).thenReturn(savedUser);
        try (MockedStatic<UserResponseDTO> mocked = mockStatic(UserResponseDTO.class)) {
            mocked.when(() -> UserResponseDTO.fromEntity(savedUser)).thenReturn(responseDTO);

            UserResponseDTO result = userService.registerUser(dto);
            assertEquals(responseDTO, result);
        }
        verify(userRepository).save(userEntity);
    }

    // ========= READ =========
    @Test
    void getUserById_ShouldReturnUser_WhenExists() {
        User user = new User();
        user.setId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        UserResponseDTO dto = UserResponseDTOFactory.withCustom(1,"Name","email@test.com","12345678901");

        try (MockedStatic<UserResponseDTO> mocked = mockStatic(UserResponseDTO.class)) {
            mocked.when(() -> UserResponseDTO.fromEntity(user)).thenReturn(dto);

            UserResponseDTO result = userService.getUserById(1);
            assertEquals(dto, result);
        }
    }

    @Test
    void getUserById_ShouldThrowResourceNotFoundException_WhenNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1));
    }

    @Test
    void getUserByCpf_ShouldReturnOptionalUser_WhenExists() {
        User user = new User();
        when(userRepository.findByCpf("12345678901")).thenReturn(Optional.of(user));
        UserResponseDTO dto = UserResponseDTOFactory.withCustom(1,"Name","email@test.com","12345678901");

        try (MockedStatic<UserResponseDTO> mocked = mockStatic(UserResponseDTO.class)) {
            mocked.when(() -> UserResponseDTO.fromEntity(user)).thenReturn(dto);

            Optional<UserResponseDTO> result = userService.getUserByCpf("12345678901");
            assertTrue(result.isPresent());
            assertEquals(dto, result.get());
        }
    }

    @Test
    void getUserByCpf_ShouldReturnEmpty_WhenNotExists() {
        when(userRepository.findByCpf("12345678901")).thenReturn(Optional.empty());
        Optional<UserResponseDTO> result = userService.getUserByCpf("12345678901");
        assertTrue(result.isEmpty());
    }

    @Test
    void getUserByEmail_ShouldReturnOptionalUser_WhenExists() {
        User user = new User();
        when(userRepository.findByEmail("email@test.com")).thenReturn(Optional.of(user));
        UserResponseDTO dto = UserResponseDTOFactory.withCustom(1,"Name","email@test.com","12345678901");

        try (MockedStatic<UserResponseDTO> mocked = mockStatic(UserResponseDTO.class)) {
            mocked.when(() -> UserResponseDTO.fromEntity(user)).thenReturn(dto);

            Optional<UserResponseDTO> result = userService.getUserByEmail("email@test.com");
            assertTrue(result.isPresent());
            assertEquals(dto, result.get());
        }
    }

    @Test
    void getUserByEmail_ShouldReturnEmpty_WhenNotExists() {
        when(userRepository.findByEmail("email@test.com")).thenReturn(Optional.empty());
        Optional<UserResponseDTO> result = userService.getUserByEmail("email@test.com");
        assertTrue(result.isEmpty());
    }

    @Test
    void listAllUsers_ShouldReturnPage_WhenUsersExist() {
        Pageable pageable = PageRequest.of(0, 10);
        List<User> users = List.of(new User(), new User());
        Page<User> page = new PageImpl<>(users);

        when(userRepository.findAll(pageable)).thenReturn(page);

        try (MockedStatic<UserResponseDTO> mocked = mockStatic(UserResponseDTO.class)) {
            users.forEach(user -> mocked.when(() -> UserResponseDTO.fromEntity(user))
                    .thenReturn(UserResponseDTOFactory.sample()));
            Page<UserResponseDTO> result = userService.listAllUsers(pageable);
            assertEquals(users.size(), result.getContent().size());
        }
    }

    @Test
    void listAllUsers_ShouldReturnEmptyPage_WhenNoUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> emptyPage = new PageImpl<>(Collections.emptyList());
        when(userRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<UserResponseDTO> result = userService.listAllUsers(pageable);
        assertTrue(result.getContent().isEmpty());
    }

    // ========= UPDATE =========
    @Test
    void updateUser_ShouldReturnUpdatedUser_WhenDataIsValid() {
        UserUpdateDTO dto = new UserUpdateDTO(
                "New Name", "new@email.com", "12345678901",
                "81999999999","Rua Teste","123","Apto","Centro","Cidade","ST","12345678"
        );

        User existingUser = new User();
        existingUser.setId(1);

        User updatedUser = new User();
        updatedUser.setId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByCpf(dto.cpf())).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.of(existingUser));
        doNothing().when(userMapper).updateEntityFromDto(existingUser, dto);
        when(userRepository.saveAndFlush(existingUser)).thenReturn(updatedUser);

        try (MockedStatic<UserResponseDTO> mocked = mockStatic(UserResponseDTO.class)) {
            UserResponseDTO dtoResponse = UserResponseDTOFactory.withCustom(1,"New Name","new@email.com",dto.cpf());
            mocked.when(() -> UserResponseDTO.fromEntity(updatedUser)).thenReturn(dtoResponse);

            UserResponseDTO result = userService.updateUser(1, dto);
            assertEquals(dtoResponse, result);
        }

        verify(userRepository).saveAndFlush(existingUser);
    }

    @Test
    void updateUser_ShouldThrowResourceNotFoundException_WhenUserNotFound() {
        UserUpdateDTO dto = mock(UserUpdateDTO.class);
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(99, dto));
    }

    @Test
    void updateUser_ShouldThrowResourceAlreadyExistsException_WhenCpfOrEmailDuplicated() {
        UserUpdateDTO dto = new UserUpdateDTO(
                "Name", "email@test.com", "12345678901",
                "81999999999","Rua","123","Apto","Centro","Cidade","ST","12345678"
        );

        User existingUser = new User();
        existingUser.setId(1);

        User anotherUser = new User();
        anotherUser.setId(2);

        when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByCpf(dto.cpf())).thenReturn(Optional.of(anotherUser));

        assertThrows(ResourceAlreadyExistsException.class, () -> userService.updateUser(1, dto));

        when(userRepository.findByCpf(dto.cpf())).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.of(anotherUser));

        assertThrows(ResourceAlreadyExistsException.class, () -> userService.updateUser(1, dto));
    }

    // ========= UPDATE PASSWORD =========
    @Test
    void updatePassword_ShouldUpdate_WhenCurrentPasswordIsCorrect() {
        PasswordDTO dto = new PasswordDTO("oldPass", "newPass123");
        User user = new User();
        user.setId(1);
        user.setPassword("encodedOld");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.currentPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(dto.newPassword())).thenReturn("encodedNew");

        userService.updatePassword(1, dto);

        verify(userRepository).saveAndFlush(user);
        assertEquals("encodedNew", user.getPassword());
    }

    @Test
    void updatePassword_ShouldThrowIncorrectPasswordException_WhenCurrentPasswordIsWrong() {
        PasswordDTO dto = new PasswordDTO("wrongOld", "newPass123");
        User user = new User();
        user.setPassword("encodedOld");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.currentPassword(), user.getPassword())).thenReturn(false);

        assertThrows(IncorrectPasswordException.class, () -> userService.updatePassword(1, dto));
    }

    @Test
    void updatePassword_ShouldThrowResourceNotFoundException_WhenUserNotFound() {
        PasswordDTO dto = new PasswordDTO("oldPass", "newPass123");
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updatePassword(99, dto));
    }

    // ========= DELETE =========
    @Test
    void deleteUser_ShouldDelete_WhenExists() {
        when(userRepository.existsById(1)).thenReturn(true);

        userService.deleteUser(1);

        verify(userRepository).deleteById(1);
    }

    @Test
    void deleteUser_ShouldThrowResourceNotFoundException_WhenNotExists() {
        when(userRepository.existsById(1)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1));
    }

}
