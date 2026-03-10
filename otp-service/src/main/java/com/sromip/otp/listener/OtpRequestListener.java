package com.sromip.otp.listener;

import com.sromip.common.event.OtpRequestEvent;
import com.sromip.otp.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OtpRequestListener {

    private final OtpService otpService;

    @KafkaListener(topics = "otp-request-topic", groupId = "otp-service-group")
    public void consume(OtpRequestEvent event) {

        log.info("📩 OTP request received from Payment Service");
        log.info("User: {}", event.getUserEmail());

        otpService.generateOtp(event.getUserEmail());
    }
}