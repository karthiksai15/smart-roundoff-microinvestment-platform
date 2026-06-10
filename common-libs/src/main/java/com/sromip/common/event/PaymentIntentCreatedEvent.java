package com.sromip.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentIntentCreatedEvent {

    private String traceId;
    private String paymentId;
    private String userEmail;
    private double amount;
    private String currency;
    private LocalDateTime createdAt;
}