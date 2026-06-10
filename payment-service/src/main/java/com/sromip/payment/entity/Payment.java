package com.sromip.payment.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_id", unique = true, nullable = false)
    private String paymentId;

    private String userEmail;

    private double originalAmount;
    private double roundedAmount;
    private double spareAmount;

    private double riskScore;
    private String riskLevel;

    private boolean otpRequired;
    private boolean otpVerified;

    private String otpSessionId;
    private int otpAttempts;
    private long otpExpiryTime;

    private String roundingStrategy;

    // ✅ FIX: ENUM STATUS
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
}