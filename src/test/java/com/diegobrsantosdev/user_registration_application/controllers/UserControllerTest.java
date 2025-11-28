package com.diegobrsantosdev.user_registration_application.controllers;

import com.diegobrsantosdev.user_registration_application.dtos.*;
import com.diegobrsantosdev.user_registration_application.exceptions.DuplicateCpfException;
import com.diegobrsantosdev.user_registration_application.exceptions.DuplicateEmailException;
import com.diegobrsantosdev.user_registration_application.exceptions.ResourceNotFoundException;
import com.diegobrsantosdev.user_registration_application.models.Gender;
import com.diegobrsantosdev.user_registration_application.services.AuthService;
import com.diegobrsantosdev.user_registration_application.services.UserService;
import com.diegobrsantosdev.user_registration_application.viaCep.CepController;
import com.diegobrsantosdev.user_registration_application.viaCep.CepService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {


    //Constants

    private static final Integer USER_ID = 1;
    private static final String USER_NAME = "João Silva";
    private static final String USER_EMAIL = "joao@email.com";
    private static final String USER_CPF = "98765432100";
    private static final String INVALID_CPF = "00000000000";
    private static final String DUPLICATE_CPF_MESSAGE = "CPF already registered";
    private static final String DUPLICATE_EMAIL_MESSAGE = "Email already registered";
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


    //GET BY ID

    @Test
    @DisplayName("Should get user by ID")
    void shouldGetUserById() throws Exception {
        var user = UserResponseDTOFactory.withCustom(
                USER_ID, USER_NAME, USER_EMAIL, USER_CPF
        );

        Mockito.when(userService.getUserById(USER_ID)).thenReturn(user);

        mockMvc.perform(get("/api/v1/users/" + USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.name").value(USER_NAME))
                .andExpect(jsonPath("$.email").value(USER_EMAIL))
                .andExpect(jsonPath("$.cpf").value(USER_CPF));
    }


    //GET BY CPF

    @Test
    @DisplayName("Should get user by CPF")
    void shouldGetUserByCpf() throws Exception {
        var user = UserResponseDTOFactory.withCustom(
                USER_ID, USER_NAME, USER_EMAIL, USER_CPF
        );

        Mockito.when(userService.getUserByCpf(USER_CPF))
                .thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/v1/users?cpf=" + USER_CPF))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.name").value(USER_NAME))
                .andExpect(jsonPath("$.email").value(USER_EMAIL))
                .andExpect(jsonPath("$.cpf").value(USER_CPF));
    }

    @Test
    @DisplayName("Should return 404 when user not found by ID")
    void shouldReturn404WhenUserNotFoundById() throws Exception {
        Mockito.when(userService.getUserById(999))
                .thenThrow(new ResourceNotFoundException(NOT_FOUND_MESSAGE));

        mockMvc.perform(get("/api/v1/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_MESSAGE));
    }

    @Test
    @DisplayName("Should return 404 if user is not found by CPF")
    void shouldReturn404WhenUserNotFoundByCpf() throws Exception {
        Mockito.when(userService.getUserByCpf(INVALID_CPF)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/users?cpf=" + INVALID_CPF))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return BAD_REQUEST if no params are provided")
    void shouldReturnBadRequestWhenNoParams() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isBadRequest());
    }


    //LIST ALL PAGINATED

    @Test
    @DisplayName("Should list all users paginated")
    void shouldListAllUsersPaginated() throws Exception {
        var user = UserResponseDTOFactory.withCustom(
                USER_ID, USER_NAME, USER_EMAIL, USER_CPF
        );

        Mockito.when(userService.listAllUsers(any()))
                .thenReturn(new PageImpl<>(List.of(user), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/v1/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(USER_ID))
                .andExpect(jsonPath("$.content[0].name").value(USER_NAME))
                .andExpect(jsonPath("$.content[0].email").value(USER_EMAIL))
                .andExpect(jsonPath("$.content[0].cpf").value(USER_CPF));
    }


    //REGISTER USER

    @Test
    @DisplayName("Should register a new user")
    void shouldRegisterUser() throws Exception {

        var req = new UserRegisterDTO(
                USER_NAME,
                USER_EMAIL,
                "senha1234",
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

        var resp = UserResponseDTOFactory.withCustom(
                USER_ID, USER_NAME, USER_EMAIL, USER_CPF
        );

        Mockito.when(userService.registerUser(any(UserRegisterDTO.class))).thenReturn(resp);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.name").value(USER_NAME))
                .andExpect(jsonPath("$.email").value(USER_EMAIL))
                .andExpect(jsonPath("$.cpf").value(USER_CPF));
    }

    @Test
    @DisplayName("Should return 409 when CPF is already registered")
    void shouldReturn409WhenCpfDuplicated() throws Exception {
        var req = new UserRegisterDTO(
                USER_NAME, USER_EMAIL, "senha1234", USER_CPF,
                "12345678", "11999998888", "Rua Alpha", "100",
                "Apto 12", "Centro", "São Paulo", "SP",
                "01001000", Gender.MALE,
                LocalDate.of(1990, 5, 15), null, true
        );

        Mockito.when(userService.registerUser(any(UserRegisterDTO.class)))
                .thenThrow(new DuplicateCpfException(DUPLICATE_CPF_MESSAGE));

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(content().string(DUPLICATE_CPF_MESSAGE));
    }

    @Test
    @DisplayName("Should return 409 when email is already registered")
    void shouldReturn409WhenEmailDuplicated() throws Exception {
        var req = new UserRegisterDTO(
                USER_NAME, USER_EMAIL, "senha1234", USER_CPF,
                "12345678", "11999998888", "Rua Alpha", "100",
                "Apto 12", "Centro", "São Paulo", "SP",
                "01001000", Gender.MALE,
                LocalDate.of(1990, 5, 15), null, true
        );

        Mockito.when(userService.registerUser(any(UserRegisterDTO.class)))
                .thenThrow(new DuplicateEmailException(DUPLICATE_EMAIL_MESSAGE));

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(content().string(DUPLICATE_EMAIL_MESSAGE));
    }


    //UPDATE USER

    @Test
    @DisplayName("Should update a user")
    void shouldUpdateUser() throws Exception {

        var updateDTO = new UserUpdateDTO(
                USER_NAME, USER_EMAIL, USER_CPF,
                "12345678", "11999998888", "Rua Alpha", "100",
                "Apto 12", "Centro", "São Paulo", "SP",
                "01001000", Gender.MALE,
                LocalDate.of(1990, 5, 15), null, true
        );

        var resp = UserResponseDTOFactory.withCustom(
                USER_ID, USER_NAME, USER_EMAIL, USER_CPF
        );

        Mockito.when(userService.updateUser(eq(USER_ID), any(UserUpdateDTO.class)))
                .thenReturn(resp);

        mockMvc.perform(put("/api/v1/users/" + USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.name").value(USER_NAME))
                .andExpect(jsonPath("$.email").value(USER_EMAIL))
                .andExpect(jsonPath("$.cpf").value(USER_CPF));
    }

    @Test
    @DisplayName("Should return 404 when trying to update a non-existent user")
    void shouldReturn404WhenUpdatingNonExistentUser() throws Exception {

        var updateDTO = new UserUpdateDTO(
                USER_NAME, USER_EMAIL, USER_CPF,
                "12345678", "11999998888", "Rua Alpha", "100",
                "Apto 12", "Centro", "São Paulo", "SP",
                "01001000", Gender.MALE,
                LocalDate.of(1990, 5, 15), null, true
        );

        Mockito.when(userService.updateUser(eq(99), any(UserUpdateDTO.class)))
                .thenThrow(new ResourceNotFoundException(NOT_FOUND_MESSAGE));

        mockMvc.perform(put("/api/v1/users/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }


    //PASSWORD

    @Test
    @DisplayName("Should update user password")
    void shouldUpdateUserPassword() throws Exception {
        doNothing().when(userService).updatePassword(eq(USER_ID), any(PasswordDTO.class));

        var req = new PasswordDTO("oldPass", "newPass123");

        mockMvc.perform(put("/api/v1/users/" + USER_ID + "/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNoContent());
    }


    // DELETE USER

    @Test
    @DisplayName("Should delete user")
    void shouldDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(USER_ID);

        mockMvc.perform(delete("/api/v1/users/" + USER_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return 404 when trying to delete a non-existent user")
    void shouldReturn404WhenDeletingNonExistentUser() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException(NOT_FOUND_MESSAGE))
                .when(userService).deleteUser(99);

        mockMvc.perform(delete("/api/v1/users/99"))
                .andExpect(status().isNotFound());
    }
}


