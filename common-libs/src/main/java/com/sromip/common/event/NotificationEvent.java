package com.sromip.common.event;

import java.time.LocalDateTime;

public class NotificationEvent {

    private String traceId; // ✅ ADDED

    private NotificationEventType eventType;

    private String paymentId;

    private String userEmail;
    private String userId;

    private String message;
    private Double amount;

    private LocalDateTime createdAt;

    public NotificationEvent() {
        this.createdAt = LocalDateTime.now();
    }

    public NotificationEvent(
            String traceId,
            NotificationEventType eventType,
            String paymentId,
            String userEmail,
            String userId,
            String message,
            Double amount
    ) {
        this.traceId = traceId;
        this.eventType = eventType;
        this.paymentId = paymentId;
        this.userEmail = userEmail;
        this.userId = userId;
        this.message = message;
        this.amount = amount;
        this.createdAt = LocalDateTime.now();
    }

    // getters & setters
}