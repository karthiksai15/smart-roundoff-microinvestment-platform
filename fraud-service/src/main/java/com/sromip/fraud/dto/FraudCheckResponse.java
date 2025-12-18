package com.sromip.fraud.dto;

import lombok.Data;

@Data
public class FraudCheckResponse {
    private boolean allow;
    private String reason;
}
