package com.sromip.notification.listener;

import com.sromip.common.event.FraudDecisionEvent;
import com.sromip.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FraudBlockedListener {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "trust-decision-topic",
            groupId = "notification-service-group"
    )
    public void handleFraudDecision(FraudDecisionEvent event) {

        log.info("Fraud decision received for user {}", event.getUserEmail());

        // Only send notification if fraud blocked the transaction
        if (!"BLOCKED".equalsIgnoreCase(event.getStatus())) {
            return;
        }

        log.warn("🚨 FRAUD BLOCKED EVENT RECEIVED");
        log.warn("User: {}", event.getUserEmail());
        log.warn("Amount: {}", event.getAmount());

        notificationService.dispatchFraudBlocked(
                event.getUserEmail(),
                event.getAmount()
        );
    }
}