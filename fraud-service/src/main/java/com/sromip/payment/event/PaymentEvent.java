package com.sromip.payment.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEvent {

    private Long paymentId;
    private String userEmail;
    private double originalAmount;
    private double roundedAmount;
    private double spareAmount;
    private PaymentEventType eventType;
}
