package com.goal.taxi.client.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goal.taxi.client.config.AuthenticationProperties;
import com.goal.taxi.client.config.LoadProperties;
import com.goal.taxi.client.dto.LoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class TokenService {

    private final AuthenticationProperties authenticationProperties;
    private final String loginEntity;
    private final RequestConfig requestConfig;
    private final CloseableHttpClient client = HttpClients.custom()
            .setRetryHandler(new DefaultHttpRequestRetryHandler(3, true))
            .build();


    public TokenService(final LoadProperties loadProperties,
                        final AuthenticationProperties authenticationProperties) throws JsonProcessingException {
        final var loginRequest = new LoginRequest(authenticationProperties.getId(), authenticationProperties.getPassword());
        this.loginEntity = new ObjectMapper().writeValueAsString(loginRequest);
        this.authenticationProperties = authenticationProperties;
        this.requestConfig = RequestConfig.custom()
                .setConnectTimeout((int) loadProperties.getRequest().getTimeout().toMillis())
                .build();
    }

    public String getToken() {
        try {
            final var httpPost = new HttpPost(authenticationProperties.getUrl());
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            httpPost.setConfig(this.requestConfig);
            httpPost.setEntity(new StringEntity(loginEntity));

            try (final var response = client.execute(httpPost)) {
                if (response.getStatusLine().getStatusCode() != 200) {
                    log.error("Response: {}", response);
                    throw new RuntimeException("Failed to login");
                }

                String text = toString(response.getEntity().getContent());
                final var accessToken = new JSONObject(text).getString("accessToken");
                return "Bearer %s".formatted(accessToken);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
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
}
