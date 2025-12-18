package com.sromip.payment.controller;

import com.sromip.payment.dto.PaymentRequest;
import com.sromip.payment.entity.Payment;
import com.sromip.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/execute")
    public Payment execute(@RequestBody PaymentRequest request) {
        return paymentService.processPayment(request);
    }
}
