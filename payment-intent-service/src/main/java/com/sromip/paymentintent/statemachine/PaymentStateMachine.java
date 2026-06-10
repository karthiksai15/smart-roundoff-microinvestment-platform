package com.sromip.paymentintent.statemachine;

import com.sromip.paymentintent.enums.PaymentIntentStatus;

import java.util.Map;
import java.util.Set;

public class PaymentStateMachine {

    private static final Map<PaymentIntentStatus, Set<PaymentIntentStatus>> transitions = Map.of(

            PaymentIntentStatus.INIT, Set.of(
                    PaymentIntentStatus.CREATED
            ),

            PaymentIntentStatus.CREATED, Set.of(
                    PaymentIntentStatus.FRAUD_CHECKED
            ),

            PaymentIntentStatus.FRAUD_CHECKED, Set.of(
                    PaymentIntentStatus.PROCESSING,
                    PaymentIntentStatus.APPROVED,
                    PaymentIntentStatus.OTP_REQUIRED,
                    PaymentIntentStatus.BLOCKED
            ),

            PaymentIntentStatus.PROCESSING, Set.of(
                    PaymentIntentStatus.APPROVED,
                    PaymentIntentStatus.OTP_REQUIRED,
                    PaymentIntentStatus.BLOCKED
            ),

            PaymentIntentStatus.OTP_REQUIRED, Set.of(
                    PaymentIntentStatus.APPROVED,
                    PaymentIntentStatus.FAILED
            ),

            PaymentIntentStatus.APPROVED, Set.of(),
            PaymentIntentStatus.BLOCKED, Set.of(),
            PaymentIntentStatus.FAILED, Set.of()
    );

    public static void validateTransition(
            PaymentIntentStatus current,
            PaymentIntentStatus next
    ) {

        if (!transitions.containsKey(current) ||
                !transitions.get(current).contains(next)) {

            throw new IllegalStateException(
                    "❌ Invalid state transition: " + current + " → " + next
            );
        }
    }
}