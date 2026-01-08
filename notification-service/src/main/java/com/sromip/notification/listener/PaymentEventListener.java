package com.sromip.notification.listener;

import com.sromip.common.event.PaymentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentEventListener {

    @KafkaListener(
            topics = "payment-events",
            groupId = "notification-service-group"
    )
    public void handlePaymentEvent(PaymentEvent event) {

        log.info("📩 PAYMENT EVENT RECEIVED");
        log.info("User Email: {}", event.getUserEmail());
        log.info("Amount: {}", event.getAmount());
        log.info("Spare Amount: {}", event.getSpareAmount());
        log.info("Event Type: {}", event.getEventType());
    }
}
