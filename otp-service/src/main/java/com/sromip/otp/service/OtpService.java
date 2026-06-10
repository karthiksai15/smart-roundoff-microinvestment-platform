package com.sromip.otp.service;

import com.sromip.common.event.OtpVerifiedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String OTP_VERIFIED_TOPIC = "otp-verified-topic";

    private final SecureRandom random = new SecureRandom();

    // ================= GENERATE OTP =================
    public void generateOtp(String userEmail,
                            String paymentId,
                            String traceId,
                            String otpSessionId,
                            long expiry,
                            int maxAttempts) {

        String otp = String.valueOf(100000 + random.nextInt(900000));

        String key = "otp:" + otpSessionId;

        Map<String, Object> data = new HashMap<>();
        data.put("otp", otp);
        data.put("paymentId", paymentId);
        data.put("userEmail", userEmail);
        data.put("traceId", traceId);
        data.put("attempts", 0);
        data.put("maxAttempts", maxAttempts);

        long ttl = expiry - System.currentTimeMillis();
        if (ttl <= 0) ttl = 300000;

        redisTemplate.opsForValue().set(key, data, ttl, TimeUnit.MILLISECONDS);

        // 🔥 DEV LOG (IMPORTANT FOR TESTING)
        log.info("🔥 OTP GENERATED paymentId={} sessionId={} otp={}",
                paymentId, otpSessionId, otp);
    }

    // ================= VERIFY OTP =================
    public boolean verifyOtp(String otpSessionId,
                             String otp,
                             String traceId,
                             String userEmail) {

        String key = "otp:" + otpSessionId;

        Object raw = redisTemplate.opsForValue().get(key);

        if (!(raw instanceof Map)) {
            log.warn("OTP not found or invalid session sessionId={}", otpSessionId);
            return false;
        }

        Map<String, Object> data = (Map<String, Object>) raw;

        int attempts = (int) data.get("attempts");
        int maxAttempts = (int) data.get("maxAttempts");

        String storedOtp = (String) data.get("otp");
        String paymentId = (String) data.get("paymentId");
        String storedUser = (String) data.get("userEmail");

        // USER VALIDATION
        if (!storedUser.equals(userEmail)) {
            log.warn("❌ OTP user mismatch paymentId={}", paymentId);
            return false;
        }

        // ATTEMPT LIMIT
        if (attempts >= maxAttempts) {
            redisTemplate.delete(key);
            log.warn("❌ Max OTP attempts reached paymentId={}", paymentId);
            return false;
        }

        // SUCCESS
        if (storedOtp.equals(otp)) {

            redisTemplate.delete(key);

            kafkaTemplate.send(
                    OTP_VERIFIED_TOPIC,
                    paymentId,
                    new OtpVerifiedEvent(traceId, paymentId, otpSessionId, userEmail)
            );

            log.info("✅ OTP VERIFIED paymentId={}", paymentId);
            return true;
        }

        // FAILURE
        data.put("attempts", attempts + 1);
        redisTemplate.opsForValue().set(key, data, 5, TimeUnit.MINUTES);

        log.warn("❌ Invalid OTP attempt paymentId={} attempt={}",
                paymentId, attempts + 1);

        return false;
    }

    // ================= RESEND =================
    public void resendOtp(String otpSessionId,
                          String userEmail,
                          String traceId) {

        String key = "otp:" + otpSessionId;

        Object raw = redisTemplate.opsForValue().get(key);

        if (!(raw instanceof Map)) {
            log.warn("Cannot resend OTP — session not found sessionId={}", otpSessionId);
            return;
        }

        Map<String, Object> data = (Map<String, Object>) raw;

        String paymentId = (String) data.get("paymentId");
        int maxAttempts = (int) data.get("maxAttempts");

        long expiry = System.currentTimeMillis() + (5 * 60 * 1000);

        generateOtp(userEmail, paymentId, traceId, otpSessionId, expiry, maxAttempts);

        log.info("🔁 OTP resent paymentId={} sessionId={}", paymentId, otpSessionId);
    }
}