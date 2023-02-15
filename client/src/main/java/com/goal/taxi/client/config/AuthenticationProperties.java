package com.goal.taxi.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Data
@Component
@ConfigurationProperties(prefix = "authentication")
public class AuthenticationProperties {
    private String url = "http://localhost:8080/api/v1/login";
    private Duration tokenExpiration = Duration.ofMinutes(4);
    private String id;
    private String password;
}
