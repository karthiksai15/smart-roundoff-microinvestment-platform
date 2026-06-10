package com.sromip.dashboard.listener;


import com.sromip.common.event.InvestmentCompletedEvent;
import com.sromip.dashboard.service.DashboardUpdateService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvestmentListener {

    private final DashboardUpdateService service;

    @KafkaListener(
            topics = "investment-completed-topic",
            groupId = "dashboard-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(InvestmentCompletedEvent event) {

        try {

            log.info("Investment event received paymentId={}", event.getPaymentId());

            service.updateInvestment(
                    event.getPaymentId(),
                    event.getUserEmail(),
                    event.getInvestedAmount()
            );

        } catch (Exception e) {

            log.error("Investment listener failed", e);

        }
    }
}