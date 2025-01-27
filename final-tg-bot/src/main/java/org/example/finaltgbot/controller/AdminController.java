package org.example.finaltgbot.controller;

import org.example.finaltgbot.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/{email}/activate")
    public ResponseEntity<String> activateUser(@PathVariable String email) {
        userService.activateUserByEmail(email);
        return ResponseEntity.ok("User activated successfully.");
    }
}

