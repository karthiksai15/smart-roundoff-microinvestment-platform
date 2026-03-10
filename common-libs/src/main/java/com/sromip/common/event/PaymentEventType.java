package com.sromip.common.event;

public enum PaymentEventType {

    PAYMENT_CREATED,     // when payment first initiated
    PAYMENT_COMPLETED    // when OTP verified / final approval
}