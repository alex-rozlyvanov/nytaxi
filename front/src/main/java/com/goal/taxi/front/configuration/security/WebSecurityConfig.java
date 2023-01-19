package com.goal.taxi.front.configuration.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@EnableWebFluxSecurity
public class WebSecurityConfig {

    private final ReactiveAuthenticationManager authenticationManager;
    private final ServerSecurityContextRepository securityContextRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(final ServerHttpSecurity http) {

        return http
                // Disable CORS and disable CSRF
                .csrf().disable()
                .cors().disable()
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                // Set unauthorized requests exception handler
                .exceptionHandling()
                .authenticationEntryPoint(
                        (swe, ex) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
                .accessDeniedHandler(
                        (swe, ex) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN)))
                .and()
                // Set permissions on endpoints
                .authorizeExchange()
                .pathMatchers(HttpMethod.POST, "/api/v1/authenticator/*").permitAll()
                .pathMatchers("/docs/**").permitAll()
                .pathMatchers("/swagger/**").permitAll()
                .pathMatchers("/api/v1/message").authenticated()
                .pathMatchers("/api/v1/total").authenticated()
                .anyExchange().authenticated()
                .and()
                .build();
    }

}
