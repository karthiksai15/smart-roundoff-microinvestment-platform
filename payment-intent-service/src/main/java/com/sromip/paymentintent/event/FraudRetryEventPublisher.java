package com.sromip.paymentintent.event;

import com.sromip.common.event.FraudCheckRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FraudRetryEventPublisher {

    private final KafkaTemplate<String, FraudCheckRequest> kafkaTemplate;

    private static final String TOPIC = "fraud-check-retry-topic";

    public void publish(FraudCheckRequest request) {

        log.warn("📤 Publishing Fraud RETRY event paymentId={}", request.getPaymentId());

        kafkaTemplate.send(TOPIC, request.getPaymentId(), request);
    }
}