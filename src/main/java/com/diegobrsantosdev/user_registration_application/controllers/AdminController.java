package com.diegobrsantosdev.user_registration_application.controllers;

import com.diegobrsantosdev.user_registration_application.dtos.UserResponseDTO;
import com.diegobrsantosdev.user_registration_application.models.Role;
import com.diegobrsantosdev.user_registration_application.models.User;
import com.diegobrsantosdev.user_registration_application.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public List<UserResponseDTO> listAllUsers() {
        return userService.listAllUsers(Pageable.unpaged())
                .getContent();
    }

    @PutMapping("/{id}/promote")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> promoteToAdmin(@PathVariable Integer id) {

        User user = userService.findById(id);
        user.setRoles(Set.of(Role.ADMIN));

        userService.save(user);

        return ResponseEntity.ok(UserResponseDTO.fromEntity(user));
    }

}

