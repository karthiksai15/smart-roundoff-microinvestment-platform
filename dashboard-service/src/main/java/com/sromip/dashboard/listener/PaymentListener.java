package com.sromip.dashboard.listener;

import com.sromip.common.event.PaymentEvent;
import com.sromip.common.event.PaymentEventType;
import com.sromip.dashboard.service.DashboardUpdateService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentListener {

    private final DashboardUpdateService service;

    @KafkaListener(topics = "payment-topic", groupId = "dashboard-group")
    public void handlePayment(PaymentEvent event) {

        try {

            if (event.getEventType() != PaymentEventType.PAYMENT_COMPLETED) {
                return;
            }

            Long paymentId = Long.valueOf(event.getTransactionId());
            String email = event.getUserEmail();

            service.updatePayment(paymentId, email);

            log.info("Payment updated for {}", paymentId);

        } catch (Exception e){

            log.error("Payment listener failed", e);

        }
    }
}