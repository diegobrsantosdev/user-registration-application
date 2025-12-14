package com.diegobrsantosdev.user_registration_application.services;

import com.diegobrsantosdev.user_registration_application.dtos.*;
import com.diegobrsantosdev.user_registration_application.exceptions.InvalidDataException;
import com.diegobrsantosdev.user_registration_application.exceptions.UsernameNotFoundException;
import com.diegobrsantosdev.user_registration_application.models.User;
import com.diegobrsantosdev.user_registration_application.repositories.UserRepository;
import com.diegobrsantosdev.user_registration_application.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TwoFactorAuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final TopService topService;

    // helper method to get the authenticated User from Authentication
    private User getAuthenticatedUser(Authentication authentication) {
        String email = authentication.getName(); // get sername/email
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public TwoFactorSetupResponseDTO setup2FA(Authentication authentication) throws Exception {
        User user = getAuthenticatedUser(authentication); // use method
        String secret = topService.generateSecret();
        String qrCode = topService.generateQrCodeImage(user.getEmail(), secret);

        user.setTwoFactorSecret(secret);
        userRepository.save(user);

        return new TwoFactorSetupResponseDTO(secret, qrCode);
    }

    public TwoFactorVerifyResponseDTO verify2FA(TwoFactorVerifyRequestDTO request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        boolean valid = topService.validateCode(user.getTwoFactorSecret(), request.code());
        if (!valid) throw new InvalidDataException("Invalid 2FA code");

        user.setTwoFactorEnabled(true);
        userRepository.save(user);

        List<String> roles = user.getRoles().stream()
                .map(role -> "ROLE_" + role.name())
                .toList();

        String token = jwtUtil.generateToken(user.getEmail(), roles);
        return new TwoFactorVerifyResponseDTO("2FA activated successfully!", token);
    }

    public String loginWith2FA(TwoFactorVerifyRequestDTO request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.getTwoFactorEnabled()) {
            throw new InvalidDataException("This user does not have 2FA enabled.");
        }

        boolean valid = topService.validateCode(user.getTwoFactorSecret(), request.code());
        if (!valid) {
            throw new InvalidDataException("Invalid 2FA code");
        }

        List<String> roles = user.getRoles().stream()
                .map(role -> "ROLE_" + role.name())
                .toList();

        return jwtUtil.generateToken(user.getEmail(), roles);
    }

}