package com.example.apigateway.security;

import com.example.apigateway.util.JwtUtil;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        
        try {
            if (!JwtUtil.validateToken(authToken)) {
                return Mono.error(new InvalidJwtException("Invalid JWT token"));
            }
            if (JwtUtil.isTokenExpired(authToken)) {
                return Mono.error(new JwtExpiredException("JWT token has expired"));
            }
            String username = JwtUtil.extractUsername(authToken);
            // You might want to load user details from a database here
            return Mono.just(new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>()));
        } catch (Exception e) {
            return Mono.error(new AuthenticationException("Authentication failed", e));
        }
    }
}