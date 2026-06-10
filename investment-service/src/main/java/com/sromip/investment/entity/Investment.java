package com.sromip.investment.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "investments")
public class Investment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ FIX: UNIQUE + REQUIRED
    @Column(name = "payment_id", unique = true, nullable = false)
    private String paymentId;

    @Column(name = "user_email")
    private String userEmail;

    private double amount;

    // ✅ TRACEABILITY (IMPORTANT)
    private String traceId;

    private LocalDateTime date = LocalDateTime.now();
}