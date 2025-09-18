package com.diegobrsantosdev.user_registration_application.validators;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ValidCpfValidatorTest {

    private final ValidCpfValidator validator = new ValidCpfValidator();

    @Test
    void testValidCpfs() {
        // CPFs válidos reais ou de exemplo
        assertTrue(validator.isValid("52998224725", null)); // Válido
        assertTrue(validator.isValid("12345678909", null)); // Válido
    }

    @Test
    void testInvalidCpfs() {
        // CPFs inválidos
        assertFalse(validator.isValid("11111111111", null)); // Repetido
        assertFalse(validator.isValid("12345678901", null)); // Dígito errado
        assertFalse(validator.isValid("abcdefghijk", null)); // Letras
        assertFalse(validator.isValid("5299822472", null));  // Menos de 11 dígitos
        assertFalse(validator.isValid(null, null));          // Nulo
    }
}