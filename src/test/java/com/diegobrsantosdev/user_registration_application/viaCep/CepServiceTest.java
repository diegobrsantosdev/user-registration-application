package com.diegobrsantosdev.user_registration_application.viaCep;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CepServiceTest {

    @Test
    void shouldThrowExceptionWhenCepNotFound() {
        CepService cepService = new CepService() {
            @Override
            public CepResponseDTO lookupCep(String cep) {
                throw new CepNotFoundException(cep);
            }
        };

        String cepInvalido = "99999999";
        CepNotFoundException thrown = assertThrows(
            CepNotFoundException.class,
            () -> cepService.lookupCep(cepInvalido)
        );

        assertTrue(thrown.getMessage().contains(cepInvalido));
    }
}