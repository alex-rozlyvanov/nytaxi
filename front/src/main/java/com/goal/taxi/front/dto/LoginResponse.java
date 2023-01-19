package com.goal.taxi.front.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class LoginResponse {
    private final String accessToken;
    private final String refreshToken;
}
