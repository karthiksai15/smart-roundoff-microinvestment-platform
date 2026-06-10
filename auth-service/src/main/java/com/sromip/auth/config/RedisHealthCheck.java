package com.sromip.auth.config;

import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisHealthCheck {

    private final RedisTemplate<String, String> redisTemplate; // ✅ FIX

    public RedisHealthCheck(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void testRedis() {
        redisTemplate.opsForValue().set("redis-test", "connected");
        System.out.println("✅ Redis status: " +
                redisTemplate.opsForValue().get("redis-test"));
    }
}