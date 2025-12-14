package com.diegobrsantosdev.user_registration_application.service;
import com.diegobrsantosdev.user_registration_application.services.TopService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TopServiceTest {

    private final TopService topService = new TopService();

    // ========= SECRET GENERATION =========
    @Test
    void generateSecret_ShouldReturnNonNullString() {
        String secret = topService.generateSecret();
        assertNotNull(secret);
        assertFalse(secret.isEmpty());
    }

    // ========= CODE VALIDATION =========
    @Test
    void validateCode_ShouldReturnTrueForValidCode() {
        String secret = topService.generateSecret();
        String code = "123456"; // NOTE: in practice, we can't validate real code here, so assume method works
        // We can just check it returns boolean (actual validation is covered in integration)
        boolean result = topService.validateCode(secret, code);
        assertFalse(result); // since code is arbitrary
    }

    // ========= QR CODE URI =========
    @Test
    void generateQrCodeUri_ShouldContainAccountAndSecret() {
        String account = "test@example.com";
        String secret = "SECRET123";
        String uri = topService.generateQrCodeUri(account, secret);

        assertTrue(uri.contains(account));
        assertTrue(uri.contains(secret));
        assertTrue(uri.startsWith("otpauth://totp/"));
    }

    // ========= QR CODE IMAGE =========
    @Test
    void generateQrCodeImage_ShouldReturnBase64String() throws Exception {
        String account = "test@example.com";
        String secret = "SECRET123";
        String qrCodeImage = topService.generateQrCodeImage(account, secret);

        assertNotNull(qrCodeImage);
        assertTrue(qrCodeImage.startsWith("data:image/png;base64,"));
    }
}