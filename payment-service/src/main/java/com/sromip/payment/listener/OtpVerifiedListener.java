package com.sromip.payment.listener;

import com.sromip.common.event.OtpVerifiedEvent;
import com.sromip.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OtpVerifiedListener {

    private final PaymentService paymentService;

    @KafkaListener(
            topics = "otp-verified-topic",
            groupId = "payment-service-group"
    )
    public void handleOtpVerified(OtpVerifiedEvent event) {

        log.info("🔥 OTP VERIFIED RECEIVED paymentId={}", event.getPaymentId());

        paymentService.resumeAfterOtp(
                event.getPaymentId(),
                event.getTraceId()
        );
    }
}