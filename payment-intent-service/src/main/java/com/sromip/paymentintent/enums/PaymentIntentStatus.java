package com.sromip.paymentintent.enums;

public enum PaymentIntentStatus {

    INIT,
    CREATED,
    FRAUD_CHECKED,
    PROCESSING,
    OTP_REQUIRED,
    APPROVED,
    BLOCKED,
    FAILED
}