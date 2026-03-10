package com.sromip.dashboard.listener;

import com.sromip.common.event.FraudDecisionEvent;
import com.sromip.dashboard.service.DashboardUpdateService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FraudDecisionListener {

    private final DashboardUpdateService service;

    @KafkaListener(topics = "trust-decision-topic", groupId = "dashboard-group")
    public void consume(FraudDecisionEvent event) {

        try {

            Long paymentId = event.getPaymentId();
            String email = event.getUserEmail();

            service.updateFraud(
                    paymentId,
                    email,
                    event.getStatus(),
                    event.getRiskScore()
            );

            log.info("Fraud updated for {}", paymentId);

        } catch (Exception e) {

            log.error("Fraud listener failed", e);

        }
    }
}