package com.diegobrsantosdev.user_registration_application.controllers;
import com.diegobrsantosdev.user_registration_application.config.SecurityConfig;
import com.diegobrsantosdev.user_registration_application.config.UserDetailsImpl;
import com.diegobrsantosdev.user_registration_application.models.Role;
import com.diegobrsantosdev.user_registration_application.security.JwtUtil;
import com.diegobrsantosdev.user_registration_application.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminController.class)
@Import(SecurityConfig.class)
class AdminControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("Should return 403 when user is not admin")
    void userWithoutAdminCannotDelete() throws Exception {
        UserDetailsImpl normalUser = new UserDetailsImpl(
                1,
                "user",
                "1234",
                Set.of(Role.USER)
        );

        mockMvc.perform(delete("/admin/users/{id}", 55)
                        .with(user(normalUser)))
                .andExpect(status().isForbidden());
    }
}
