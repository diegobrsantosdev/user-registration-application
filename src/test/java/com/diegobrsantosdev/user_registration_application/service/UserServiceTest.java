package com.diegobrsantosdev.user_registration_application.service;

import com.diegobrsantosdev.user_registration_application.dtos.*;
import com.diegobrsantosdev.user_registration_application.models.Gender;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    // standard constants
    private static final int EXISTING_ID = 1;
    private static final int NON_EXISTENT_ID = 99;
    private static final String DEFAULT_NAME = "Lucas Pereira";
    private static final String DEFAULT_EMAIL = "lucas@gmail.com";
    private static final String DEFAULT_CPF = "98765432100";
    private static final String DEFAULT_PASSWORD = "senha12345";

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    // ======= HELPERS =======
    private UserRegisterDTO createDefaultUserRegisterDTO() {
        return new UserRegisterDTO(
                DEFAULT_NAME,
                DEFAULT_EMAIL,
                DEFAULT_PASSWORD,
                DEFAULT_CPF,
                "4455667",
                "81999997777",
                "Rua Sol",
                "321",
                "Apto 101",
                "Boa Vista",
                "Recife",
                "PE",
                "50020000",
                Gender.MALE,
                LocalDate.of(1990, 8, 21),
                "https://example.com/lucas.jpg",
                true
        );
    }

    private UserUpdateDTO createDefaultUserUpdateDTO() {
        return new UserUpdateDTO(
                "Carlos Henrique",
                "carlos@gmail.com",
                "32165498700",
                "1020304",
                "81977776666",
                "Avenida Brasil",
                "250",
                "Bloco B, Apto 402",
                "Boa Vista",
                "Olinda",
                "PE",
                "53020000",
                Gender.MALE,
                LocalDate.of(1988, 5, 14),
                "https://example.com/profile.png",
                true
        );
    }

    // ========= CREATE =========
    @Test
    void registerUser_ShouldThrowDuplicateCpfException_WhenCpfExists() {
        UserRegisterDTO dto = createDefaultUserRegisterDTO();
        when(userRepository.findByCpf(dto.cpf())).thenReturn(Optional.of(new User()));

        DuplicateCpfException ex = assertThrows(DuplicateCpfException.class, () -> userService.registerUser(dto));
        assertEquals("CPF already registered", ex.getMessage());
    }

    @Test
    void registerUser_ShouldThrowDuplicateEmailException_WhenEmailExists() {
        UserRegisterDTO dto = createDefaultUserRegisterDTO();
        when(userRepository.findByCpf(dto.cpf())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.of(new User()));

        DuplicateEmailException ex = assertThrows(DuplicateEmailException.class, () -> userService.registerUser(dto));
        assertEquals("Email already registered", ex.getMessage());
    }

    @Test
    void registerUser_ShouldSaveAndReturnUser_WhenDataIsValid() {
        UserRegisterDTO dto = createDefaultUserRegisterDTO();
        User userEntity = new User();
        User savedUser = new User();
        UserResponseDTO responseDTO = UserResponseDTOFactory.withCustom(EXISTING_ID, DEFAULT_NAME, DEFAULT_EMAIL, DEFAULT_CPF);

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
        user.setId(EXISTING_ID);
        when(userRepository.findById(EXISTING_ID)).thenReturn(Optional.of(user));
        UserResponseDTO dto = UserResponseDTOFactory.withCustom(EXISTING_ID, DEFAULT_NAME, DEFAULT_EMAIL, DEFAULT_CPF);

        try (MockedStatic<UserResponseDTO> mocked = mockStatic(UserResponseDTO.class)) {
            mocked.when(() -> UserResponseDTO.fromEntity(user)).thenReturn(dto);

            UserResponseDTO result = userService.getUserById(EXISTING_ID);
            assertEquals(dto, result);
        }
    }

    @Test
    void getUserById_ShouldThrowResourceNotFoundException_WhenNotFound() {
        when(userRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(NON_EXISTENT_ID));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void getUserByCpf_ShouldReturnOptionalUser_WhenExists() {
        User user = new User();
        when(userRepository.findByCpf(DEFAULT_CPF)).thenReturn(Optional.of(user));
        UserResponseDTO dto = UserResponseDTOFactory.withCustom(EXISTING_ID, DEFAULT_NAME, DEFAULT_EMAIL, DEFAULT_CPF);

        try (MockedStatic<UserResponseDTO> mocked = mockStatic(UserResponseDTO.class)) {
            mocked.when(() -> UserResponseDTO.fromEntity(user)).thenReturn(dto);

            Optional<UserResponseDTO> result = userService.getUserByCpf(DEFAULT_CPF);
            assertTrue(result.isPresent());
            assertEquals(dto, result.get());
        }
    }

    @Test
    void getUserByCpf_ShouldReturnEmpty_WhenNotExists() {
        when(userRepository.findByCpf(DEFAULT_CPF)).thenReturn(Optional.empty());
        Optional<UserResponseDTO> result = userService.getUserByCpf(DEFAULT_CPF);
        assertTrue(result.isEmpty());
    }

    // ========= UPDATE =========
    @Test
    void updateUser_ShouldReturnUpdatedUser_WhenDataIsValid() {
        UserUpdateDTO dto = createDefaultUserUpdateDTO();
        User existingUser = new User(); existingUser.setId(EXISTING_ID);
        User updatedUser = new User(); updatedUser.setId(EXISTING_ID);

        when(userRepository.findById(EXISTING_ID)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByCpf(dto.cpf())).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.of(existingUser));
        doNothing().when(userMapper).updateEntityFromDto(existingUser, dto);
        when(userRepository.saveAndFlush(existingUser)).thenReturn(updatedUser);

        try (MockedStatic<UserResponseDTO> mocked = mockStatic(UserResponseDTO.class)) {
            UserResponseDTO dtoResponse = UserResponseDTOFactory.withCustom(EXISTING_ID, dto.name(), dto.email(), dto.cpf());
            mocked.when(() -> UserResponseDTO.fromEntity(updatedUser)).thenReturn(dtoResponse);

            UserResponseDTO result = userService.updateUser(EXISTING_ID, dto);
            assertEquals(dtoResponse, result);
        }

        verify(userRepository).saveAndFlush(existingUser);
    }

    @Test
    void updateUser_ShouldThrowResourceNotFoundException_WhenUserNotFound() {
        UserUpdateDTO dto = createDefaultUserUpdateDTO();
        when(userRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(NON_EXISTENT_ID, dto));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void updateUser_ShouldThrowResourceAlreadyExistsException_WhenCpfOrEmailDuplicated() {
        UserUpdateDTO dto = createDefaultUserUpdateDTO();
        User existingUser = new User(); existingUser.setId(EXISTING_ID);
        User anotherUser = new User(); anotherUser.setId(2);

        when(userRepository.findById(EXISTING_ID)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByCpf(dto.cpf())).thenReturn(Optional.of(anotherUser));

        assertThrows(ResourceAlreadyExistsException.class, () -> userService.updateUser(EXISTING_ID, dto));

        when(userRepository.findByCpf(dto.cpf())).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.of(anotherUser));

        assertThrows(ResourceAlreadyExistsException.class, () -> userService.updateUser(EXISTING_ID, dto));
    }

    // ========= UPDATE PASSWORD =========
    @Test
    void updatePassword_ShouldUpdate_WhenCurrentPasswordIsCorrect() {
        PasswordDTO dto = new PasswordDTO("oldPass", "newPass123");
        User user = new User(); user.setId(EXISTING_ID); user.setPassword("encodedOld");

        when(userRepository.findById(EXISTING_ID)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.currentPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(dto.newPassword())).thenReturn("encodedNew");

        userService.updatePassword(EXISTING_ID, dto);

        verify(userRepository).saveAndFlush(user);
        assertEquals("encodedNew", user.getPassword());
    }

    @Test
    void updatePassword_ShouldThrowIncorrectPasswordException_WhenCurrentPasswordIsWrong() {
        PasswordDTO dto = new PasswordDTO("wrongOld", "newPass123");
        User user = new User(); user.setPassword("encodedOld");

        when(userRepository.findById(EXISTING_ID)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.currentPassword(), user.getPassword())).thenReturn(false);

        assertThrows(IncorrectPasswordException.class, () -> userService.updatePassword(EXISTING_ID, dto));
    }

    @Test
    void updatePassword_ShouldThrowResourceNotFoundException_WhenUserNotFound() {
        PasswordDTO dto = new PasswordDTO("oldPass", "newPass123");
        when(userRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updatePassword(NON_EXISTENT_ID, dto));
    }

    // ========= DELETE =========
    @Test
    void deleteUser_ShouldDelete_WhenExists() {
        when(userRepository.existsById(EXISTING_ID)).thenReturn(true);

        userService.deleteUser(EXISTING_ID);

        verify(userRepository).deleteById(EXISTING_ID);
    }

    @Test
    void deleteUser_ShouldThrowResourceNotFoundException_WhenNotExists() {
        when(userRepository.existsById(EXISTING_ID)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(EXISTING_ID));
    }
}
