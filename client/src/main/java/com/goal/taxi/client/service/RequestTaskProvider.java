package com.goal.taxi.client.service;

import com.goal.taxi.client.config.AuthenticationProperties;
import com.goal.taxi.client.config.LoadProperties;
import com.goal.taxi.common.kafka.KafkaCustomHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestTaskProvider {
    private static final Object lock = new Object();
    private static volatile String token;
    private static volatile LocalDateTime tokenRefreshTime;
    private final LoadProperties loadProperties;
    private final AuthenticationProperties authenticationProperties;
    private final TokenService tokenService;
    private final CloseableHttpClient client;
    private final RequestConfig requestConfig;

    public Runnable getSendTask(final String taxiTripJson) {
        return () -> {
            try {
                final var httpPost = new HttpPost(loadProperties.getUrl());
                httpPost.setHeader(HttpHeaders.ACCEPT, "application/json");
                httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
                httpPost.setHeader(HttpHeaders.AUTHORIZATION, tokenSupplier());
                httpPost.setHeader(KafkaCustomHeaders.IDEMPOTENCY_KEY, UUID.randomUUID().toString());
                httpPost.setEntity(new StringEntity(taxiTripJson));
                httpPost.setConfig(this.requestConfig);

                try (final var response = client.execute(httpPost)) {
                    if (response.getStatusLine().getStatusCode() >= 300) {
                        StatisticCollector.errorsCounter.incrementAndGet();
                        log.error("Error {}, {}", new String(response.getEntity().getContent().readAllBytes()), taxiTripJson);
                        return;
                    }
                }
                StatisticCollector.sentRecordsCounter.incrementAndGet();
            } catch (IOException e) {
                StatisticCollector.errorsCounter.incrementAndGet();
                log.error("Error: {} ", taxiTripJson, e);
                throw new RuntimeException(e);
            }
        };
    }

    private String tokenSupplier() {
        if (tokenExpired()) {
            synchronized (lock) {
                if (tokenExpired()) {
                    token = tokenService.getToken();
                    tokenRefreshTime = LocalDateTime.now();
                    return token;
                }
            }
        }

        return token;
    }

    private boolean tokenExpired() {
        return tokenRefreshTime == null || tokenRefreshTime.plus(authenticationProperties.getTokenExpiration())
                .isBefore(LocalDateTime.now());
    }
}
