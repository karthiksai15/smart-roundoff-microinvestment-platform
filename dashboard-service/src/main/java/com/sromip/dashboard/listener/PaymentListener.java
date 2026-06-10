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

    @KafkaListener(
            topics = "payment-topic",
            groupId = "dashboard-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handlePayment(PaymentEvent event) {

        try {

            String paymentId = event.getPaymentId();
            String email = event.getUserEmail();

            if (event.getEventType() == PaymentEventType.PAYMENT_COMPLETED) {

                service.updatePayment(paymentId, email, "COMPLETED", "APPROVED");

            } else if (event.getEventType() == PaymentEventType.PAYMENT_FAILED) {

                service.updatePayment(paymentId, email, "FAILED", "BLOCKED");

            }

        } catch (Exception e){
            log.error("Payment listener failed", e);
        }
    }
}