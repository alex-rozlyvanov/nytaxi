package com.goal.taxi.front.dao.repository;

import com.goal.taxi.front.dao.entity.Client;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@AllArgsConstructor
public class ClientRepository {
    public static final String CLIENT = "Client";
    private final ReactiveRedisTemplate<String, Client> redisTemplate;

    public Mono<Client> findById(final String id) {
        return redisTemplate.opsForHash().get(CLIENT, id).map(r -> (Client) r);
    }

    public Mono<Client> save(final Client client) {
        return redisTemplate.opsForHash().put(CLIENT, client.getId(), client)
                .map(isSaved -> {
                    log.info("Client saved '{}'", client.getId());
                    return client;
                });
    }
}
