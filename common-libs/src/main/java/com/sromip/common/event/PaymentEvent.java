package com.sromip.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {

    private PaymentEventType eventType;

    private String transactionId;
    private String userEmail;

    private double amount;
    private double spareAmount;

    private LocalDateTime eventTime;
}

