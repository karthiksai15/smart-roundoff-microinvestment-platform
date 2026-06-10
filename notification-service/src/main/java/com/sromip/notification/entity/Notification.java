package com.sromip.notification.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ UNIQUE FOR IDEMPOTENCY
    @Column(name = "payment_id", nullable = false)
    private String paymentId;

    private String userEmail;

    private String message;

    private String channel;   // EMAIL / SMS / PUSH
    private String status;    // SENT / FAILED

    // ✅ TRACEABILITY
    private String traceId;

    private LocalDateTime createdAt = LocalDateTime.now();
}