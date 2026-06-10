package com.sromip.paymentintent.util;

import com.sromip.paymentintent.dto.PaymentIntentRequest;

public class PaymentValidator {

    public static void validate(PaymentIntentRequest request) {

        if (request == null) {
            throw new RuntimeException("Request cannot be null");
        }

        if (request.getUserEmail() == null || request.getUserEmail().isEmpty()) {
            throw new RuntimeException("Invalid user email");
        }

        if (request.getAmount() <= 0) {
            throw new RuntimeException("Invalid amount");
        }

        // ✅ UPDATED LIMIT (allow high payments)
        if (request.getAmount() > 1000000) { // 10 lakh safety limit
            throw new RuntimeException("Amount exceeds system limit");
        }
    }
}