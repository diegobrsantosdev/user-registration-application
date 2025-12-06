package com.diegobrsantosdev.user_registration_application.integration;

import com.diegobrsantosdev.user_registration_application.dtos.UserRegisterDTO;
import com.diegobrsantosdev.user_registration_application.dtos.UserResponseDTOFactory;
import com.diegobrsantosdev.user_registration_application.models.Gender;
import com.diegobrsantosdev.user_registration_application.models.Role;
import com.diegobrsantosdev.user_registration_application.models.User;
import com.diegobrsantosdev.user_registration_application.repositories.UserRepository;
import com.diegobrsantosdev.user_registration_application.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

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
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // standard constants
    private static final String TEST_NAME = "Usuario Teste";
    private static final String TEST_EMAIL = "user.test@example.com";
    private static final String TEST_CPF = "39053344705";

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        userRepository.save(
                User.builder()
                        .name("Admin Teste")
                        .email("admin@example.com")
                        .password(passwordEncoder.encode("admin123"))
                        .cpf("29537955044")
                        .rg("00000000")
                        .phone("81990000000")
                        .address("Rua Teste")
                        .number("100")
                        .neighborhood("Centro")
                        .city("Recife")
                        .state("PE")
                        .zipCode("50000000")
                        .gender(Gender.MALE)
                        .dateOfBirth(LocalDate.of(1990, 1, 1))
                        .termsAccepted(true)
                        .roles(Set.of(Role.ADMIN))
                        .twoFactorEnabled(false)
                        .build()
        );
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    @DisplayName("Should register and fetch a user successfully")
    void shouldRegisterAndFetchUser() throws Exception {

        UserRegisterDTO user = new UserRegisterDTO(
                TEST_NAME,
                TEST_EMAIL,
                "admin1234",
                TEST_CPF,
                "11223344",
                "81987501006",
                "Rua Teste 123",
                "123",
                null,
                "Centro",
                "Recife",
                "PE",
                "50000000",
                Gender.MALE,
                LocalDate.of(1990, 1, 1),
                null,
                true
        );

        // register test
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(TEST_NAME))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.cpf").value(TEST_CPF));

        // get by cpf
        mockMvc.perform(get("/api/v1/users").param("cpf", TEST_CPF))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value(TEST_CPF))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.name").value(TEST_NAME));
    }
}