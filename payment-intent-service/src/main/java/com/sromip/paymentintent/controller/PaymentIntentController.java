package com.sromip.paymentintent.controller;

import com.sromip.paymentintent.dto.PaymentIntentRequest;
import com.sromip.paymentintent.dto.PaymentIntentResponse;
import com.sromip.paymentintent.dto.PaymentIntentStatusResponse;
import com.sromip.paymentintent.service.PaymentIntentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/payment-intent")
@RequiredArgsConstructor
@Slf4j
public class PaymentIntentController {

    private final PaymentIntentService service;

    @PostMapping("/create")
    public ResponseEntity<PaymentIntentResponse> create(
            @RequestBody PaymentIntentRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String key
    ) {
        log.info("🔥 HIT CREATE ENDPOINT");

        return ResponseEntity.ok(
                service.createIntent(request, key)
        );
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentIntentStatusResponse> getStatus(
            @PathVariable String paymentId
    ) {
        log.info("📡 Fetching status for paymentId={}", paymentId);

        return ResponseEntity.ok(
                service.getStatus(paymentId)
        );
    }

    @GetMapping("/test")
    public String test() {
        return "WORKING";
    }
}