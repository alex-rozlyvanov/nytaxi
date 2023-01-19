package com.goal.taxi.front.controller;

import com.goal.taxi.front.dto.LoginRequest;
import com.goal.taxi.front.dto.LoginResponse;
import com.goal.taxi.front.dto.TokenRefreshRequest;
import com.goal.taxi.front.dto.TokenRefreshResponse;
import com.goal.taxi.front.service.LoginService;
import com.goal.taxi.front.service.RefreshTokenService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/authenticator")
@AllArgsConstructor
public class AuthenticationController {
    private final LoginService loginService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public Mono<LoginResponse> login(@RequestBody @Valid final LoginRequest request) {
        return loginService.login(request);
    }

    @PostMapping("/refresh")
    public Mono<TokenRefreshResponse> refreshToken(@RequestBody @Valid final TokenRefreshRequest request) {
        return refreshTokenService.refreshToken(request.getRefreshToken());
    }
}
