package com.sromip.notification.listener;

import com.sromip.common.event.NotificationEventType;

import com.sromip.notification.service.NotificationService;
import com.sromip.common.event.InvestmentCompletedEvent;
import com.sromip.common.event.NotificationEvent;
import com.sromip.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvestmentCompletedListener {

    private final NotificationService notificationService;
    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    private static final String NOTIFICATION_TOPIC = "notification-topic";

    @KafkaListener(
            topics = "investment-completed-topic",
            groupId = "notification-service-group"
    )
    public void handleInvestmentCompleted(InvestmentCompletedEvent event) {

        try {

            log.info("💰 INVESTMENT COMPLETED EVENT RECEIVED");

            notificationService.dispatchInvestmentSuccess(
                    event.getUserEmail(),
                    event.getInvestedAmount()
            );

            NotificationEvent notificationEvent =
                    new NotificationEvent(
                            NotificationEventType.INVESTMENT_SUCCESS,
                            event.getPaymentId(),
                            event.getUserEmail(),
                            null,
                            "Investment completed successfully",
                            event.getInvestedAmount()
                    );

            kafkaTemplate.send("notification-topic", notificationEvent);

            log.info("Notification event published for payment {}", event.getPaymentId());

        } catch (Exception e) {

            log.error("Notification processing failed", e);

        }
    }
}