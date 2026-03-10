package com.sromip.otp.service;

import com.sromip.common.event.OtpGeneratedEvent;
import com.sromip.common.event.OtpVerifiedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final RedisTemplate<String, String> redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String OTP_TOPIC = "otp-generated-topic";
    private static final String OTP_VERIFIED_TOPIC = "otp-verified-topic";

    // 🔹 Generate OTP
    public void generateOtp(String userEmail) {

        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        redisTemplate.opsForValue().set(userEmail, otp, 5, TimeUnit.MINUTES);

        kafkaTemplate.send(OTP_TOPIC, new OtpGeneratedEvent(userEmail, otp));

        log.info("🔐 OTP generated for {} : {}", userEmail, otp);
    }

    // 🔹 Verify OTP
    public boolean verifyOtp(String userEmail, String otp) {

        String storedOtp = redisTemplate.opsForValue().get(userEmail);

        if (storedOtp != null && storedOtp.equals(otp)) {

            redisTemplate.delete(userEmail);

            kafkaTemplate.send(OTP_VERIFIED_TOPIC, new OtpVerifiedEvent(userEmail));

            log.info("✅ OTP verified for {}", userEmail);

            return true;
        }

        log.warn("❌ Invalid OTP for {}", userEmail);
        return false;
    }
}