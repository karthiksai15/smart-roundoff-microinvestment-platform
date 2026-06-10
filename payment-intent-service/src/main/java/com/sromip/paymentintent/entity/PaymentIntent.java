package com.sromip.paymentintent.entity;

import com.sromip.paymentintent.enums.PaymentIntentStatus;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment_intents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentIntent {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "payment_id", unique = true, nullable = false)
    private String paymentId;

    private String userEmail;
    private double requestedAmount;
    private String currency;

    // 🔥 STATE MACHINE FIELD
    @Enumerated(EnumType.STRING)
    private PaymentIntentStatus status;

    private LocalDateTime createdAt;

    private Double trustScore;
    private String riskLevel;
    private String recommendedAction;

    private Double roundoffPreview;
    private Double spareAmountPreview;
}