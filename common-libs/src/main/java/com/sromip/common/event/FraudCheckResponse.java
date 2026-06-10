package com.sromip.common.event;

import lombok.Data;

@Data
public class FraudCheckResponse {

    private boolean allow;
    private String reason;
    private double riskScore;   // 🔴 ADD THIS FIELD
}