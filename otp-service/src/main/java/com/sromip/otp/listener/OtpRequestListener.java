package com.sromip.otp.listener;

import com.sromip.common.event.OtpRequestEvent;
import com.sromip.otp.service.OtpService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OtpRequestListener {

    private final OtpService otpService;

    @KafkaListener(
            topics = "otp-request-topic",
            groupId = "otp-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(@Payload OtpRequestEvent event,
                        ConsumerRecord<String, Object> record) {

        MDC.put("traceId", event.getTraceId());

        try {

            log.info("🔥 OTP EVENT RECEIVED → paymentId={} sessionId={}",
                    event.getPaymentId(),
                    event.getOtpSessionId());

            otpService.generateOtp(
                    event.getUserEmail(),
                    event.getPaymentId(),
                    event.getTraceId(),
                    event.getOtpSessionId(),
                    event.getExpiryTime(),
                    event.getMaxAttempts()
            );

        } catch (Exception e) {
            log.error("❌ Error processing OTP request paymentId={}", event.getPaymentId(), e);
            throw e;
        } finally {
            MDC.clear();
        }
    }
}