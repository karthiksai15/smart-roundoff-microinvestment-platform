package com.sromip.payment.service;

import com.sromip.payment.dto.PaymentRequest;
import com.sromip.payment.entity.Payment;
import com.sromip.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public Payment processPayment(PaymentRequest request) {

        double original = request.getAmount();
        double rounded = Math.ceil(original / 100) * 100;
        double spare = rounded - original;

        Payment payment = new Payment();
        payment.setUserEmail(request.getUserEmail());
        payment.setOriginalAmount(original);
        payment.setRoundedAmount(rounded);
        payment.setSpareAmount(spare);
        payment.setStatus("SUCCESS");

        return paymentRepository.save(payment);
    }
}
