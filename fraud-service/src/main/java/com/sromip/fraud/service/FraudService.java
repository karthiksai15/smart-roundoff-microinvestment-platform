package com.sromip.fraud.service;

import com.sromip.fraud.dto.FraudCheckRequest;
import com.sromip.fraud.dto.FraudCheckResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudService {

    public FraudCheckResponse check(FraudCheckRequest req) {

        FraudCheckResponse res = new FraudCheckResponse();

        double amount = req.getAmount();

        log.info("🔍 Fraud check started for amount: {}", amount);

        double riskScore;

        if (amount > 500000) {

            log.warn("🚨 HIGH RISK TRANSACTION BLOCKED");

            riskScore = 0.95;

            res.setAllow(false);
            res.setRiskScore(riskScore);
            res.setReason("HIGH risk — blocked");

        }

        else if (amount > 100000) {

            log.warn("⚠ MEDIUM RISK — OTP REQUIRED");

            riskScore = 0.65;

            res.setAllow(true);
            res.setRiskScore(riskScore);
            res.setReason("MEDIUM risk — OTP required");

        }

        else {

            log.info("✅ LOW RISK — Approved");

            riskScore = 0.10;

            res.setAllow(true);
            res.setRiskScore(riskScore);
            res.setReason("LOW risk approved");
        }

        return res;
    }
}