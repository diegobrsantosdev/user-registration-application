package com.diegobrsantosdev.user_registration_application.viaCep;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@EnableCaching
@SpringBootTest
@ContextConfiguration(classes = {CepServiceIntegrationTest.TestConfig.class})
class CepServiceIntegrationTest {

    @Configuration
    static class TestConfig {

        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }

        @Bean
        public CepService cepService(RestTemplate restTemplate) {
            return new CepService(restTemplate, "http://fake-url/");
        }

        @Bean
        public CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("ceps");
        }
    }

    @Autowired
    private CepService cepService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CacheManager cacheManager;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);

        if (cacheManager.getCache("ceps") != null) {
            cacheManager.getCache("ceps").clear();
        }
    }

    @Test
    void shouldReturnCepResponseWhenValidCep() {
        String cep = "01001000";
        String json = """
                {
                  "cep": "01001-000",
                  "logradouro": "Praça da Sé",
                  "complemento": "lado ímpar",
                  "bairro": "Sé",
                  "localidade": "São Paulo",
                  "uf": "SP"
                }
                """;

        mockServer.expect(requestTo("http://fake-url/01001000/json/"))
                .andExpect(method(GET))
                .andRespond(withSuccess(json, APPLICATION_JSON));

        var response = cepService.lookupCep(cep);

        assertEquals("01001-000", response.zipCode());
        assertEquals("Praça da Sé", response.address());
        assertEquals("São Paulo", response.city());
    }

    @Test
    void shouldThrowCepNotFoundExceptionWhenCepDoesNotExist() {
        String cep = "99999999";

        mockServer.expect(requestTo("http://fake-url/99999999/json/"))
                .andExpect(method(GET))
                .andRespond(withSuccess("{}", APPLICATION_JSON));

        assertThrows(CepNotFoundException.class, () -> cepService.lookupCep(cep));
    }

    @Test
    void shouldThrowRuntimeExceptionWhenApiFails() {
        String cep = "33333333";

        mockServer.expect(requestTo("http://fake-url/33333333/json/"))
                .andExpect(method(GET))
                .andRespond(withServerError());

        assertThrows(RuntimeException.class, () -> cepService.lookupCep(cep));
    }

    @Test
    void shouldUseCacheWhenSameCEPs() {
        String cep = "01001000";
        String json = """
                {
                  "cep": "01001-000",
                  "logradouro": "Praça da Sé",
                  "complemento": "",
                  "bairro": "Sé",
                  "localidade": "São Paulo",
                  "uf": "SP"
                }
                """;

        mockServer.expect(requestTo("http://fake-url/01001000/json/"))
                .andExpect(method(GET))
                .andRespond(withSuccess(json, APPLICATION_JSON));

        var r1 = cepService.lookupCep(cep);
        var r2 = cepService.lookupCep(cep);

        // MockRestServiceServer falhará se houver mais de uma chamada HTTP
        mockServer.verify();

        assertSame(r1, r2, "Deve retornar o mesmo objeto do cache");
    }
}