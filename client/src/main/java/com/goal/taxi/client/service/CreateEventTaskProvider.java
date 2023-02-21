package com.goal.taxi.client.service;

import com.goal.taxi.client.config.LoadProperties;
import com.goal.taxi.common.kafka.KafkaCustomHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateEventTaskProvider {
    private final LoadProperties loadProperties;
    private final TokenService tokenService;
    private final RequestConfig requestConfig;
    private final RequestExecutor requestExecutor;

    public Runnable getTask(final String taxiTripJson) {
        return () -> {
            try {
                final var httpPost = new HttpPost(loadProperties.getCreateUrl());
                httpPost.setHeader(HttpHeaders.ACCEPT, "application/json");
                httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
                httpPost.setHeader(HttpHeaders.AUTHORIZATION, tokenService.getToken());
                httpPost.setHeader(KafkaCustomHeaders.IDEMPOTENCY_KEY, UUID.randomUUID().toString());
                httpPost.setEntity(new StringEntity(taxiTripJson));
                httpPost.setConfig(this.requestConfig);

                requestExecutor.executeRequest(taxiTripJson, httpPost);
            } catch (IOException e) {
                StatisticCollector.errorsCounter.incrementAndGet();
                log.error("Error: {} ", taxiTripJson, e);
            }
        };
    }


}
