package com.sromip.paymentintent.event;

import com.sromip.common.event.PaymentIntentCreatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentIntentEventPublisher {

    private final KafkaTemplate<String, PaymentIntentCreatedEvent> kafkaTemplate;

    private static final String TOPIC = "payment-intent-created-topic";

    public void publish(
            String paymentId,
            String userEmail,
            double amount,
            String currency
    ) {

        String traceId = MDC.get("traceId");

        PaymentIntentCreatedEvent event = new PaymentIntentCreatedEvent(
                traceId,
                paymentId,
                userEmail,
                amount,
                currency,
                LocalDateTime.now()
        );

        log.info("📤 Publishing PaymentIntentCreatedEvent paymentId={}", paymentId);

        kafkaTemplate.send(TOPIC, paymentId, event);
    }
}