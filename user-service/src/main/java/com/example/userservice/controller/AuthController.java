package com.example.userservice.controller;

import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateUser(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return userRepository.findByEmail(email)
                .filter(user -> encoder.matches(password, user.getPassword()))
                .map(user -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "VALID");
                    response.put("userId", user.getId());
                    response.put("email", user.getEmail());
                    // Add any other user fields you want to include
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status", "INVALID")));
    }
}