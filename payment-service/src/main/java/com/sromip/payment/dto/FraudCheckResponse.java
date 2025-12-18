package com.sromip.payment.dto;

import lombok.Data;

@Data
public class FraudCheckResponse {
    private boolean allow;
    private String reason;
}
