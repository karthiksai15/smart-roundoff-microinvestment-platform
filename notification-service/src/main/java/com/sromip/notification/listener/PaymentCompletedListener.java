package com.sromip.notification.listener;

import com.sromip.common.event.PaymentEvent;
import com.sromip.common.event.PaymentEventType;
import com.sromip.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentCompletedListener {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "payment-topic",
            groupId = "notification-service-group"
    )
    public void handlePaymentCompleted(PaymentEvent event) {

        if (event.getEventType() != PaymentEventType.PAYMENT_COMPLETED) {
            return;
        }

        log.info("💳 PAYMENT COMPLETED EVENT RECEIVED");

        notificationService.dispatchPaymentSuccess(
                event.getUserEmail(),
                event.getAmount()
        );
    }
}