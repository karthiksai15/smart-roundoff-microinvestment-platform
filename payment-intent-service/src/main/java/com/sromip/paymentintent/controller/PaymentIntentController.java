package com.sromip.paymentintent.controller;

import com.sromip.paymentintent.dto.PaymentIntentRequest;
import com.sromip.paymentintent.dto.PaymentIntentResponse;
import com.sromip.paymentintent.service.PaymentIntentService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment-intent")
@RequiredArgsConstructor
public class PaymentIntentController {

    private final PaymentIntentService service;

    @PostMapping
    public PaymentIntentResponse create(@RequestBody PaymentIntentRequest request) {
        return service.createIntent(request);
    }
}