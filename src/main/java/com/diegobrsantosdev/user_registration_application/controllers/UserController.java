package com.diegobrsantosdev.user_registration_application.controllers;

import com.diegobrsantosdev.user_registration_application.config.UserDetailsImpl;
import com.diegobrsantosdev.user_registration_application.dtos.*;
import com.diegobrsantosdev.user_registration_application.services.UserService;
import com.diegobrsantosdev.user_registration_application.shared.ApiMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        UserResponseDTO dto = userService.getUserById(currentUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateCurrentUser(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @RequestBody @Valid UserUpdateDTO dto) {
        UserResponseDTO updated = userService.updateUser(currentUser.getId(), dto);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @PutMapping("/me/password")
    public ResponseEntity<PasswordMessageResponseDTO> updatePassword(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @RequestBody @Valid PasswordDTO dto) {

        userService.updatePassword(currentUser.getId(), dto);

        return ResponseEntity.ok(
                new PasswordMessageResponseDTO(ApiMessages.PASSWORD_UPDATED)
        );
    }

    @DeleteMapping("/me")
    public ResponseEntity<MessageResponseDTO> deleteOwnUser(
            @AuthenticationPrincipal UserDetailsImpl currentUser) {

        return ResponseEntity.ok(userService.deleteOwnUser(currentUser.getId()));
    }
}