package com.diegobrsantosdev.user_registration_application.controllers;

import com.diegobrsantosdev.user_registration_application.dtos.AuthResponseDTO;
import com.diegobrsantosdev.user_registration_application.dtos.LoginRequestDTO;
import com.diegobrsantosdev.user_registration_application.dtos.UserResponseDTO;
import com.diegobrsantosdev.user_registration_application.exceptions.InvalidCredentialsException;
import com.diegobrsantosdev.user_registration_application.models.Gender;
import com.diegobrsantosdev.user_registration_application.models.User;
import com.diegobrsantosdev.user_registration_application.security.JwtUtil;
import com.diegobrsantosdev.user_registration_application.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    // test constants
    private static final String NAME = "João Silva";
    private static final String EMAIL = "joao@email.com";
    private static final String PASSWORD = "senha1234";
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final String CPF = "98765432100";
    private static final String RG = "12345678";
    private static final String PHONE = "11999998888";
    private static final String ADDRESS = "Rua Alpha";
    private static final String NUMBER = "100";
    private static final String COMPLEMENT = "Apto 12";
    private static final String NEIGHBORHOOD = "Centro";
    private static final String CITY = "São Paulo";
    private static final String STATE = "SP";
    private static final String ZIP = "01001000";
    private static final LocalDate DOB = LocalDate.of(1990, 5, 15);
    private static final String JWT_TOKEN = "FAKE_JWT_TOKEN";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtUtil jwtUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldLoginWithValidCredentials() throws Exception {

        User user = User.builder()
                .id(1)
                .name(NAME)
                .email(EMAIL)
                .password(ENCODED_PASSWORD)
                .cpf(CPF)
                .rg(RG)
                .phone(PHONE)
                .address(ADDRESS)
                .number(NUMBER)
                .complement(COMPLEMENT)
                .neighborhood(NEIGHBORHOOD)
                .city(CITY)
                .state(STATE)
                .zipCode(ZIP)
                .gender(Gender.MALE)
                .dateOfBirth(DOB)
                .termsAccepted(true)
                .profilePictureUrl(null)
                .build();

        AuthResponseDTO response = new AuthResponseDTO(
                JWT_TOKEN,
                false,
                UserResponseDTO.fromEntity(user)
        );

        Mockito.when(authService.login(Mockito.any(LoginRequestDTO.class)))
                .thenReturn(response);

        LoginRequestDTO requestDTO = new LoginRequestDTO(
                EMAIL,
                PASSWORD
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(JWT_TOKEN))
                .andExpect(jsonPath("$.user.email").value(EMAIL))
                .andExpect(jsonPath("$.user.name").value(NAME))
                .andExpect(jsonPath("$.user.gender").value("MALE"))
                .andExpect(jsonPath("$.user.city").value(CITY));
    }

    @Test
    void shouldRejectInvalidPassword() throws Exception {

        Mockito.when(authService.login(Mockito.any(LoginRequestDTO.class)))
                .thenThrow(new InvalidCredentialsException("Invalid email or password."));

        LoginRequestDTO requestDTO = new LoginRequestDTO(
                EMAIL,
                "wrongPass"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectNonexistentEmail() throws Exception {

        Mockito.when(authService.login(Mockito.any(LoginRequestDTO.class)))
                .thenThrow(new InvalidCredentialsException("Invalid email or password."));

        LoginRequestDTO requestDTO = new LoginRequestDTO(
                "notfound@email.com",
                "whatever"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isUnauthorized());
    }
}
