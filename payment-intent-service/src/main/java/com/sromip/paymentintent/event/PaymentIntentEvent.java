package com.sromip.paymentintent.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PaymentIntentEvent {

    private UUID intentId;
    private String userEmail;
    private double amount;
    private double roundoffPreview;
    private double spareAmount;
    private String status;
    private LocalDateTime createdAt;
}