package com.sromip.paymentintent.dto;


import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PaymentIntentResponse {

    private UUID intentId;
    private double amount;
    private double roundoffPreview;
    private double spareAmount;
    private String status;
}