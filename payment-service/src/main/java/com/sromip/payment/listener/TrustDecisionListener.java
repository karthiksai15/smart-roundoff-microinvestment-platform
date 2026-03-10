package com.sromip.payment.listener;

import com.sromip.common.event.FraudDecisionEvent;
import com.sromip.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TrustDecisionListener {

    private final PaymentService paymentService;

    @KafkaListener(topics = "trust-decision-topic", groupId = "payment-service-group")
    public void listen(FraudDecisionEvent event) {

        log.info("📥 Trust decision received from Fraud Service");

        paymentService.processDecision(event);
    }
}