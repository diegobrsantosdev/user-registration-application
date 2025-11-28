package com.diegobrsantosdev.user_registration_application.viaCep;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CepController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ViaCepExceptionHandler.class)
public class CepControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CepService cepService;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void clearCepCache() {
        if (cacheManager.getCache("ceps") != null) {
            cacheManager.getCache("ceps").clear();
        }
    }

    @Test
    void shouldReturn404WhenCepNotFound() throws Exception {
        String invalidCep = "99999999";
        when(cepService.lookupCep(invalidCep))
                .thenThrow(new CepNotFoundException(invalidCep));

        mockMvc.perform(get("/api/v1/cep/{cep}", invalidCep)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
