package com.goal.taxi.client.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goal.taxi.client.dto.LoginRequest;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class TokenServiceConfig {
    private final AuthenticationProperties authenticationProperties;

    @Bean
    String loginEntity() throws JsonProcessingException {
        final var loginRequest = new LoginRequest(authenticationProperties.getId(), authenticationProperties.getPassword());
        return new ObjectMapper().writeValueAsString(loginRequest);
    }
}
