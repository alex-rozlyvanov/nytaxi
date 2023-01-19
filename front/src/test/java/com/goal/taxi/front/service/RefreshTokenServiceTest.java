package com.goal.taxi.front.service;

import com.goal.taxi.front.dao.entity.Client;
import com.goal.taxi.front.dao.entity.RefreshToken;
import com.goal.taxi.front.dao.repository.ClientRepository;
import com.goal.taxi.front.dao.repository.RefreshTokenRepository;
import com.goal.taxi.front.exception.TokenRefreshException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository mockRefreshTokenRepository;
    @Mock
    private ClientRepository mockClientRepository;
    @Mock
    private JwtTokenService mockJwtTokenService;
    @Mock
    private Clock mockClock;
    @InjectMocks
    private RefreshTokenService service;

    @Test
    void verifyExpiration_checkResult() {
        // GIVEN
        final var now = LocalDateTime.now();
        when(mockClock.instant()).thenReturn(now.toInstant(ZoneOffset.UTC));
        when(mockClock.getZone()).thenReturn(ZoneOffset.UTC.normalized());

        final var refreshToken = new RefreshToken()
                .setExpiryDate(now.plusMinutes(1L));

        // WHEN
        final var mono = service.verifyExpiration(refreshToken);

        // THEN
        StepVerifier.create(mono)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void verifyExpiration_tokenExpired_checkResult() {
        // GIVEN
        final var now = LocalDateTime.now();
        when(mockClock.instant()).thenReturn(now.toInstant(ZoneOffset.UTC));
        when(mockClock.getZone()).thenReturn(ZoneOffset.UTC.normalized());
        when(mockRefreshTokenRepository.delete(any())).thenReturn(Mono.just(true));

        final var refreshToken = new RefreshToken()
                .setExpiryDate(now.minusMinutes(1L));

        // WHEN
        final var mono = service.verifyExpiration(refreshToken);

        // THEN
        StepVerifier.create(mono)
                .expectErrorSatisfies(throwable -> {
                    assertThat(throwable)
                            .isInstanceOf(TokenRefreshException.class)
                            .hasMessage("400 BAD_REQUEST \"Refresh token was expired. Please make a new signin request\"");
                })
                .verify();
    }

    @Test
    void refreshToken_callFindByToken() {
        // GIVEN
        final var now = LocalDateTime.now();
        when(mockClock.instant()).thenReturn(now.toInstant(ZoneOffset.UTC));
        when(mockClock.getZone()).thenReturn(ZoneOffset.UTC.normalized());

        final var refreshToken = new RefreshToken().setExpiryDate(now.plusMinutes(1L));
        when(mockRefreshTokenRepository.findByToken(any())).thenReturn(Mono.just(refreshToken));

        final var client = new Client();
        when(mockClientRepository.findById(any())).thenReturn(Mono.just(client));
        when(mockRefreshTokenRepository.deleteByClientId(any())).thenReturn(Mono.just(true));
        final var newRefreshToken = new RefreshToken().setToken("new_refreshToken");
        when(mockRefreshTokenRepository.save(any())).thenReturn(Mono.just(newRefreshToken));

        // WHEN
        final var mono = service.refreshToken("refreshToken");

        // THEN
        StepVerifier.create(mono)
                .expectNextCount(1)
                .verifyComplete();
        verify(mockRefreshTokenRepository).findByToken("refreshToken");
    }

    @Test
    void refreshToken_callFindById() {
        // GIVEN
        final var now = LocalDateTime.now();
        when(mockClock.instant()).thenReturn(now.toInstant(ZoneOffset.UTC));
        when(mockClock.getZone()).thenReturn(ZoneOffset.UTC.normalized());

        final var refreshToken = new RefreshToken().setClientId("test-client").setExpiryDate(now.plusMinutes(1L));
        when(mockRefreshTokenRepository.findByToken(any())).thenReturn(Mono.just(refreshToken));

        final var client = new Client();
        when(mockClientRepository.findById(any())).thenReturn(Mono.just(client));
        when(mockRefreshTokenRepository.deleteByClientId(any())).thenReturn(Mono.just(true));
        final var newRefreshToken = new RefreshToken().setToken("new_refreshToken");
        when(mockRefreshTokenRepository.save(any())).thenReturn(Mono.just(newRefreshToken));

        // WHEN
        final var mono = service.refreshToken("refreshToken");

        // THEN
        StepVerifier.create(mono)
                .expectNextCount(1)
                .verifyComplete();
        verify(mockClientRepository).findById("test-client");
    }

    @Test
    void refreshToken_callGenerateAccessToken() {
        // GIVEN
        final var now = LocalDateTime.now();
        when(mockClock.instant()).thenReturn(now.toInstant(ZoneOffset.UTC));
        when(mockClock.getZone()).thenReturn(ZoneOffset.UTC.normalized());

        final var refreshToken = new RefreshToken().setClientId("test-client").setExpiryDate(now.plusMinutes(1L));
        when(mockRefreshTokenRepository.findByToken(any())).thenReturn(Mono.just(refreshToken));

        final var client = new Client();
        when(mockClientRepository.findById(any())).thenReturn(Mono.just(client));
        when(mockRefreshTokenRepository.deleteByClientId(any())).thenReturn(Mono.just(true));
        final var newRefreshToken = new RefreshToken().setToken("new_refreshToken");
        when(mockRefreshTokenRepository.save(any())).thenReturn(Mono.just(newRefreshToken));

        // WHEN
        final var mono = service.refreshToken("refreshToken");

        // THEN
        StepVerifier.create(mono)
                .expectNextCount(1)
                .verifyComplete();
        verify(mockJwtTokenService).generateAccessToken(client);
    }

    @Test
    void refreshToken_checkResult() {
        // GIVEN
        final var now = LocalDateTime.now();
        when(mockClock.instant()).thenReturn(now.toInstant(ZoneOffset.UTC));
        when(mockClock.getZone()).thenReturn(ZoneOffset.UTC.normalized());

        final var refreshToken = new RefreshToken().setClientId("test-client").setExpiryDate(now.plusMinutes(1L));
        when(mockRefreshTokenRepository.findByToken(any())).thenReturn(Mono.just(refreshToken));

        final var client = new Client();
        when(mockJwtTokenService.generateAccessToken(any())).thenReturn("accessToken");
        when(mockClientRepository.findById(any())).thenReturn(Mono.just(client));
        when(mockRefreshTokenRepository.deleteByClientId(any())).thenReturn(Mono.just(true));
        final var newRefreshToken = new RefreshToken().setToken("new_refreshToken");
        when(mockRefreshTokenRepository.save(any())).thenReturn(Mono.just(newRefreshToken));

        // WHEN
        final var mono = service.refreshToken("refreshToken");

        // THEN
        StepVerifier.create(mono)
                .assertNext(result -> {
                    assertThat(result.getAccessToken()).isEqualTo("accessToken");
                    assertThat(result.getRefreshToken()).isEqualTo("new_refreshToken");
                })
                .verifyComplete();
    }

    @Test
    void createRefreshToken_callGenerateRefreshToken() {
        // GIVEN
        final var now = LocalDateTime.now();
        when(mockClock.instant()).thenReturn(now.toInstant(ZoneOffset.UTC));
        when(mockClock.getZone()).thenReturn(ZoneOffset.UTC.normalized());
        when(mockRefreshTokenRepository.deleteByClientId(any())).thenReturn(Mono.just(true));
        final var newRefreshToken = new RefreshToken().setToken("new_refreshToken");
        when(mockRefreshTokenRepository.save(any())).thenReturn(Mono.just(newRefreshToken));

        final var client = new Client().setId("test-id");

        // WHEN
        final var mono = service.createRefreshToken(client);

        // THEN
        StepVerifier.create(mono)
                .expectNextCount(1)
                .verifyComplete();
        verify(mockJwtTokenService).generateRefreshToken(client);
    }

    @Test
    void createRefreshToken_callDeleteByClientId() {
        // GIVEN
        final var now = LocalDateTime.now();
        when(mockClock.instant()).thenReturn(now.toInstant(ZoneOffset.UTC));
        when(mockClock.getZone()).thenReturn(ZoneOffset.UTC.normalized());
        when(mockRefreshTokenRepository.deleteByClientId(any())).thenReturn(Mono.just(true));
        final var newRefreshToken = new RefreshToken().setToken("new_refreshToken");
        when(mockRefreshTokenRepository.save(any())).thenReturn(Mono.just(newRefreshToken));

        final var client = new Client().setId("test-id");

        // WHEN
        final var mono = service.createRefreshToken(client);

        // THEN
        StepVerifier.create(mono)
                .expectNextCount(1)
                .verifyComplete();
        verify(mockRefreshTokenRepository).deleteByClientId("test-id");
    }

    @Test
    void createRefreshToken_callSave() {
        // GIVEN
        final var now = LocalDateTime.now();
        when(mockClock.instant()).thenReturn(now.toInstant(ZoneOffset.UTC));
        when(mockClock.getZone()).thenReturn(ZoneOffset.UTC.normalized());
        when(mockJwtTokenService.getRefreshTokenExpirationMs()).thenReturn(1L);
        when(mockJwtTokenService.generateRefreshToken(any())).thenReturn("newRefreshToken");
        when(mockRefreshTokenRepository.deleteByClientId(any())).thenReturn(Mono.just(true));
        final var newRefreshToken = new RefreshToken().setToken("new_refreshToken");
        when(mockRefreshTokenRepository.save(any())).thenReturn(Mono.just(newRefreshToken));

        final var client = new Client().setId("test-id");

        // WHEN
        final var mono = service.createRefreshToken(client);

        // THEN
        final var expectedExpiryDate =
                LocalDateTime.ofInstant(
                        now.toInstant(ZoneOffset.UTC).plusMillis(1L),
                        ZoneOffset.UTC.normalized()
                );
        final var refreshToken = new RefreshToken()
                .setClientId(client.getId())
                .setExpiryDate(expectedExpiryDate)
                .setToken("newRefreshToken");
        StepVerifier.create(mono)
                .expectNextCount(1)
                .verifyComplete();
        verify(mockRefreshTokenRepository).save(refreshToken);
    }

}
