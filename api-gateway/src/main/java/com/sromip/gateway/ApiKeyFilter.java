package com.sromip.gateway;

import org.springframework.cloud.gateway.filter.*;
import org.springframework.core.Ordered;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.beans.factory.annotation.Value;

@Component
public class ApiKeyFilter implements GlobalFilter, Ordered {

    @Value("${gateway.api-key}")
    private String validKey;

    private static final String API_KEY = "X-API-KEY";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        if (!path.startsWith("/partner")) {
            return chain.filter(exchange);
        }

        String key = exchange.getRequest().getHeaders().getFirst(API_KEY);

        if (!validKey.equals(key)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -4;
    }
}