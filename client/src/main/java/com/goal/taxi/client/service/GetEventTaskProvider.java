package com.goal.taxi.client.service;

import com.goal.taxi.client.config.LoadProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetEventTaskProvider {
    private static final SecureRandom SECURE_RANDOM;

    static {
        try {
            SECURE_RANDOM = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private final LoadProperties loadProperties;
    private final TokenService tokenService;
    private final RequestConfig requestConfig;
    private final RequestExecutor requestExecutor;

    public Runnable getTask(final String taxiTripJson) {
        return () -> {
            try {
                final var randomMonth = getRandomMonth();
                final var randomDay = getRandomDay(randomMonth);
                final var getUrl = loadProperties.getGetUrl() + "&month=%s&day=%s".formatted(
                        randomMonth,
                        randomDay
                );
                final var httpGet = new HttpGet(getUrl);
                httpGet.setHeader(HttpHeaders.ACCEPT, "application/json");
                httpGet.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
                httpGet.setHeader(HttpHeaders.AUTHORIZATION, tokenService.getToken());
                httpGet.setConfig(this.requestConfig);

                requestExecutor.executeRequest(taxiTripJson, httpGet);
            } catch (Exception e) {
                StatisticCollector.errorsCounter.incrementAndGet();
                log.error("Error: {} ", taxiTripJson, e);
            }
        };
    }

    private int getRandomDay(int randomMonth) {
        final var bound = LocalDate.of(2018, randomMonth, 1).lengthOfMonth();
        return SECURE_RANDOM.nextInt(1, bound + 1);
    }

    private int getRandomMonth() {
        return SECURE_RANDOM.nextInt(1, 6);
    }

}
