package com.sromip.fraud.controller;

import com.sromip.fraud.entity.FraudCheck;
import com.sromip.fraud.repository.FraudCheckRepository;
import com.sromip.fraud.service.FraudService;

import com.sromip.common.event.FraudCheckRequest;
import com.sromip.common.event.FraudCheckResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/fraud")
@RequiredArgsConstructor
@Slf4j
public class FraudController {

    private final FraudService fraudService;
    private final FraudCheckRepository fraudCheckRepository;

    @PostMapping("/check")
    public FraudCheckResponse checkFraud(@RequestBody FraudCheckRequest request) {

        String traceId = MDC.get("traceId");

        log.info("🔍 Fraud API called for paymentId={}", request.getPaymentId());

        // ✅ Core fraud logic
        FraudCheckResponse response = fraudService.check(request);

        // ✅ Store audit
        FraudCheck fraudCheck = new FraudCheck();
        fraudCheck.setPaymentId(request.getPaymentId());
        fraudCheck.setUserEmail(request.getUserEmail());
        fraudCheck.setAmount(request.getAmount());
        fraudCheck.setSuspicious(!response.isAllow());
        fraudCheck.setReason(response.getReason());
        fraudCheck.setCheckedAt(LocalDateTime.now());

        fraudCheckRepository.save(fraudCheck);

        log.info("✅ Fraud decision completed paymentId={} decision={}",
                request.getPaymentId(),
                response.getReason());

        // ✅ RETURN ONLY (NO CALLBACK)
        return response;
    }
}