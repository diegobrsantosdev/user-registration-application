package com.diegobrsantosdev.user_registration_application.controllers;

import com.diegobrsantosdev.user_registration_application.dtos.*;
import com.diegobrsantosdev.user_registration_application.exceptions.ResourceNotFoundException;
import com.diegobrsantosdev.user_registration_application.services.UserService;
import com.diegobrsantosdev.user_registration_application.shared.ApiMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    // ========= READ =========
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Integer id) {
        UserResponseDTO dto = service.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @GetMapping
    public ResponseEntity<UserResponseDTO> getUser(
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String email) {
        if (cpf != null) {
            return service.getUserByCpf(cpf)
                    .map(ResponseEntity::ok)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        }
        if (email != null) {
            return service.getUserByEmail(email)
                    .map(ResponseEntity::ok)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        }
        return ResponseEntity.badRequest().body(null);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<UserResponseDTO>> listAllUsers(Pageable pageable) {
        Page<UserResponseDTO> users = service.listAllUsers(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }


    // ========= CREATE =========
    @PostMapping
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody @Valid UserRegisterDTO dto) {
        UserResponseDTO response = service.registerUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ========= UPDATE =========
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Integer id,
                                                     @RequestBody @Valid UserUpdateDTO dto) {
        UserResponseDTO updated = service.updateUser(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<PasswordMessageResponseDTO> updatePassword(
            @PathVariable Integer id,
            @RequestBody @Valid PasswordDTO dto) {

        service.updatePassword(id, dto);

        return ResponseEntity.ok(
                new PasswordMessageResponseDTO(ApiMessages.PASSWORD_UPDATED)
        );
    }

    // ========= DELETE =========
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        service.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}