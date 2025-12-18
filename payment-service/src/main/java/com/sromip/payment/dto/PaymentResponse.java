package com.sromip.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private double originalAmount;
    private double roundedAmount;
    private double spareAmount;
    private String status;
    private String userEmail;
}

