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

    // ✅ PAYMENT SUCCESS
    public void dispatchPaymentSuccess(String paymentId, String email, double amount, String traceId) {

        String message = "Payment of ₹" + amount + " completed.";

        process(paymentId, email, message, traceId, "PAYMENT_SUCCESS");
    }

    // ✅ PAYMENT FAILED (FRAUD BLOCK)
    public void dispatchPaymentFailed(String paymentId, String email, double amount, String traceId) {

        String message = "⚠️ Your payment of ₹" + amount + " was blocked.";

        process(paymentId, email, message, traceId, "PAYMENT_FAILED");
    }

    // ✅ INVESTMENT SUCCESS
    public void dispatchInvestmentSuccess(String paymentId, String email, double amount, String traceId) {

        String message = "🎉 Investment of ₹" + amount + " successful!";

        process(paymentId, email, message, traceId, "INVESTMENT_SUCCESS");
    }

    // 🔥 COMMON PIPELINE
    private void process(String paymentId,
                         String email,
                         String message,
                         String traceId,
                         String type) {

        if (paymentId == null || email == null) {
            log.warn("Invalid notification event");
            return;
        }

        // ✅ IDEMPOTENCY
        if (repo.findByPaymentIdAndMessage(paymentId, message).isPresent()) {
            log.warn("Duplicate notification skipped paymentId={}", paymentId);
            return;
        }

        String status;

        try {
            dispatcher.dispatch(NotificationChannel.EMAIL, email, message);
            status = "SENT";
        } catch (Exception e) {
            log.error("Notification failed paymentId={}", paymentId, e);
            status = "FAILED";
        }

        saveNotification(paymentId, email, message, traceId, status);

        sendEvent(paymentId, email, type, traceId, status);
    }

    private void saveNotification(String paymentId,
                                  String email,
                                  String message,
                                  String traceId,
                                  String status) {

        Notification n = new Notification();

        n.setPaymentId(paymentId);
        n.setUserEmail(email);
        n.setMessage(message);
        n.setChannel(NotificationChannel.EMAIL.name());
        n.setStatus(status);
        n.setTraceId(traceId);
        n.setCreatedAt(LocalDateTime.now());

        repo.save(n);
    }

    private void sendEvent(String paymentId,
                           String email,
                           String type,
                           String traceId,
                           String status) {

        Map<String, Object> event = new HashMap<>();

        event.put("traceId", traceId);
        event.put("paymentId", paymentId);
        event.put("userEmail", email);
        event.put("type", type);
        event.put("status", status);

        kafkaTemplate.send("notification-topic", paymentId, event);

        log.info("Notification event sent paymentId={}", paymentId);
    }
}