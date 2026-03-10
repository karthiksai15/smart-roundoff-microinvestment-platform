package com.sromip.paymentintent.entity;

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

    private String userEmail;
    private double requestedAmount;
    private String currency;
    private String status;
    private LocalDateTime createdAt;

    private Double trustScore;
    private String riskLevel;
    private String recommendedAction;

    private Double roundoffPreview;
    private Double spareAmountPreview;
}