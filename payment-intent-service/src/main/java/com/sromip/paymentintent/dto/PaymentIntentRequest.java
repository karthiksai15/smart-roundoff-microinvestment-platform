package com.sromip.paymentintent.dto;

import lombok.Data;

@Data
public class PaymentIntentRequest {

    private String userEmail;
    private double amount;
}