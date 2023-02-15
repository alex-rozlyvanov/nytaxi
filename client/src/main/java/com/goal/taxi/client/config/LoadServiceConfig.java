package com.goal.taxi.client.config;

import lombok.AllArgsConstructor;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@AllArgsConstructor
public class LoadServiceConfig {

    private final LoadProperties loadProperties;

    @Bean
    ThreadPoolExecutor threadPoolExecutor() {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(loadProperties.getThreads());
    }

    @Bean
    RequestConfig requestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout((int) loadProperties.getRequest().getTimeout().toMillis())
                .build();
    }

    @Bean
    CloseableHttpClient httpClient() {
        return HttpClients.custom()
                .setRetryHandler(new DefaultHttpRequestRetryHandler(3, true))
                .build();
    }

}
