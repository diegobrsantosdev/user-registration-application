package com.diegobrsantosdev.user_registration_application.service;

import com.diegobrsantosdev.user_registration_application.dtos.*;
import com.diegobrsantosdev.user_registration_application.exceptions.InvalidDataException;
import com.diegobrsantosdev.user_registration_application.exceptions.UsernameNotFoundException;
import com.diegobrsantosdev.user_registration_application.models.Role;
import com.diegobrsantosdev.user_registration_application.models.User;
import com.diegobrsantosdev.user_registration_application.repositories.UserRepository;
import com.diegobrsantosdev.user_registration_application.security.JwtUtil;
import com.diegobrsantosdev.user_registration_application.services.TwoFactorAuthService;
import com.diegobrsantosdev.user_registration_application.services.TopService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;


import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TwoFactorAuthServiceTest {

    private static final String DEFAULT_EMAIL = "lucas@gmail.com";
    private static final String SECRET = "SECRET123";
    private static final String QR_CODE = "QRCodeImage";
    private static final String VALID_CODE = "123456";
    private static final String JWT_TOKEN = "JWT_TOKEN";

    @InjectMocks
    private TwoFactorAuthService twoFactorAuthService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TopService topService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private Authentication authentication;

    // ======== HELPERS ========
    private User createDefaultUser() {
        User user = new User();
        user.setEmail(DEFAULT_EMAIL);
        user.setRoles(Set.of(Role.USER));
        return user;
    }

    private TwoFactorVerifyRequestDTO createDefaultVerifyRequest() {
        return new TwoFactorVerifyRequestDTO(DEFAULT_EMAIL, VALID_CODE);
    }

    // ========= SETUP 2FA =========
    @Test
    void setup2FA_ShouldReturnSecretAndQrCode_WhenUserExists() throws Exception {
        User user = createDefaultUser();

        when(authentication.getName()).thenReturn(DEFAULT_EMAIL);
        when(userRepository.findByEmail(DEFAULT_EMAIL)).thenReturn(Optional.of(user));
        when(topService.generateSecret()).thenReturn(SECRET);
        when(topService.generateQrCodeImage(DEFAULT_EMAIL, SECRET)).thenReturn(QR_CODE);
        when(userRepository.save(user)).thenReturn(user);

        TwoFactorSetupResponseDTO response = twoFactorAuthService.setup2FA(authentication);

        assertEquals(SECRET, response.secret());
        assertEquals(QR_CODE, response.qrCode());
        verify(userRepository).save(user);
    }

    @Test
    void setup2FA_ShouldThrowUsernameNotFoundException_WhenUserNotFound() {
        when(authentication.getName()).thenReturn(DEFAULT_EMAIL);
        when(userRepository.findByEmail(DEFAULT_EMAIL)).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
                () -> twoFactorAuthService.setup2FA(authentication));
        assertTrue(ex.getMessage().contains("User not found with email"));
    }

    // ========= VERIFY 2FA =========
    @Test
    void loginWith2FA_ShouldReturnToken_WhenValid() {
        User user = createDefaultUser();
        user.setTwoFactorEnabled(true);
        user.setTwoFactorSecret(SECRET);

        TwoFactorVerifyRequestDTO request = createDefaultVerifyRequest();

        when(userRepository.findByEmail(DEFAULT_EMAIL)).thenReturn(Optional.of(user));
        when(topService.validateCode(SECRET, VALID_CODE)).thenReturn(true);
        when(jwtUtil.generateToken(
                eq(DEFAULT_EMAIL),
                anyList()
        )).thenReturn(JWT_TOKEN);

        String token = twoFactorAuthService.loginWith2FA(request);

        assertEquals(JWT_TOKEN, token);
    }


    @Test
    void verify2FA_ShouldThrowInvalidDataException_WhenCodeIsInvalid() {
        User user = createDefaultUser();
        user.setTwoFactorSecret(SECRET);

        TwoFactorVerifyRequestDTO request = createDefaultVerifyRequest();

        when(userRepository.findByEmail(DEFAULT_EMAIL)).thenReturn(Optional.of(user));
        when(topService.validateCode(SECRET, VALID_CODE)).thenReturn(false);

        InvalidDataException ex = assertThrows(InvalidDataException.class,
                () -> twoFactorAuthService.verify2FA(request));
        assertEquals("Invalid 2FA code", ex.getMessage());
    }

    @Test
    void verify2FA_ShouldThrowUsernameNotFoundException_WhenUserNotFound() {
        TwoFactorVerifyRequestDTO request = createDefaultVerifyRequest();
        when(userRepository.findByEmail(DEFAULT_EMAIL)).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
                () -> twoFactorAuthService.verify2FA(request));
        assertTrue(ex.getMessage().contains("User not found with email"));
    }

    // ========= LOGIN WITH 2FA =========
    @Test
    void verify2FA_ShouldActivate2FAAndReturnToken_WhenCodeIsValid() {
        User user = createDefaultUser();
        user.setTwoFactorSecret(SECRET);
        user.setTwoFactorEnabled(false);

        TwoFactorVerifyRequestDTO request = createDefaultVerifyRequest();

        when(userRepository.findByEmail(DEFAULT_EMAIL)).thenReturn(Optional.of(user));
        when(topService.validateCode(SECRET, VALID_CODE)).thenReturn(true);
        when(userRepository.save(user)).thenReturn(user);
        when(jwtUtil.generateToken(
                eq(DEFAULT_EMAIL),
                anyList()
        )).thenReturn(JWT_TOKEN);

        TwoFactorVerifyResponseDTO response = twoFactorAuthService.verify2FA(request);

        assertTrue(user.getTwoFactorEnabled());
        assertEquals("2FA activated successfully!", response.message());
        assertEquals(JWT_TOKEN, response.token());

        verify(userRepository).save(user);
    }

    @Test
    void loginWith2FA_ShouldThrowInvalidDataException_When2FADisabled() {
        User user = createDefaultUser();
        user.setTwoFactorEnabled(false);

        TwoFactorVerifyRequestDTO request = createDefaultVerifyRequest();

        when(userRepository.findByEmail(DEFAULT_EMAIL)).thenReturn(Optional.of(user));

        InvalidDataException ex = assertThrows(InvalidDataException.class,
                () -> twoFactorAuthService.loginWith2FA(request));
        assertEquals("This user does not have 2FA enabled.", ex.getMessage());
    }

    @Test
    void loginWith2FA_ShouldThrowInvalidDataException_WhenCodeInvalid() {
        User user = createDefaultUser();
        user.setTwoFactorEnabled(true);
        user.setTwoFactorSecret(SECRET);

        TwoFactorVerifyRequestDTO request = createDefaultVerifyRequest();

        when(userRepository.findByEmail(DEFAULT_EMAIL)).thenReturn(Optional.of(user));
        when(topService.validateCode(SECRET, VALID_CODE)).thenReturn(false);

        InvalidDataException ex = assertThrows(InvalidDataException.class,
                () -> twoFactorAuthService.loginWith2FA(request));
        assertEquals("Invalid 2FA code", ex.getMessage());
    }

    @Test
    void loginWith2FA_ShouldThrowUsernameNotFoundException_WhenUserNotFound() {
        TwoFactorVerifyRequestDTO request = createDefaultVerifyRequest();
        when(userRepository.findByEmail(DEFAULT_EMAIL)).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
                () -> twoFactorAuthService.loginWith2FA(request));
        assertTrue(ex.getMessage().contains("User not found with email"));
    }
}
