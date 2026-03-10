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

    @KafkaListener(topics = "investment-completed-topic", groupId = "dashboard-group")
    public void consume(InvestmentCompletedEvent event) {

        try {

            Long paymentId = event.getPaymentId();
            String email = event.getUserEmail();
            Double amount = event.getInvestedAmount();

            service.updateInvestment(paymentId, email, amount);

            log.info("Investment updated for paymentId {}", paymentId);

        } catch (Exception e) {

            log.error("Investment listener failed", e);

        }
    }
}