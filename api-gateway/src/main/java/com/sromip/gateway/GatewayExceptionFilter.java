package com.sromip.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.*;
import org.springframework.core.Ordered;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

@Component
@Slf4j
public class GatewayExceptionFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        return chain.filter(exchange)
                .onErrorResume(ex -> {

                    String traceId = UUID.randomUUID().toString();
                    String correlationId = exchange.getRequest().getHeaders().getFirst("X-Correlation-ID");

                    log.error("🔥 Gateway Error [{}]: {}", correlationId, ex.getMessage());

                    exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                    exchange.getResponse().getHeaders().add("Content-Type", "application/json");

                    String body = String.format(
                            "{\"timestamp\":\"%s\",\"status\":500,\"message\":\"%s\",\"traceId\":\"%s\"}",
                            Instant.now(),
                            ex.getMessage(),
                            traceId
                    );

                    return exchange.getResponse().writeWith(
                            Mono.just(exchange.getResponse()
                                    .bufferFactory()
                                    .wrap(body.getBytes(StandardCharsets.UTF_8)))
                    );
                });
    }

    @Override
    public int getOrder() {
        return -5; // ✅ FIXED (highest priority)
    }
}