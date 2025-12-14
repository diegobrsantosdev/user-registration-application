package com.diegobrsantosdev.user_registration_application.controllers;

import com.diegobrsantosdev.user_registration_application.dtos.TwoFactorSetupResponseDTO;
import com.diegobrsantosdev.user_registration_application.dtos.TwoFactorVerifyRequestDTO;
import com.diegobrsantosdev.user_registration_application.dtos.TwoFactorVerifyResponseDTO;
import com.diegobrsantosdev.user_registration_application.services.TwoFactorAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TwoFactorAuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class TwoFactorAuthControllerTest {

    private static final String EMAIL = "user@email.com";
    private static final String VALID_CODE = "123456";
    private static final String SECRET = "JBSWY3DPEHPK3PXP";
    private static final String QR_CODE = "data:image/png;base64,AAAA...";
    private static final String TOKEN = "FAKE_2FA_JWT";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TwoFactorAuthService twoFactorAuthService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldSetup2FA() throws Exception {
        Authentication auth = Mockito.mock(Authentication.class);

        TwoFactorSetupResponseDTO responseDTO = new TwoFactorSetupResponseDTO(SECRET, QR_CODE);
        Mockito.when(twoFactorAuthService.setup2FA(auth)).thenReturn(responseDTO);

        mockMvc.perform(post("/auth/2fa/setup")
                        .principal(auth))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.secret").value(SECRET))
                .andExpect(jsonPath("$.qrCode").value(QR_CODE));
    }

    @Test
    void shouldVerify2FA() throws Exception {

        TwoFactorVerifyRequestDTO request =
                new TwoFactorVerifyRequestDTO(EMAIL, VALID_CODE);

        TwoFactorVerifyResponseDTO response =
                new TwoFactorVerifyResponseDTO("2FA enabled successfully", TOKEN);

        Mockito.when(twoFactorAuthService.verify2FA(eq(request)))
                .thenReturn(response);

        mockMvc.perform(post("/auth/2fa/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("2FA enabled successfully"))
                .andExpect(jsonPath("$.token").value(TOKEN));
    }
}
