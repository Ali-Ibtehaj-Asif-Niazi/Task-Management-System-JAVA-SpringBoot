package com.example.apigateway.controller;

import com.example.apigateway.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final WebClient.Builder webClientBuilder;

    public AuthController(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");

        return webClientBuilder.build()
                .post()
                .uri("http://localhost:8080/auth/validate")
                .bodyValue(credentials)
                .retrieve()
                .bodyToMono(String.class)
                .map(validationResponse -> {
                    if ("VALID".equals(validationResponse)) {
                        String token = JwtUtil.generateToken(email);
                        return ResponseEntity.ok(token);
                    } else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
                    }
                })
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Server error: " + e.getMessage())));
    }
}