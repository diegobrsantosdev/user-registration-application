package com.diegobrsantosdev.user_registration_application.validators;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ValidCpfValidatorTest {

    private final ValidCpfValidator validator = new ValidCpfValidator();

    @Test
    void testValidCpfs() {

        assertTrue(validator.isValid("52998224725", null));
        assertTrue(validator.isValid("12345678909", null));
    }

    @Test
    void testInvalidCpfs() {

        assertFalse(validator.isValid("11111111111", null));
        assertFalse(validator.isValid("12345678901", null));
        assertFalse(validator.isValid("abcdefghijk", null));
        assertFalse(validator.isValid("5299822472", null));
        assertFalse(validator.isValid(null, null));
    }
}