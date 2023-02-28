package com.goal.taxi.front.service;

import com.goal.taxi.front.dao.entity.Client;
import com.goal.taxi.front.dao.repository.ClientRepository;
import com.goal.taxi.front.dto.LoginRequest;
import com.goal.taxi.front.dto.LoginResponse;
import com.goal.taxi.front.exception.ClientNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private JwtTokenService mockJwtTokenService;
    @Mock
    private RefreshTokenService mockRefreshTokenService;
    @Mock
    private ClientRepository mockClientRepository;
    @Mock
    private PasswordEncoder mockPasswordEncoder;
    @InjectMocks
    private LoginService service;

    @Test
    void login_callFindById() {
        // GIVEN
        final var client = new Client();
        when(mockClientRepository.findById(any())).thenReturn(Mono.just(client));
        when(mockPasswordEncoder.matches(any(), any())).thenReturn(true);
        when(mockRefreshTokenService.createRefreshToken(any())).thenReturn(Mono.just("test_refresh_token"));

        final var request = LoginRequest.builder().id("test-id").build();

        // WHEN
        final var mono = service.login(request);

        // THEN
        StepVerifier.create(mono).expectNextCount(1).verifyComplete();
        verify(mockClientRepository).findById("test-id");
    }

    @Test
    void login_callMatches() {
        // GIVEN
        final var client = new Client().setPassword("encoded");
        when(mockClientRepository.findById(any())).thenReturn(Mono.just(client));
        when(mockPasswordEncoder.matches(any(), any())).thenReturn(true);
        when(mockRefreshTokenService.createRefreshToken(any())).thenReturn(Mono.just("test_refresh_token"));

        final var request = LoginRequest.builder().id("test-id").password("raw").build();

        // WHEN
        final var mono = service.login(request);

        // THEN
        StepVerifier.create(mono).expectNextCount(1).verifyComplete();
        verify(mockPasswordEncoder).matches("raw", "encoded");
    }

    @Test
    void login_callGenerateAccessToken() {
        // GIVEN
        final var client = new Client().setPassword("encoded");
        when(mockClientRepository.findById(any())).thenReturn(Mono.just(client));
        when(mockPasswordEncoder.matches(any(), any())).thenReturn(true);
        when(mockRefreshTokenService.createRefreshToken(any())).thenReturn(Mono.just("test_refresh_token"));

        final var request = LoginRequest.builder().id("test-id").password("raw").build();

        // WHEN
        final var mono = service.login(request);

        // THEN
        StepVerifier.create(mono).expectNextCount(1).verifyComplete();
        verify(mockJwtTokenService).generateAccessToken(client);
    }

    @Test
    void login_callCreateRefreshToken() {
        // GIVEN
        final var client = new Client().setPassword("encoded");
        when(mockClientRepository.findById(any())).thenReturn(Mono.just(client));
        when(mockPasswordEncoder.matches(any(), any())).thenReturn(true);
        when(mockRefreshTokenService.createRefreshToken(any())).thenReturn(Mono.just("test_refresh_token"));

        final var request = LoginRequest.builder().id("test-id").password("raw").build();

        // WHEN
        final var mono = service.login(request);

        // THEN
        StepVerifier.create(mono).expectNextCount(1).verifyComplete();
        verify(mockRefreshTokenService).createRefreshToken(client);
    }

    @Test
    void login_checkResult() {
        // GIVEN
        final var client = new Client().setPassword("encoded");
        when(mockClientRepository.findById(any())).thenReturn(Mono.just(client));
        when(mockPasswordEncoder.matches(any(), any())).thenReturn(true);
        when(mockJwtTokenService.generateAccessToken(any())).thenReturn("accessToken");
        when(mockRefreshTokenService.createRefreshToken(any())).thenReturn(Mono.just("refreshToken"));

        final var request = LoginRequest.builder().id("test-id").password("raw").build();

        // WHEN
        final var mono = service.login(request);

        // THEN
        final var expected = LoginResponse.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();
        StepVerifier.create(mono)
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    void login_clientNotFound_exceptionThrown() {
        // GIVEN
        when(mockClientRepository.findById(any())).thenReturn(Mono.empty());

        final var request = LoginRequest.builder().id("test-id").password("raw").build();

        // WHEN
        final var mono = service.login(request);

        // THEN
        StepVerifier.create(mono)
                .expectErrorSatisfies(throwable -> {
                    assertThat(throwable)
                            .isInstanceOf(ClientNotFoundException.class)
                            .hasMessage("400 BAD_REQUEST \"Client not found by id 'test-id'\"");
                })
                .verify();
    }

    @Test
    void login_passwordDoesNot_exceptionThrown() {
        // GIVEN
        final var client = new Client().setPassword("encoded");
        when(mockClientRepository.findById(any())).thenReturn(Mono.just(client));
        when(mockPasswordEncoder.matches(any(), any())).thenReturn(false);
//        when(mockJwtTokenService.generateAccessToken(any())).thenReturn("accessToken");
//        when(mockRefreshTokenService.createRefreshToken(any())).thenReturn(Mono.just("refreshToken"));

        final var request = LoginRequest.builder().id("test-id").password("raw").build();

        // WHEN
        final var mono = service.login(request);

        // THEN
        StepVerifier.create(mono)
                .expectErrorSatisfies(throwable -> {
                    assertThat(throwable)
                            .isInstanceOf(BadCredentialsException.class)
                            .hasMessage("Bad credentials");
                })
                .verify();
    }

}
