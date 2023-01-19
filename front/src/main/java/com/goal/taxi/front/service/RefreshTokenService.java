package com.goal.taxi.front.service;

import com.goal.taxi.front.dao.entity.Client;
import com.goal.taxi.front.dao.entity.RefreshToken;
import com.goal.taxi.front.dao.repository.ClientRepository;
import com.goal.taxi.front.dao.repository.RefreshTokenRepository;
import com.goal.taxi.front.dto.TokenRefreshResponse;
import com.goal.taxi.front.exception.TokenRefreshException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final ClientRepository clientRepository;
    private final JwtTokenService jwtTokenService;
    private final Clock clock;

    @Transactional
    public Mono<Boolean> verifyExpiration(final RefreshToken token) {
        log.info("Verifying token expiration");
        if (token.getExpiryDate().isBefore(getNow())) {
            return refreshTokenRepository.delete(token)
                    .flatMap(r -> Mono.error(new TokenRefreshException("Refresh token was expired. Please make a new signin request")));
        }
        return Mono.just(true);
    }

    private LocalDateTime getNow() {
        return LocalDateTime.ofInstant(clock.instant(), clock.getZone());
    }

    @Transactional
    public Mono<TokenRefreshResponse> refreshToken(final String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken)
                .flatMap(this::refreshToken)
                .switchIfEmpty(Mono.error(() -> new TokenRefreshException("Refresh token is not valid!")));
    }

    private Mono<TokenRefreshResponse> refreshToken(final RefreshToken token) {
        return verifyExpiration(token)
                .flatMap(r -> clientRepository.findById(token.getClientId()))
                .flatMap(client -> {
                    final var accessToken = jwtTokenService.generateAccessToken(client);

                    return createRefreshToken(client)
                            .map(newRefreshToken -> TokenRefreshResponse.builder()
                                    .accessToken(accessToken)
                                    .refreshToken(newRefreshToken)
                                    .build());
                });
    }

    @Transactional
    public Mono<String> createRefreshToken(final Client client) {
        log.info("Creating refresh token for client '{}'", client.getId());
        final var expiryDate = LocalDateTime.ofInstant(
                clock.instant().plusMillis(jwtTokenService.getRefreshTokenExpirationMs()),
                clock.getZone()
        );
        final var refreshToken = new RefreshToken()
                .setClientId(client.getId())
                .setExpiryDate(expiryDate)
                .setToken(jwtTokenService.generateRefreshToken(client));

        return refreshTokenRepository.deleteByClientId(client.getId())
                .flatMap(r -> refreshTokenRepository.save(refreshToken))
                .map(RefreshToken::getToken);
    }

}
