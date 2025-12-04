package com.diegobrsantosdev.user_registration_application.controllers;

import com.diegobrsantosdev.user_registration_application.config.SecurityConfig;
import com.diegobrsantosdev.user_registration_application.dtos.UserResponseDTO;
import com.diegobrsantosdev.user_registration_application.models.Gender;
import com.diegobrsantosdev.user_registration_application.models.Role;
import com.diegobrsantosdev.user_registration_application.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@Import(SecurityConfig.class)
class AdminControllerRolesTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    // Users for testing (from your TestConfig)
    private static final UserResponseDTO USER_1 = new UserResponseDTO(
            1,
            "João Silva",
            "joao@email.com",
            "98765432100",
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
            true,
            null,
            null,
            Set.of(Role.USER)
    );


    private static final UserResponseDTO ADMIN_1 = new UserResponseDTO(
            3,
            "Caio Pereira",
            "caiopereiraaa19@gmail.com",
            "48520695490",
            "11223344",
            "81987501006",
            "Rua Padre Alencar",
            "256",
            "casa",
            "Santo Amaro",
            "Recife",
            "PE",
            "50070000",
            Gender.MALE,
            LocalDate.of(1988, 11, 30),
            null,
            true,
            null,
            null,
            Set.of(Role.ADMIN)
    );


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminShouldAccessListUsers() throws Exception {
        when(userService.listAllUsers(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(USER_1, ADMIN_1)));

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(USER_1.id()))
                .andExpect(jsonPath("$[1].id").value(ADMIN_1.id()));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void userShouldNotAccessListUsers() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isForbidden());
    }


    @Test
    void unauthenticatedUserShouldNotAccessListUsers() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "manager", roles = {"USER", "ADMIN"})
    void multiRoleUserShouldAccessListUsers() throws Exception {
        when(userService.listAllUsers(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(USER_1, ADMIN_1)));

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(USER_1.id()))
                .andExpect(jsonPath("$[1].id").value(ADMIN_1.id()));
    }


}