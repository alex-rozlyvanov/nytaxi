package com.goal.taxi.front.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class TokenRefreshResponse {
    private final String accessToken;
    private final String refreshToken;
}
