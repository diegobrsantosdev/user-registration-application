package com.diegobrsantosdev.user_registration_application.integration;

import com.diegobrsantosdev.user_registration_application.dtos.UserRegisterDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should register and fetch a user successfully")
    void shouldRegisterAndFetchUser() throws Exception {
        // Create a new user
        UserRegisterDTO user = new UserRegisterDTO(
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

        // 2⃣Send a post do register a new user
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Name"))
                .andExpect(jsonPath("$.email").value("email@email.com"));

        //  Get user recently created by cpf
        mockMvc.perform(get("/api/v1/users")
                        .param("cpf", "12345678909"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value("12345678909"))
                .andExpect(jsonPath("$.email").value("email@email.com"));
    }
}