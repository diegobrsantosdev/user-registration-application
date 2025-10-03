package com.diegobrsantosdev.user_registration_application.controllers;

import com.diegobrsantosdev.user_registration_application.dtos.*;
import com.diegobrsantosdev.user_registration_application.exceptions.DuplicateCpfException;
import com.diegobrsantosdev.user_registration_application.exceptions.DuplicateEmailException;
import com.diegobrsantosdev.user_registration_application.exceptions.ResourceNotFoundException;
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

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CepService cepService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Should get user by ID")
    void shouldGetUserById() throws Exception {
        var user = UserResponseDTOFactory.withCustom(1, "João Silva", "joao@email.com", "12345678901");
        Mockito.when(userService.getUserById(1)).thenReturn(user);

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.id()))
                .andExpect(jsonPath("$.name").value(user.name()));
    }

    @Test
    @DisplayName("Should get user by CPF")
    void shouldGetUserByCpf() throws Exception {
        var user = UserResponseDTOFactory.withCustom(1, "Name", "email@email.com", "12345678901");
        Mockito.when(userService.getUserByCpf("12345678901")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/v1/users?cpf=12345678901"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value(user.cpf()));
    }

    @Test
    @DisplayName("Should return 404 when user not found by ID")
    void shouldReturn404WhenUserNotFoundById() throws Exception {
        Mockito.when(userService.getUserById(999))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/api/v1/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }


    @Test
    @DisplayName("Should return 404 if user is not found by CPF")
    void shouldReturn404WhenUserNotFoundByCpf() throws Exception {
        Mockito.when(userService.getUserByCpf("00000000000")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/users?cpf=00000000000"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return BAD_REQUEST if no params are provided")
    void shouldReturnBadRequestWhenNoParams() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should list all users paginated")
    void shouldListAllUsersPaginated() throws Exception {
        var user = UserResponseDTOFactory.sample();
        Mockito.when(userService.listAllUsers(any()))
                .thenReturn(new PageImpl<>(List.of(user), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/v1/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(user.id()));
    }

    @Test
    @DisplayName("Should register a new user")
    void shouldRegisterUser() throws Exception {
        var req = new UserRegisterDTO(
                "Name",
                "email@email.com",
                "12345678901",
                "12345678909",
                "81999999999",
                "Rua Teste",
                "123",
                "Apto 101",
                "Centro",
                "Recife",
                "PE",
                "50000000"
        );

        var resp = UserResponseDTOFactory.sample();

        Mockito.when(userService.registerUser(any(UserRegisterDTO.class))).thenReturn(resp);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(resp.id()))
                .andExpect(jsonPath("$.name").value(resp.name()))
                .andExpect(jsonPath("$.email").value(resp.email()))
                .andExpect(jsonPath("$.cpf").value(resp.cpf()));
    }

    @Test
    @DisplayName("Should update a user")
    void shouldUpdateUser() throws Exception {
        var updateDTO = new UserUpdateDTO(
                "Updated Name",
                "updated@email.com",
                "98765432100",
                "81999999999",
                "Rua Atualizada",
                "456",
                "Apto 202",
                "Boa Vista",
                "Recife",
                "PE",
                "50001000"
        );

        var resp = UserResponseDTOFactory.withCustom(
                1,
                "Updated Name",
                "updated@email.com",
                "98765432100"
        );

        Mockito.when(userService.updateUser(eq(1), any(UserUpdateDTO.class))).thenReturn(resp);

        mockMvc.perform(put("/api/v1/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(resp.id()))
                .andExpect(jsonPath("$.name").value(resp.name()))
                .andExpect(jsonPath("$.email").value(resp.email()))
                .andExpect(jsonPath("$.cpf").value(resp.cpf()));
    }

    @Test
    @DisplayName("Should return 404 when trying to update a non-existent user")
    void shouldReturn404WhenUpdatingNonExistentUser() throws Exception {
        var updateDTO = new UserUpdateDTO(
                "Updated Name",
                "updated@email.com",
                "98765432100",
                "81999999999",
                "Rua Atualizada",
                "456",
                "Apto 202",
                "Boa Vista",
                "Recife",
                "PE",
                "50001000"
        );

        Mockito.when(userService.updateUser(eq(99), any(UserUpdateDTO.class)))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(put("/api/v1/users/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }



    @Test
    @DisplayName("Should return 409 when CPF is already registered")
    void shouldReturn409WhenCpfDuplicated() throws Exception {
        var req = new UserRegisterDTO(
                "Name",
                "other@email.com",
                "12345678901",
                "12345678909",
                "81999999999",
                "Rua Teste",
                "123",
                "Apto 101",
                "Centro",
                "Recife",
                "PE",
                "50000000"
        );

        Mockito.when(userService.registerUser(any(UserRegisterDTO.class)))
                .thenThrow(new DuplicateCpfException("CPF already registered"));

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(content().string("CPF already registered"));
    }



    @Test
    @DisplayName("Should return 409 when email is already registered")
    void shouldReturn409WhenEmailDuplicated() throws Exception {
        var req = new UserRegisterDTO(
                "Name",
                "email@email.com",
                "12345678901",
                "12345678909",
                "81999999999",
                "Rua Teste",
                "123",
                "Apto 101",
                "Centro",
                "Recife",
                "PE",
                "50000000"
        );

        Mockito.when(userService.registerUser(any(UserRegisterDTO.class)))
                .thenThrow(new DuplicateEmailException("Email already registered"));

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Email already registered"));
    }




    @Test
    @DisplayName("Should update user password")
    void shouldUpdateUserPassword() throws Exception {
        doNothing().when(userService).updatePassword(eq(2), any(PasswordDTO.class));

        var req = new PasswordDTO("oldPass", "newPass123");
        mockMvc.perform(put("/api/v1/users/2/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should delete user")
    void shouldDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(3);

        mockMvc.perform(delete("/api/v1/users/3"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return 404 when trying to delete a non-existent user")
    void shouldReturn404WhenDeletingNonExistentUser() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("User not found"))
                .when(userService).deleteUser(99);

        mockMvc.perform(delete("/api/v1/users/99"))
                .andExpect(status().isNotFound());
    }

}

