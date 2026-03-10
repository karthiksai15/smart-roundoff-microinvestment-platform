package com.sromip.fraud.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MLResponse {

    @JsonProperty("is_anomaly")
    private boolean isAnomaly;

    @JsonProperty("risk_score")
    private double riskScore;

    @JsonProperty("latency_ms")
    private double latencyMs;
}