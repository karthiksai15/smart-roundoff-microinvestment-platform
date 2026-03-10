package com.sromip.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvestmentCompletedEvent {

    private Long paymentId;
    private String userEmail;
    private Double investedAmount;
}