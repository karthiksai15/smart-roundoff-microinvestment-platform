package com.sromip.payment.dto;

import lombok.Data;

@Data
public class FraudCheckRequest {
    private String userEmail;
    private double amount;
    private String ipAddress;
    private String deviceId;
}
