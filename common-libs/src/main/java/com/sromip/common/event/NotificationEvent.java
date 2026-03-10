package com.sromip.common.event;

import java.time.LocalDateTime;

public class NotificationEvent {

    private NotificationEventType eventType;

    private Long paymentId;     // ⭐ ADD THIS

    private String userEmail;
    private String userId;

    private String message;
    private Double amount;

    private LocalDateTime createdAt;

    public NotificationEvent() {
        this.createdAt = LocalDateTime.now();
    }

    public NotificationEvent(
            NotificationEventType eventType,
            Long paymentId,        // ⭐ ADD THIS
            String userEmail,
            String userId,
            String message,
            Double amount
    ) {
        this.eventType = eventType;
        this.paymentId = paymentId;   // ⭐ ADD THIS
        this.userEmail = userEmail;
        this.userId = userId;
        this.message = message;
        this.amount = amount;
        this.createdAt = LocalDateTime.now();
    }

    // Getters & Setters

    public NotificationEventType getEventType() {
        return eventType;
    }

    public void setEventType(NotificationEventType eventType) {
        this.eventType = eventType;
    }

    public Long getPaymentId() {        // ⭐ ADD
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {   // ⭐ ADD
        this.paymentId = paymentId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}