package com.goal.taxi.front.dao.repository;

import com.goal.taxi.front.dao.entity.RefreshToken;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@AllArgsConstructor
public class RefreshTokenRepository {
    public static final String REFRESH_TOKEN = "RefreshToken";
    private final ReactiveRedisTemplate<String, RefreshToken> redisTemplate;

    public Mono<Boolean> delete(final RefreshToken token) {
        return redisTemplate.opsForHash().delete(token.getClientId());
    }

    public Mono<RefreshToken> findByToken(final String refreshToken) {
        final var hashOperations = redisTemplate.<String, RefreshToken>opsForHash();
        return hashOperations.scan(REFRESH_TOKEN)
                .filter(entry -> entry.getValue().getToken().equals(refreshToken))
                .map(Map.Entry::getValue)
                .singleOrEmpty();
    }

    public Mono<Boolean> deleteByClientId(final String clientId) {
        return redisTemplate.opsForHash().delete(clientId);
    }

    public Mono<RefreshToken> save(final RefreshToken refreshToken) {
        return redisTemplate.opsForHash().put(REFRESH_TOKEN, refreshToken.getClientId(), refreshToken)
                .map(isSaved -> refreshToken);
    }
}
