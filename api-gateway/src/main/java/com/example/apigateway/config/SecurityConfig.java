package com.example.apigateway.config;

import com.example.apigateway.security.JwtAuthenticationManager;
import com.example.apigateway.security.JwtServerSecurityContextRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtAuthenticationManager jwtAuthenticationManager;
    private final JwtServerSecurityContextRepository jwtServerSecurityContextRepository;

    public SecurityConfig(JwtAuthenticationManager jwtAuthenticationManager,
                          JwtServerSecurityContextRepository jwtServerSecurityContextRepository) {
        this.jwtAuthenticationManager = jwtAuthenticationManager;
        this.jwtServerSecurityContextRepository = jwtServerSecurityContextRepository;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authenticationManager(jwtAuthenticationManager)
                .securityContextRepository(jwtServerSecurityContextRepository)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/auth/**").permitAll()
                        .pathMatchers("/admin/**").hasRole("ADMIN")
                        .anyExchange().authenticated()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((exchange, ex) -> {
                            log.error("Unauthorized error: {}", ex.getMessage());
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        })
                        .accessDeniedHandler((exchange, denied) -> {
                            log.error("Access denied error: {}", denied.getMessage());
                            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            return exchange.getResponse().setComplete();
                        })
                )
                .httpBasic().disable()
                .formLogin().disable()
                .build();
    }
}