package com.diegobrsantosdev.user_registration_application.controllers;

import com.diegobrsantosdev.user_registration_application.config.UserDetailsImpl;
import com.diegobrsantosdev.user_registration_application.dtos.MessageResponseDTO;
import com.diegobrsantosdev.user_registration_application.dtos.UserResponseDTO;
import com.diegobrsantosdev.user_registration_application.dtos.UserResponseDTOFactory;
import com.diegobrsantosdev.user_registration_application.exceptions.ResourceNotFoundException;
import com.diegobrsantosdev.user_registration_application.models.Role;
import com.diegobrsantosdev.user_registration_application.models.User;
import com.diegobrsantosdev.user_registration_application.services.UserService;
import com.diegobrsantosdev.user_registration_application.shared.ApiMessages;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    private static final Integer ADMIN_ID = 100;
    private static final Integer USER_ID = 1;
    private static final String USER_NAME = "João Silva";
    private static final String USER_EMAIL = "joao@email.com";
    private static final String USER_CPF = "98765432100";
    private static final String NOT_FOUND_MESSAGE = "User not found";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private void authenticateAsAdmin() {
        UserDetailsImpl mockedAdmin = new UserDetailsImpl(
                ADMIN_ID,
                "admin",
                "senha",
                List.of(Role.ADMIN)
        );

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        mockedAdmin,
                        null,
                        mockedAdmin.getAuthorities()
                )
        );
    }

    @Test
    @DisplayName("Should get user by ID as admin")
    void shouldGetUserById() throws Exception {
        UserResponseDTO user = UserResponseDTOFactory.withCustom(
                USER_ID, USER_NAME, USER_EMAIL, USER_CPF
        );

        when(userService.getUserById(USER_ID)).thenReturn(user);

        mockMvc.perform(get("/api/v1/admin/users/{id}", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.name").value(USER_NAME))
                .andExpect(jsonPath("$.email").value(USER_EMAIL))
                .andExpect(jsonPath("$.cpf").value(USER_CPF));
    }

    @Test
    @DisplayName("Should return 404 when user not found by ID as admin")
    void shouldReturn404WhenUserNotFoundById() throws Exception {
        when(userService.getUserById(999))
                .thenThrow(new ResourceNotFoundException(NOT_FOUND_MESSAGE));

        mockMvc.perform(get("/api/v1/admin/users/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_MESSAGE));
    }

    @Test
    @DisplayName("Should get user by CPF as admin")
    void shouldGetUserByCpf() throws Exception {
        UserResponseDTO user = UserResponseDTOFactory.withCustom(
                USER_ID, USER_NAME, USER_EMAIL, USER_CPF
        );

        when(userService.getUserByCpf(USER_CPF)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/v1/admin/users/cpf/{cpf}", USER_CPF))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.name").value(USER_NAME))
                .andExpect(jsonPath("$.email").value(USER_EMAIL))
                .andExpect(jsonPath("$.cpf").value(USER_CPF));
    }

    @Test
    @DisplayName("Should return 404 when user not found by CPF as admin")
    void shouldReturn404WhenUserNotFoundByCpf() throws Exception {
        when(userService.getUserByCpf("00000000000")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/admin/users/cpf/{cpf}", "00000000000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_MESSAGE));
    }

    @Test
    @DisplayName("Should get user by email as admin")
    void shouldGetUserByEmail() throws Exception {
        UserResponseDTO user = UserResponseDTOFactory.withCustom(
                USER_ID, USER_NAME, USER_EMAIL, USER_CPF
        );

        when(userService.getUserByEmail(USER_EMAIL)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/v1/admin/users/email/{email}", USER_EMAIL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.name").value(USER_NAME))
                .andExpect(jsonPath("$.email").value(USER_EMAIL))
                .andExpect(jsonPath("$.cpf").value(USER_CPF));
    }

    @Test
    @DisplayName("Should return 404 when user not found by email as admin")
    void shouldReturn404WhenUserNotFoundByEmail() throws Exception {
        when(userService.getUserByEmail("missing@email.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/admin/users/email/{email}", "missing@email.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_MESSAGE));
    }

    @Test
    @DisplayName("Should list all users as admin")
    void shouldListAllUsers() throws Exception {
        UserResponseDTO user = UserResponseDTOFactory.withCustom(
                USER_ID, USER_NAME, USER_EMAIL, USER_CPF
        );

        when(userService.listAllUsers(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(user), Pageable.unpaged(), 1));

        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(USER_ID))
                .andExpect(jsonPath("$[0].name").value(USER_NAME))
                .andExpect(jsonPath("$[0].email").value(USER_EMAIL))
                .andExpect(jsonPath("$[0].cpf").value(USER_CPF));
    }

    @Test
    @DisplayName("Should promote user to admin")
    void shouldPromoteUserToAdmin() throws Exception {
        User promotedUser = User.builder()
                .id(USER_ID)
                .name(USER_NAME)
                .email(USER_EMAIL)
                .cpf(USER_CPF)
                .roles(Set.of(Role.ADMIN))
                .build();

        when(userService.promoteToAdmin(USER_ID)).thenReturn(promotedUser);

        mockMvc.perform(put("/api/v1/admin/users/{id}/promote", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ApiMessages.USER_PROMOTED))
                .andExpect(jsonPath("$.user.id").value(USER_ID))
                .andExpect(jsonPath("$.user.email").value(USER_EMAIL));
    }

    @Test
    @DisplayName("Should return 200 when admin deletes existing user")
    void adminDeletesUserSuccessfully() throws Exception {
        authenticateAsAdmin();

        int targetUserId = 55;

        when(userService.deleteUserAsAdmin(targetUserId, ADMIN_ID))
                .thenReturn(new MessageResponseDTO("User deleted successfully"));

        mockMvc.perform(delete("/api/v1/admin/users/{id}", targetUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should return 404 when admin tries to delete non-existing user")
    void adminDeletesNonExistingUser() throws Exception {
        authenticateAsAdmin();

        int notFoundUserId = 999;

        when(userService.deleteUserAsAdmin(notFoundUserId, ADMIN_ID))
                .thenThrow(new ResourceNotFoundException(NOT_FOUND_MESSAGE));

        mockMvc.perform(delete("/api/v1/admin/users/{id}", notFoundUserId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_MESSAGE));

        SecurityContextHolder.clearContext();
    }
}
