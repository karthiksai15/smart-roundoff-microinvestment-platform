package com.sromip.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FraudDecisionEvent {

    private long paymentId;
    private String userEmail;
    private double amount;
    private double riskScore;
    private String status; // APPROVED or BLOCKED
    private LocalDateTime decidedAt;
}

