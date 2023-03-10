package com.goal.taxi.client.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Slf4j
@Component
@AllArgsConstructor
public class RequestExecutor {
    private final CloseableHttpClient httpClient;

    public void executeRequest(final String taxiTripJson, final HttpRequestBase httpRequestBase) {
        try (final var response = httpClient.execute(httpRequestBase)) {
            if (response.getStatusLine().getStatusCode() >= 300) {
                StatisticCollector.errorsCounter.incrementAndGet();
                log.error("Error {}, {}", new String(response.getEntity().getContent().readAllBytes()), taxiTripJson);
                return;
            }
            StatisticCollector.sentRecordsCounter.incrementAndGet();
            logGetResponse(httpRequestBase, response);
        } catch (Exception e) {
            if (!(e instanceof org.apache.http.impl.execchain.RequestAbortedException)) {
                log.error("Exception: {} ", taxiTripJson, e);
                StatisticCollector.errorsCounter.incrementAndGet();
            }
        }
    }

    private void logGetResponse(final HttpRequestBase httpRequestBase, final CloseableHttpResponse closeableHttpResponse) throws IOException {
        if ("GET".equals(httpRequestBase.getMethod()) && closeableHttpResponse.getEntity() != null) {
            final var content = closeableHttpResponse.getEntity().getContent();
            final var responseString = new String(content.readAllBytes());

            if (StringUtils.hasText(responseString)) {
                log.info("Get Response: '{}'", responseString);
            }
        }
    }
}
