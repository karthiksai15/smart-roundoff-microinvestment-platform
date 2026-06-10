package com.sromip.notification.listener;

import com.sromip.common.event.PaymentEvent;
import com.sromip.common.event.PaymentEventType;
import com.sromip.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
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
    public void handlePayment(PaymentEvent event) {

        MDC.put("traceId", event.getTraceId());

        try {

            String paymentId = event.getPaymentId();

            if (event.getEventType() == PaymentEventType.PAYMENT_COMPLETED) {

                notificationService.dispatchPaymentSuccess(
                        paymentId,
                        event.getUserEmail(),
                        event.getAmount(),
                        event.getTraceId()
                );
            }

            // ✅ NOW THIS WILL WORK
            if (event.getEventType() == PaymentEventType.PAYMENT_FAILED) {

                notificationService.dispatchPaymentFailed(
                        paymentId,
                        event.getUserEmail(),
                        event.getAmount(),
                        event.getTraceId()
                );
            }

        } finally {
            MDC.clear();
        }
    }
}