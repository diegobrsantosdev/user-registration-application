package com.diegobrsantosdev.user_registration_application.viaCep;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest
class CepServiceIntegrationTest {

    @Autowired
    private CepService cepService;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @Autowired
    private CacheManager cacheManager;


    @BeforeEach
    void setup() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        //clean cache before test
        if (cacheManager.getCache("ceps") != null) {
            cacheManager.getCache("ceps").clear();
        }
    }

    @Test
    void shouldReturnCepResponseWhenValidCep() throws Exception {
        String cep = "01001000";

        String jsonResponse = """
            {
              "cep": "01001-000",
              "logradouro": "Praça da Sé",
              "complemento": "lado ímpar",
              "bairro": "Sé",
              "localidade": "São Paulo",
              "uf": "SP"
            }
        """;

        mockServer.expect(requestTo("https://viacep.com.br/ws/" + cep + "/json/"))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        CepResponseDTO response = cepService.lookupCep(cep);

        assertEquals("01001-000", response.zipCode());
        assertEquals("Praça da Sé", response.address());

        mockServer.verify(); // ensure all expected calls happened
    }

    @Test
    void shouldThrowCepNotFoundExceptionWhenCepDoesNotExist() {
        String cep = "99999999";

        String jsonResponse = "{}"; // empty JSON simulates not found

        mockServer.expect(requestTo("https://viacep.com.br/ws/" + cep + "/json/"))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        assertThrows(CepNotFoundException.class, () -> cepService.lookupCep(cep));
        mockServer.verify();
    }

    @Test
    void shouldThrowRuntimeExceptionWhenApiFails() {
        String cep = "01001000";

        mockServer.expect(requestTo("https://viacep.com.br/ws/" + cep + "/json/"))
                .andRespond(withServerError());

        assertThrows(RuntimeException.class, () -> cepService.lookupCep(cep));
        mockServer.verify();
    }

    @Test
    void shouldUseCacheWhenSameCEPs() {
        String cep = "01001000";
        String jsonResponse = """
        {
          "cep": "01001-000",
          "logradouro": "Praça da Sé",
          "complemento": "lado ímpar",
          "bairro": "Sé",
          "localidade": "São Paulo",
          "uf": "SP"
        }
    """;
        // Expect ONE call to the external API (only the first lookup should access the real endpoint)
        mockServer.expect(requestTo("https://viacep.com.br/ws/" + cep + "/json/"))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        // First lookup: should perform the HTTP request and store the result in the cache
        CepResponseDTO firstRequest = cepService.lookupCep(cep);

        // Second lookup: should fetch the result directly from the cache, without making another HTTP request
        CepResponseDTO secondRequest = cepService.lookupCep(cep);

        assertThat(firstRequest).isEqualTo(secondRequest);

        // Verifies that only ONE HTTP request was made (the one above)
        // If a second HTTP call is performed, the test will fail here
        mockServer.verify();
    }


}
