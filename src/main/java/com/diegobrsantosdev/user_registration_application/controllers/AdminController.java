package com.diegobrsantosdev.user_registration_application.controllers;

import com.diegobrsantosdev.user_registration_application.config.UserDetailsImpl;
import com.diegobrsantosdev.user_registration_application.dtos.MessageResponseDTO;
import com.diegobrsantosdev.user_registration_application.dtos.PromoteUserResponseDTO;
import com.diegobrsantosdev.user_registration_application.dtos.UserResponseDTO;
import com.diegobrsantosdev.user_registration_application.exceptions.ResourceNotFoundException;
import com.diegobrsantosdev.user_registration_application.models.User;
import com.diegobrsantosdev.user_registration_application.services.UserService;
import com.diegobrsantosdev.user_registration_application.shared.ApiMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    //Get user by id
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Integer id) {
        UserResponseDTO dto = userService.getUserById(id);
        return ResponseEntity.ok(dto);
    }

    //Get user by cpf
    @GetMapping("/cpf/{cpf}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> getUserByCpf(@PathVariable String cpf) {
        return userService.getUserByCpf(cpf)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    //Get user by email
    @GetMapping("/email/{email:.+}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    //List all users
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponseDTO> listAllUsers() {
        return userService.listAllUsers(Pageable.unpaged())
                .getContent();
    }

    //Promote user as admin
    @PutMapping("/{id}/promote")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PromoteUserResponseDTO> promoteToAdmin(@PathVariable Integer id) {

        User user = userService.promoteToAdmin(id);

        return ResponseEntity.ok(
                new PromoteUserResponseDTO(
                        ApiMessages.USER_PROMOTED,
                        UserResponseDTO.fromEntity(user)
                )
        );
    }

    //Delete user
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponseDTO> deleteUserAsAdmin(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetailsImpl adminUser) {
        MessageResponseDTO dto = userService.deleteUserAsAdmin(id, adminUser.getId());
        return ResponseEntity.ok(dto);
    }



}


