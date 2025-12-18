package com.sromip.payment.dto;

import lombok.Data;

@Data
public class AddInvestmentRequest {
    private String userEmail;
    private double spareAmount;
}
