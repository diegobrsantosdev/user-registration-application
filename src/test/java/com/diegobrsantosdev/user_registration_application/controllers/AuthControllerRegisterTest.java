package com.diegobrsantosdev.user_registration_application.controllers;

import com.diegobrsantosdev.user_registration_application.dtos.AuthResponseDTO;
import com.diegobrsantosdev.user_registration_application.dtos.UserRegisterDTO;
import com.diegobrsantosdev.user_registration_application.dtos.UserResponseDTO;
import com.diegobrsantosdev.user_registration_application.exceptions.InvalidCredentialsException;
import com.diegobrsantosdev.user_registration_application.models.Gender;
import com.diegobrsantosdev.user_registration_application.models.Role;
import com.diegobrsantosdev.user_registration_application.models.User;
import com.diegobrsantosdev.user_registration_application.security.JwtUtil;
import com.diegobrsantosdev.user_registration_application.services.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;


@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerRegisterTest {

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
    private static final String GENDER = "MALE";
    private static final LocalDate DOB = LocalDate.of(1990, 5, 15);

    private static final String JWT_TOKEN = "FAKE_JWT_TOKEN";

    private static final String REGISTER_BODY = """
        {
            "name":"João Silva",
            "email":"joao@email.com",
            "password":"senha1234",
            "cpf":"98765432100",
            "rg":"12345678",
            "phone":"11999998888",
            "address":"Rua Alpha",
            "number":"100",
            "complement":"Apto 12",
            "neighborhood":"Centro",
            "city":"São Paulo",
            "state":"SP",
            "zipCode":"01001000",
            "gender":"MALE",
            "dateOfBirth":"1990-05-15",
            "termsAccepted":true,
            "profilePictureUrl":null
        }
    """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void shouldRegisterNewUserSuccessfully() throws Exception {

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

                .roles(Set.of(Role.USER))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .twoFactorEnabled(false)
                .build();

        AuthResponseDTO response = new AuthResponseDTO(
                JWT_TOKEN,
                false,
                false,
                UserResponseDTO.fromEntity(user)
        );

        Mockito.when(authService.register(Mockito.any(UserRegisterDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTER_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value(JWT_TOKEN))
                .andExpect(jsonPath("$.user.email").value(EMAIL))
                .andExpect(jsonPath("$.user.name").value(NAME));
    }

    @Test
    void shouldRejectRegistrationWithExistingEmail() throws Exception {

        Mockito.when(authService.register(Mockito.any(UserRegisterDTO.class)))
                .thenThrow(new InvalidCredentialsException("Email already in use."));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTER_BODY))
                .andExpect(status().isUnauthorized());
    }
}
