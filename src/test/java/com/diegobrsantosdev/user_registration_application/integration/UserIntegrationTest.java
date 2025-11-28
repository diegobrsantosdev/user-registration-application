package com.diegobrsantosdev.user_registration_application.integration;

import com.diegobrsantosdev.user_registration_application.dtos.UserRegisterDTO;
import com.diegobrsantosdev.user_registration_application.dtos.UserResponseDTOFactory;
import com.diegobrsantosdev.user_registration_application.models.Gender;
import com.diegobrsantosdev.user_registration_application.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
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

    @Autowired
    private UserService userService;

    // standard constants
    private static final String TEST_NAME = "João Silva";
    private static final String TEST_EMAIL = "joao@email.com";
    private static final String TEST_CPF = "12345678901";

    @Test
    @DisplayName("Should register and fetch a user successfully")
    void shouldRegisterAndFetchUser() throws Exception {

        UserRegisterDTO user = new UserRegisterDTO(
                TEST_NAME,
                TEST_EMAIL,
                "senhaSegura123",
                TEST_CPF,
                "12345678",
                "81999998888",
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

        var resp = UserResponseDTOFactory.sample();

        // service mock
        Mockito.when(userService.registerUser(any(UserRegisterDTO.class))).thenReturn(resp);
        Mockito.when(userService.getUserByCpf(TEST_CPF)).thenReturn(Optional.of(resp));

        // register test
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(TEST_NAME))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.cpf").value(TEST_CPF));

        //test get by cpf
        mockMvc.perform(get("/api/v1/users")
                        .param("cpf", TEST_CPF))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value(TEST_CPF))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL));
    }
}