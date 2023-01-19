package com.goal.taxi.front.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@Jacksonized
public class LoginRequest {
    @NotNull
    private final String id;
    @NotNull
    private final String password;
}
