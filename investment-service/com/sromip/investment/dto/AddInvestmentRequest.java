package com.sromip.investment.dto;

import lombok.Data;

@Data
public class AddInvestmentRequest {
    private String userEmail;
    private double spareAmount;
}

