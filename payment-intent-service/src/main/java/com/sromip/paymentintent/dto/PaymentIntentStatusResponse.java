package com.sromip.paymentintent.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PaymentIntentStatusResponse {

    private String paymentId;
    private String userEmail;
    private double amount;
    private String status;
    private String riskLevel;
    private String recommendedAction;
    private LocalDateTime createdAt;
}