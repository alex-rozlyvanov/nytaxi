package com.goal.taxi.client.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private final String id;
    private final String password;
}
