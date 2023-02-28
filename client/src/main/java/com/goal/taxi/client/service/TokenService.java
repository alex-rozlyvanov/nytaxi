package com.goal.taxi.client.service;

import com.goal.taxi.client.config.AuthenticationProperties;
import com.goal.taxi.client.exception.TokenServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    private static final Object lock = new Object();
    private static volatile String token;
    private static volatile LocalDateTime tokenRefreshTime;
    private final AuthenticationProperties authenticationProperties;
    private final String loginEntity;
    private final RequestConfig requestConfig;
    private final CloseableHttpClient httpClient;

    public String getToken() {
        if (tokenExpired()) {
            synchronized (lock) {
                if (tokenExpired()) {
                    token = login();
                    tokenRefreshTime = LocalDateTime.now();
                    return token;
                }
            }
        }

        return token;
    }

    private String login() {
        try {
            final var httpPost = new HttpPost(authenticationProperties.getUrl());
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            httpPost.setConfig(this.requestConfig);
            httpPost.setEntity(new StringEntity(loginEntity));

            return executeLogin(httpPost);

        } catch (IOException e) {
            throw new TokenServiceException(e);
        }
    }

    private String executeLogin(HttpPost httpPost) throws IOException {
        try (final var response = httpClient.execute(httpPost)) {
            if (response.getStatusLine().getStatusCode() != 200) {
                log.error("Response: {}", response);
                throw new TokenServiceException("Failed to login");
            }

            final var text = toString(response.getEntity().getContent());
            final var accessToken = new JSONObject(text).getString("accessToken");
            return "Bearer %s".formatted(accessToken);
        } catch (JSONException e) {
            throw new TokenServiceException(e);
        }
    }

    private String toString(final InputStream inputStream) throws IOException {
        final var bis = new BufferedInputStream(inputStream);
        final var buf = new ByteArrayOutputStream();

        for (int result = bis.read(); result != -1; result = bis.read()) {
            buf.write((byte) result);
        }

        return buf.toString(StandardCharsets.UTF_8);
    }

    private boolean tokenExpired() {
        return tokenRefreshTime == null || tokenRefreshTime.plus(authenticationProperties.getTokenExpiration())
                .isBefore(LocalDateTime.now());
    }
}
