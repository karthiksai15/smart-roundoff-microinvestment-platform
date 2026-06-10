package com.sromip.payment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "dlq_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DLQEvent {

    @Id
    @GeneratedValue
    private UUID id;

    private String paymentId;

    private String reason;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private DLQStatus status;

    private int retryCount;

    // 🔥 NEW FIELD (VERY IMPORTANT)
    @Column(nullable = false)
    private LocalDateTime nextRetryAt;
}