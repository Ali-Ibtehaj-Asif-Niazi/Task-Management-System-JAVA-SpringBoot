package com.example.userservice.controller;

import com.example.userservice.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/validate")
    public ResponseEntity<String> validateUser(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return userRepository.findByEmail(email)
                .filter(user -> encoder.matches(password, user.getPassword()))
                .map(user -> ResponseEntity.ok("VALID"))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("INVALID"));
    }
}

