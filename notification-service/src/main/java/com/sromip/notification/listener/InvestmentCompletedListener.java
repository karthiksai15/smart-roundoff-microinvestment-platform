package com.sromip.notification.listener;

import com.sromip.common.event.InvestmentCompletedEvent;
import com.sromip.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvestmentCompletedListener {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "investment-completed-topic",
            groupId = "notification-service-group"
    )
    public void handleInvestmentCompleted(InvestmentCompletedEvent event) {

        MDC.put("traceId", event.getTraceId());

        try {

            log.info("Investment completed paymentId={}", event.getPaymentId());

            notificationService.dispatchInvestmentSuccess(
                    event.getPaymentId(),
                    event.getUserEmail(),
                    event.getInvestedAmount(),
                    event.getTraceId()
            );

        } finally {
            MDC.clear();
        }
    }
}