package com.goal.taxi.front.configuration;

import com.goal.taxi.front.exception.WebExchangeBindExceptionWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@SuppressWarnings("NullableProblems")
@Slf4j
@Component
@Order(2)
public class WebExchangeBindExceptionWebFilter implements WebFilter {
    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final WebFilterChain chain) {
        return chain.filter(exchange)
                .onErrorMap(WebExchangeBindException.class, WebExchangeBindExceptionWrapper::new);
    }
}
