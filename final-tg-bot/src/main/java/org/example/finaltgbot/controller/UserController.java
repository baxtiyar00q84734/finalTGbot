package org.example.finaltgbot.controller;

import lombok.RequiredArgsConstructor;
import org.example.finaltgbot.dto.request.UserRequestDTO;
import org.example.finaltgbot.dto.response.UserResponseDTO;
import org.example.finaltgbot.entity.User;
import org.example.finaltgbot.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser (@RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO createdUser  = userService.createUser (userRequestDTO);
        return ResponseEntity.status(201).body(createdUser );
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser (@PathVariable Long id, @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO updatedUser  = userService.updateUser (id, userRequestDTO);
        return ResponseEntity.ok(updatedUser );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser (@PathVariable Long id) {
        userService.deleteUser (id);
        return ResponseEntity.noContent().build();
    }
}
