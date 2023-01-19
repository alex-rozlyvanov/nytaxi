package com.goal.taxi.front.controller;

import com.goal.taxi.front.dto.LoginRequest;
import com.goal.taxi.front.dto.LoginResponse;
import com.goal.taxi.front.dto.TokenRefreshRequest;
import com.goal.taxi.front.dto.TokenRefreshResponse;
import com.goal.taxi.front.service.LoginService;
import com.goal.taxi.front.service.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {
    @Mock
    private LoginService mockLoginService;
    @Mock
    private RefreshTokenService mockRefreshTokenService;

    @InjectMocks
    private AuthenticationController service;

    @Test
    void login_callLogin() {
        // GIVEN
        final var loginRequest = LoginRequest.builder()
                .id("testId")
                .password("testPass")
                .build();
        when(mockLoginService.login(any())).thenReturn(Mono.just(LoginResponse.builder().build()));

        // WHEN
        final var mono = service.login(loginRequest);

        // THEN
        StepVerifier.create(mono).expectNextCount(1).verifyComplete();
        verify(mockLoginService).login(loginRequest);
    }

    @Test
    void login_checkResult() {
        // GIVEN
        final var loginResponse = LoginResponse.builder().build();
        when(mockLoginService.login(any())).thenReturn(Mono.just(loginResponse));

        // WHEN
        final var result = service.login(null);

        // THEN
        assertThat(result.block()).isSameAs(loginResponse);
    }

    @Test
    void refreshToken_callRefreshToken() {
        // GIVEN
        final var refreshRequest = TokenRefreshRequest.builder()
                .refreshToken("test_refresh_token")
                .build();

        final var tokenRefreshResponse = TokenRefreshResponse.builder().build();
        when(mockRefreshTokenService.refreshToken(any())).thenReturn(Mono.just(tokenRefreshResponse));

        // WHEN
        final var mono = service.refreshToken(refreshRequest);

        // THEN
        StepVerifier.create(mono).expectNextCount(1).verifyComplete();
        verify(mockRefreshTokenService).refreshToken("test_refresh_token");
    }

    @Test
    void refreshToken_checkResult() {
        // GIVEN
        final var refreshRequest = TokenRefreshRequest.builder().build();
        final var tokenRefreshResponse = TokenRefreshResponse.builder()
                .accessToken("some_access_token_123")
                .refreshToken("some_access_token_123")
                .build();
        when(mockRefreshTokenService.refreshToken(any())).thenReturn(Mono.just(tokenRefreshResponse));

        // WHEN
        final var mono = service.refreshToken(refreshRequest);

        // THEN
        StepVerifier.create(mono)
                .assertNext(result -> {
                    assertThat(result.getAccessToken()).isEqualTo("some_access_token_123");
                    assertThat(result.getRefreshToken()).isEqualTo("some_access_token_123");
                })
                .verifyComplete();
    }

}
