package com.sromip.paymentintent.idempotency;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sromip.paymentintent.dto.PaymentIntentRequest;
import com.sromip.paymentintent.dto.PaymentIntentResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final IdempotencyRepository repository;
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate;

    private static final String PREFIX = "idem:";
    private static final String LOCK_PREFIX = "lock:";
    private static final Duration TTL = Duration.ofHours(24);

    // 🔥 STRONG HASH (FULL PAYLOAD)
    public String hashRequest(PaymentIntentRequest request) {
        try {
            String json = objectMapper.writeValueAsString(request);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(json.getBytes());

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();

        } catch (Exception e) {
            throw new RuntimeException("Hashing failed", e);
        }
    }

    // ✅ FAST CHECK (REDIS FIRST)
    public Optional<PaymentIntentResponse> checkCache(String key) {
        String cached = redisTemplate.opsForValue().get(PREFIX + key);
        if (cached != null) {
            try {
                return Optional.of(objectMapper.readValue(cached, PaymentIntentResponse.class));
            } catch (Exception e) {
                throw new RuntimeException("Cache parse error");
            }
        }
        return Optional.empty();
    }

    // ✅ DISTRIBUTED LOCK (PREVENT RACE)
    public boolean acquireLock(String key) {
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(LOCK_PREFIX + key, "LOCK", Duration.ofSeconds(10));
        return Boolean.TRUE.equals(success);
    }

    public void releaseLock(String key) {
        redisTemplate.delete(LOCK_PREFIX + key);
    }

    // ✅ DB CHECK (FALLBACK)
    public Optional<IdempotencyEntity> find(String key) {
        return repository.findByIdempotencyKey(key);
    }

    // ✅ CREATE PROCESSING
    public IdempotencyEntity createProcessing(String key, String requestHash) {

        IdempotencyEntity entity = IdempotencyEntity.builder()
                .idempotencyKey(key)
                .requestHash(requestHash)
                .status("PROCESSING")
                .createdAt(LocalDateTime.now())
                .build();

        return repository.saveAndFlush(entity);
    }

    // ✅ COMPLETE (CACHE + DB)
    public void complete(String key, PaymentIntentResponse response) {

        try {
            String json = objectMapper.writeValueAsString(response);

            // 🔥 Redis cache
            redisTemplate.opsForValue().set(PREFIX + key, json, TTL);

            // 🔥 DB update
            IdempotencyEntity entity = repository.findByIdempotencyKey(key)
                    .orElseThrow();

            entity.setStatus("COMPLETED");
            entity.setResponse(json);

            repository.saveAndFlush(entity);

        } catch (Exception e) {
            throw new RuntimeException("Error completing idempotency", e);
        }
    }

    public PaymentIntentResponse parseResponse(String json) {
        try {
            return objectMapper.readValue(json, PaymentIntentResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing response");
        }
    }

    public void markFailed(String key) {
        repository.findByIdempotencyKey(key).ifPresent(entity -> {
            entity.setStatus("FAILED");
            repository.saveAndFlush(entity);
        });
    }
}