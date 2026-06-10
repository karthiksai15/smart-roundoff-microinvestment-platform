package com.sromip.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvestmentTriggerEvent {

    private String traceId; // ✅ ADDED

    private String paymentId;

    private String userEmail;
    private Double originalAmount;
    private Double spareAmount;
    private Double roundedAmount;
}