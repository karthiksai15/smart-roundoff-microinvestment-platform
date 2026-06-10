package com.sromip.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentIntentEvent {

    private String traceId;
    private String paymentId;

    private UUID intentId;
    private String userEmail;
    private double requestedAmount;
    private double roundoffPreview;
    private double spareAmountPreview;
    private String status;
    private LocalDateTime createdAt;
}