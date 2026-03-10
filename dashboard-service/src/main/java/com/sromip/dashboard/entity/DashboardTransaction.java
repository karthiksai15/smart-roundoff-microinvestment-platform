package com.sromip.dashboard.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "dashboard_transactions")
@Data
public class DashboardTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unique pipeline identifier
    private Long paymentId;

    private String userEmail;

    private String paymentStatus;

    private String fraudStatus;

    private Double mlRiskScore;

    private String investmentStatus;

    private Double investedAmount;

    private String notificationStatus;

    private LocalDateTime lastUpdated;
}