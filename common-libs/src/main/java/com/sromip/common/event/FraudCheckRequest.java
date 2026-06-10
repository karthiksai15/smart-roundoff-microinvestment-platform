package com.sromip.common.event;

import lombok.Data;

@Data
public class FraudCheckRequest {

    // ✅ FIXED
    private String paymentId;

    private String userEmail;
    private double amount;
    private String ipAddress;
    private String deviceId;

    private double roundOff;
    private int transactionsLast1Hr;
    private int transactionsLast24Hr;
    private double avgAmount7d;
    private double amountDeviation;
    private int hourOfDay;
    private int isNewUser;
}