package com.diegobrsantosdev.user_registration_application.controllers;

import com.diegobrsantosdev.user_registration_application.dtos.Login2faResponseDTO;
import com.diegobrsantosdev.user_registration_application.dtos.TwoFactorSetupResponseDTO;
import com.diegobrsantosdev.user_registration_application.dtos.TwoFactorVerifyRequestDTO;
import com.diegobrsantosdev.user_registration_application.dtos.TwoFactorVerifyResponseDTO;
import com.diegobrsantosdev.user_registration_application.services.TwoFactorAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/2fa")
@RequiredArgsConstructor
public class TwoFactorAuthController {

    private final TwoFactorAuthService twoFactorAuthService;

    @PostMapping("/login")
    public ResponseEntity<Login2faResponseDTO> loginWith2FA(@RequestBody TwoFactorVerifyRequestDTO request) {
        String token = twoFactorAuthService.loginWith2FA(request);
        return ResponseEntity.ok(new Login2faResponseDTO(token));
    }

    // Step 2: Setup 2FA (requires user to be logged in)
    @PostMapping("/setup")
    public ResponseEntity<TwoFactorSetupResponseDTO> setup2FA(Authentication authentication) throws Exception {
        TwoFactorSetupResponseDTO response = twoFactorAuthService.setup2FA(authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Step 3: Verify 2FA code and activate
    @PostMapping("/verify")
    public ResponseEntity<TwoFactorVerifyResponseDTO> verify2FA(@RequestBody TwoFactorVerifyRequestDTO request) {
        TwoFactorVerifyResponseDTO response = twoFactorAuthService.verify2FA(request);
        return ResponseEntity.ok(response);
    }

}

