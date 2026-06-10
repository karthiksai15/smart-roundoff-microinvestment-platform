package com.sromip.paymentintent.event;

import com.sromip.common.event.FraudDecisionEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class FraudDecisionEventPublisher {

    // ✅ MUST MATCH bean name in KafkaProducerConfig
    private final KafkaTemplate<String, FraudDecisionEvent> fraudDecisionKafkaTemplate;

    private static final String TOPIC = "fraud-decision-topic";

    public void publish(
            String paymentId,
            String userEmail,
            double amount,
            double riskScore,
            boolean allow
    ) {

        String traceId = MDC.get("traceId");

        FraudDecisionEvent event = new FraudDecisionEvent(
                traceId,
                paymentId,
                userEmail,
                amount,
                riskScore,
                allow ? "APPROVED" : "BLOCKED",
                LocalDateTime.now()
        );

        log.info("📤 Publishing FraudDecisionEvent paymentId={}", paymentId);

        // ✅ Correct KafkaTemplate usage
        fraudDecisionKafkaTemplate.send(TOPIC, paymentId, event);
    }
}