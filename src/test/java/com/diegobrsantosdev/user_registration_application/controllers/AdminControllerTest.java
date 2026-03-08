package com.diegobrsantosdev.user_registration_application.controllers;

import com.diegobrsantosdev.user_registration_application.config.UserDetailsImpl;
import com.diegobrsantosdev.user_registration_application.security.TestSecurityConfig;
import com.diegobrsantosdev.user_registration_application.dtos.MessageResponseDTO;
import com.diegobrsantosdev.user_registration_application.exceptions.ResourceNotFoundException;
import com.diegobrsantosdev.user_registration_application.models.Role;

import com.diegobrsantosdev.user_registration_application.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;


import static org.mockito.BDDMockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;


    private void autenticaComoAdmin() {
        UserDetailsImpl mockedAdmin = new UserDetailsImpl(
                100,
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
    @DisplayName("Should return 200 when admin deletes existing user")
    void adminDeletesUserSuccessfully() throws Exception {
        autenticaComoAdmin();

        int targetUserId = 55;

        when(userService.deleteUserAsAdmin(targetUserId, 100))
                .thenReturn(new MessageResponseDTO("User deleted successfully"));

        mockMvc.perform(delete("/api/v1/admin/users/{1}", targetUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }

    @Test
    @DisplayName("Should return 404 when admin tries to delete non-existing user")
    void adminDeletesNonExistingUser() throws Exception {
        autenticaComoAdmin();

        int notFoundUserId = 999;

        when(userService.deleteUserAsAdmin(notFoundUserId, 100))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(delete("/api/v1/admin/users/{1}", notFoundUserId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

}
