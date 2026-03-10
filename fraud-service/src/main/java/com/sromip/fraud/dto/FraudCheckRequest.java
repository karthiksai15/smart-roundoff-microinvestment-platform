package com.sromip.fraud.dto;

import lombok.Data;

@Data
public class FraudCheckRequest {

    private Long paymentId;

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