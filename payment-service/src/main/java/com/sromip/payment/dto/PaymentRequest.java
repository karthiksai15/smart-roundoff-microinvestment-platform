package com.sromip.payment.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private String userEmail;
    private double amount;
}
