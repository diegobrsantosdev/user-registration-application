package com.diegobrsantosdev.user_registration_application.service;

import com.diegobrsantosdev.user_registration_application.dtos.AuthResponseDTO;
import com.diegobrsantosdev.user_registration_application.dtos.LoginRequestDTO;
import com.diegobrsantosdev.user_registration_application.dtos.UserRegisterDTO;
import com.diegobrsantosdev.user_registration_application.exceptions.InvalidCredentialsException;
import com.diegobrsantosdev.user_registration_application.exceptions.InvalidDataException;
import com.diegobrsantosdev.user_registration_application.models.Gender;
import com.diegobrsantosdev.user_registration_application.models.Role;
import com.diegobrsantosdev.user_registration_application.models.User;
import com.diegobrsantosdev.user_registration_application.security.JwtUtil;
import com.diegobrsantosdev.user_registration_application.services.AuthService;
import com.diegobrsantosdev.user_registration_application.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    // ========= CONSTANTS =========
    private static final String NAME = "João Silva";
    private static final String EMAIL = "joao@email.com";
    private static final String PASSWORD = "senha123";
    private static final String WRONG_PASSWORD = "senhaErrada";
    private static final String ENCODED_PASSWORD = "encoded";
    private static final String TOKEN = "jwt-token";

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1);
        user.setName(NAME);
        user.setEmail(EMAIL);
        user.setPassword(ENCODED_PASSWORD);
        user.setGender(Gender.MALE);
        user.setDateOfBirth(LocalDate.of(1990, 5, 20));
        user.setRoles(Set.of(Role.USER));
        user.setTermsAccepted(true);
        user.setTwoFactorEnabled(false);
    }

    // ====================LOGIN=================================

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() {

        LoginRequestDTO request = new LoginRequestDTO(EMAIL, PASSWORD);

        when(userService.findByEmail(EMAIL)).thenReturn(user);
        when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(jwtUtil.generateToken(
                eq(EMAIL),
                anyList()
        )).thenReturn(TOKEN);

        AuthResponseDTO response = authService.login(request);

        assertNotNull(response);
        assertEquals(TOKEN, response.token());
        assertFalse(response.twoFactorEnabled());
        assertFalse(response.requires2FA());
        assertNotNull(response.user());
        assertEquals(EMAIL, response.user().email());
    }

    @Test
    void login_ShouldThrowException_WhenPasswordIsInvalid() {

        LoginRequestDTO request = new LoginRequestDTO(EMAIL, WRONG_PASSWORD);

        when(userService.findByEmail(EMAIL)).thenReturn(user);
        when(passwordEncoder.matches(WRONG_PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        InvalidCredentialsException ex =
                assertThrows(InvalidCredentialsException.class,
                        () -> authService.login(request));

        assertEquals("Invalid email or password.", ex.getMessage());
    }

    @Test
    void login_ShouldReturn2FARequired_WhenUserHas2FAEnabled() {

        user.setTwoFactorEnabled(true);
        LoginRequestDTO request = new LoginRequestDTO(EMAIL, PASSWORD);

        when(userService.findByEmail(EMAIL)).thenReturn(user);
        when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

        AuthResponseDTO response = authService.login(request);

        assertNotNull(response);
        assertNull(response.token());
        assertTrue(response.twoFactorEnabled());
        assertTrue(response.requires2FA());
        assertNotNull(response.user());
    }

    // =======================REGISTER==============================

    @Test
    void register_ShouldCreateUser_WhenValidData() {

        UserRegisterDTO dto = new UserRegisterDTO(
                NAME,
                EMAIL,
                PASSWORD,
                "12345678900",
                "1234567",
                "81999999999",
                "Rua A",
                "10",
                null,
                "Centro",
                "Recife",
                "PE",
                "50000000",
                Gender.MALE,
                LocalDate.of(1990, 5, 20),
                null,
                true
        );

        when(userService.existsByEmail(EMAIL)).thenReturn(false);
        when(userService.existsByCpf(dto.cpf())).thenReturn(false);
        when(userService.existsByRg(dto.rg())).thenReturn(false);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userService.save(any(User.class))).thenReturn(user);
        when(jwtUtil.generateToken(
                eq(EMAIL),
                anyList()
        )).thenReturn(TOKEN);

        AuthResponseDTO response = authService.register(dto);

        assertNotNull(response);
        assertEquals(TOKEN, response.token());
        assertFalse(response.twoFactorEnabled());
        assertFalse(response.requires2FA());
        assertNotNull(response.user());
    }

    @Test
    void register_ShouldThrowException_WhenTermsNotAccepted() {

        UserRegisterDTO dto = new UserRegisterDTO(
                NAME,
                EMAIL,
                PASSWORD,
                "12345678900",
                "1234567",
                "81999999999",
                "Rua A",
                "10",
                null,
                "Centro",
                "Recife",
                "PE",
                "50000000",
                Gender.MALE,
                LocalDate.of(1990, 5, 20),
                null,
                false
        );

        InvalidDataException ex =
                assertThrows(InvalidDataException.class,
                        () -> authService.register(dto));

        assertEquals("Terms must be accepted.", ex.getMessage());
    }
}