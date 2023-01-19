package com.goal.taxi.front.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@SuppressWarnings("NullableProblems")
@Slf4j
@Component
@Order(1)
public class LogErrorWebFilter implements WebFilter {
    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final WebFilterChain chain) {
        return chain.filter(exchange)
                .doOnError(exception -> log.error("RequestId: {}. Exception: {}", exchange.getRequest().getId(), exception.getMessage()));
    }
}
