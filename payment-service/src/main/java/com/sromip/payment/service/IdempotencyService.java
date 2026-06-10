package com.sromip.payment.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class IdempotencyService {

    private final RedisTemplate<String, String> redisTemplate;

    public IdempotencyService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String key(String paymentId) {

        return "idempotency:payment:" + paymentId;
    }

    // ✅ CHECK ONLY
    public boolean isAlreadyProcessed(String paymentId) {
        return redisTemplate.hasKey(key(paymentId));
    }

    // ✅ MARK AFTER SUCCESS
    public void markProcessed(String paymentId) {
        redisTemplate.opsForValue()
                .set(key(paymentId), "done", Duration.ofHours(24));
    }
}