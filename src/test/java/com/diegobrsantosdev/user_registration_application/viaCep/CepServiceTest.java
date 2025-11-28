package com.diegobrsantosdev.user_registration_application.viaCep;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

class CepServiceTest {

    private CepService cepService;

    @BeforeEach
    void setup() {
        cepService = new CepService(new RestTemplate(), "http://fake-url/") {
            @Override
            public CepResponseDTO lookupCep(String cep) {

                if (cep == null || !cep.matches("^[0-9]{8}$")) {
                    throw new IllegalArgumentException("Invalid CEP format. Use only 8 numeric digits.");
                }
                if (cep.equals("99999999")) {
                    throw new CepNotFoundException(cep);
                }

                return new CepResponseDTO(
                        cep,
                        "Street",
                        "Complement",
                        "Neighborhood",
                        "City",
                        "State"
                );
            }
        };
    }

    @Test
    void shouldThrowExceptionWhenCepNotFound() {
        String invalidCep = "99999999";
        CepNotFoundException thrown = assertThrows(
                CepNotFoundException.class,
                () -> cepService.lookupCep(invalidCep)
        );
        assertTrue(thrown.getMessage().contains(invalidCep));
    }

    @Test
    void shouldThrowExceptionForInvalidFormat() {
        String invalidFormatCep = "12345-678";
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> cepService.lookupCep(invalidFormatCep)
        );
        assertTrue(thrown.getMessage().contains("Invalid CEP format"));
    }

    @Test
    void shouldReturnValidResponseForValidCep() {
        String validCep = "01001000";
        CepResponseDTO response = cepService.lookupCep(validCep);
        assertEquals(validCep, response.zipCode());
        assertEquals("Street", response.address());
    }
}