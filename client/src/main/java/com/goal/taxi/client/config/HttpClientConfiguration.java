package com.goal.taxi.client.config;

import lombok.AllArgsConstructor;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class HttpClientConfiguration {

    private final LoadProperties loadProperties;

    @Bean
    CloseableHttpClient httpClient() {
        final var connManager = new PoolingHttpClientConnectionManager();
        connManager.setDefaultMaxPerRoute(loadProperties.getThreads());
        connManager.setMaxTotal(loadProperties.getThreads());

        return HttpClients.custom()
                .setConnectionManager(connManager)
                .setRetryHandler(new DefaultHttpRequestRetryHandler(3, true))
                .build();
    }

    @Bean
    RequestConfig requestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout((int) loadProperties.getRequest().getTimeout().toMillis())
                .build();
    }

}
