package com.sromip.dashboard.listener;


import com.sromip.dashboard.service.DashboardUpdateService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final DashboardUpdateService service;

    @KafkaListener(
            topics = "notification-topic",
            groupId = "dashboard-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(Map<String, Object> event) {

        try {

            String paymentId = event.get("paymentId").toString();
            String email = event.get("userEmail").toString();

            service.updateNotification(paymentId, email);

        } catch (Exception e) {

            log.error("Notification listener failed", e);

        }
    }
}