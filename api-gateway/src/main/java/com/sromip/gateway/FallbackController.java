package com.sromip.gateway;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class FallbackController {

    @RequestMapping("/fallback/payment")
    public Mono<Map<String, Object>> paymentFallback() {
        return Mono.just(Map.of(
                "status", 503,
                "message", "Payment service is down",
                "fallback", true
        ));
    }

    @RequestMapping("/fallback/payment-intent")
    public Mono<Map<String, Object>> paymentIntentFallback() {
        return Mono.just(Map.of(
                "status", 503,
                "message", "Payment Intent service is down",
                "fallback", true
        ));
    }

    @RequestMapping("/fallback/investment")
    public Mono<Map<String, Object>> investmentFallback() {
        return Mono.just(Map.of(
                "status", 503,
                "message", "Investment service is down",
                "fallback", true
        ));
    }
}