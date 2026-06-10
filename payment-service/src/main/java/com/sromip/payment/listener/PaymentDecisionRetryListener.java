package com.sromip.payment.listener;

import com.sromip.common.event.FraudDecisionEvent;
import com.sromip.payment.service.PaymentService;
import com.sromip.payment.service.IdempotencyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentDecisionRetryListener {

    private final PaymentService paymentService;
    private final IdempotencyService idempotencyService;

    @KafkaListener(
            topics = "payment-decision-retry-topic",
            groupId = "payment-service-group"
    )
    public void consumeRetry(FraudDecisionEvent event) {

        String paymentId = event.getPaymentId();

        log.warn("🔁 RETRY EVENT RECEIVED paymentId={}", paymentId);

        try {

            // ✅ Idempotency check (CRITICAL)
            if (idempotencyService.isAlreadyProcessed(paymentId)) {
                log.warn("⚠️ Duplicate retry ignored paymentId={}", paymentId);
                return;
            }

            // ✅ Process again
            paymentService.processDecision(event);

            // ✅ Mark processed
            idempotencyService.markProcessed(paymentId);

            log.info("✅ RETRY SUCCESS paymentId={}", paymentId);

        } catch (Exception ex) {

            log.error("❌ RETRY FAILED AGAIN paymentId={}",
                    paymentId, ex);

            // Let DLQ / retry mechanism handle again
            throw ex;
        }
    }
}