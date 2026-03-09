package com.diegobrsantosdev.user_registration_application.controllers;

import com.diegobrsantosdev.user_registration_application.config.UserDetailsImpl;
import com.diegobrsantosdev.user_registration_application.dtos.*;
import com.diegobrsantosdev.user_registration_application.exceptions.ResourceNotFoundException;
import com.diegobrsantosdev.user_registration_application.models.Gender;
import com.diegobrsantosdev.user_registration_application.models.Role;
import com.diegobrsantosdev.user_registration_application.security.JwtUtil;
import com.diegobrsantosdev.user_registration_application.services.AuthService;
import com.diegobrsantosdev.user_registration_application.services.TwoFactorAuthService;
import com.diegobrsantosdev.user_registration_application.services.UserService;
import com.diegobrsantosdev.user_registration_application.shared.ApiMessages;
import com.diegobrsantosdev.user_registration_application.viaCep.CepService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    private static final Integer USER_ID = 1;
    private static final String USER_NAME = "João Silva";
    private static final String USER_EMAIL = "joao@email.com";
    private static final String USER_CPF = "98765432100";
    private static final String NOT_FOUND_MESSAGE = "User not found";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CepService cepService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private TwoFactorAuthService twoFactorAuthService;

    private UserDetailsImpl authenticatedUser() {
        return new UserDetailsImpl(
                USER_ID,
                USER_EMAIL,
                "ignored",
                Set.of(Role.USER)
        );
    }

    private void authenticate(UserDetailsImpl currentUser) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        currentUser,
                        null,
                        currentUser.getAuthorities()
                )
        );
    }

    @Test
    @DisplayName("Should get current authenticated user")
    void shouldGetCurrentUser() throws Exception {
        UserDetailsImpl currentUser = authenticatedUser();
        UserResponseDTO response = UserResponseDTOFactory.withCustom(
                USER_ID, USER_NAME, USER_EMAIL, USER_CPF
        );

        when(userService.getUserById(USER_ID)).thenReturn(response);
        authenticate(currentUser);

        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.name").value(USER_NAME))
                .andExpect(jsonPath("$.email").value(USER_EMAIL))
                .andExpect(jsonPath("$.cpf").value(USER_CPF));

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should return 404 when current user is not found")
    void shouldReturn404WhenCurrentUserNotFound() throws Exception {
        UserDetailsImpl currentUser = authenticatedUser();

        when(userService.getUserById(USER_ID))
                .thenThrow(new ResourceNotFoundException(NOT_FOUND_MESSAGE));
        authenticate(currentUser);

        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_MESSAGE));

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should update current user")
    void shouldUpdateCurrentUser() throws Exception {
        UserDetailsImpl currentUser = authenticatedUser();

        UserUpdateDTO request = new UserUpdateDTO(
                USER_NAME,
                USER_EMAIL,
                USER_CPF,
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
                true
        );

        UserResponseDTO response = UserResponseDTOFactory.withCustom(
                USER_ID, USER_NAME, USER_EMAIL, USER_CPF
        );

        when(userService.updateUser(eq(USER_ID), any(UserUpdateDTO.class)))
                .thenReturn(response);
        authenticate(currentUser);

        mockMvc.perform(put("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.name").value(USER_NAME))
                .andExpect(jsonPath("$.email").value(USER_EMAIL))
                .andExpect(jsonPath("$.cpf").value(USER_CPF));

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should return 404 when updating current user that does not exist")
    void shouldReturn404WhenUpdatingCurrentUserNotFound() throws Exception {
        UserDetailsImpl currentUser = authenticatedUser();

        UserUpdateDTO request = new UserUpdateDTO(
                USER_NAME,
                USER_EMAIL,
                USER_CPF,
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
                true
        );

        when(userService.updateUser(eq(USER_ID), any(UserUpdateDTO.class)))
                .thenThrow(new ResourceNotFoundException(NOT_FOUND_MESSAGE));
        authenticate(currentUser);

        mockMvc.perform(put("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_MESSAGE));

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should update current user password")
    void shouldUpdateCurrentUserPassword() throws Exception {
        UserDetailsImpl currentUser = authenticatedUser();
        PasswordDTO request = new PasswordDTO("oldPass", "newPass123");

        doNothing().when(userService).updatePassword(eq(USER_ID), any(PasswordDTO.class));
        authenticate(currentUser);

        mockMvc.perform(put("/api/v1/users/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ApiMessages.PASSWORD_UPDATED));

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should delete own user")
    void shouldDeleteOwnUser() throws Exception {
        UserDetailsImpl currentUser = authenticatedUser();
        MessageResponseDTO response = new MessageResponseDTO("Your account has been successfully deleted");

        when(userService.deleteOwnUser(USER_ID)).thenReturn(response);
        authenticate(currentUser);

        mockMvc.perform(delete("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Your account has been successfully deleted"));

        SecurityContextHolder.clearContext();
    }
}