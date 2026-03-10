package com.sromip.notification.service;

import com.sromip.notification.entity.Notification;
import com.sromip.notification.model.NotificationChannel;
import com.sromip.notification.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repo;
    private final NotificationDispatcher dispatcher;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // ==========================================================
    // 🚨 FRAUD BLOCKED NOTIFICATION
    // ==========================================================
    public void dispatchFraudBlocked(String userEmail, double amount) {

        log.warn("⚠ Dispatching Fraud Blocked Notification");

        String message = "Your transaction of ₹" + amount +
                " was blocked due to risk detection.";

        dispatcher.dispatch(
                NotificationChannel.EMAIL,
                userEmail,
                message
        );

        Notification n = new Notification();
        n.setUserEmail(userEmail);
        n.setMessage(message);
        n.setChannel(NotificationChannel.EMAIL.name());
        n.setStatus("SENT");
        n.setCreatedAt(LocalDateTime.now());

        repo.save(n);

        Map<String, Object> event = new HashMap<>();
        event.put("userEmail", userEmail);
        event.put("status", "SENT");
        event.put("type", "FRAUD_BLOCKED");

        kafkaTemplate.send("notification-topic", event);

        log.info("📤 Notification event sent → notification-topic");
    }

    // ==========================================================
    // 💰 INVESTMENT SUCCESS NOTIFICATION
    // ==========================================================
    public void dispatchInvestmentSuccess(String userEmail, double amount) {

        log.info("🎉 Dispatching Investment Success Notification");

        String message = "🎉 Your investment of ₹" + amount + " was successful!";

        dispatcher.dispatch(
                NotificationChannel.EMAIL,
                userEmail,
                message
        );

        Notification n = new Notification();
        n.setUserEmail(userEmail);
        n.setMessage(message);
        n.setChannel(NotificationChannel.EMAIL.name());
        n.setStatus("SENT");
        n.setCreatedAt(LocalDateTime.now());

        repo.save(n);

        Map<String, Object> event = new HashMap<>();
        event.put("userEmail", userEmail);
        event.put("status", "SENT");
        event.put("type", "INVESTMENT_SUCCESS");

        kafkaTemplate.send("notification-topic", event);

        log.info("📤 Notification event sent → notification-topic");
    }

    // ==========================================================
    // 💳 PAYMENT SUCCESS NOTIFICATION
    // ==========================================================
    public void dispatchPaymentSuccess(String userEmail, double amount) {

        log.info("💳 Dispatching Payment Success Notification");

        String message = "Payment of ₹" + amount + " completed successfully.";

        dispatcher.dispatch(
                NotificationChannel.EMAIL,
                userEmail,
                message
        );

        Notification n = new Notification();
        n.setUserEmail(userEmail);
        n.setMessage(message);
        n.setChannel(NotificationChannel.EMAIL.name());
        n.setStatus("SENT");
        n.setCreatedAt(LocalDateTime.now());

        repo.save(n);

        Map<String, Object> event = new HashMap<>();
        event.put("userEmail", userEmail);
        event.put("status", "SENT");
        event.put("type", "PAYMENT_SUCCESS");

        kafkaTemplate.send("notification-topic", event);

        log.info("📤 Notification event sent → notification-topic");
    }
}