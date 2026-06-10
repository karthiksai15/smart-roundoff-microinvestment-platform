package com.sromip.payment.listener;

import com.sromip.common.event.FraudDecisionEvent;
import com.sromip.payment.service.PaymentService;
import com.sromip.payment.service.IdempotencyService;
import com.sromip.payment.service.DLQService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FraudDecisionListener {

    private final PaymentService paymentService;
    private final IdempotencyService idempotencyService;
    private final DLQService dlqService;

    @KafkaListener(
            topics = "fraud-decision-topic",
            groupId = "payment-service-group"
    )
    public void consume(FraudDecisionEvent event) {

        MDC.put("traceId", event.getTraceId());

        try {

            String paymentId = event.getPaymentId();

            log.info("📥 FRAUD DECISION RECEIVED paymentId={}", paymentId);

            // ✅ Idempotency check
            if (idempotencyService.isAlreadyProcessed(paymentId)) {
                log.warn("⚠️ Duplicate detected paymentId={}", paymentId);
                return;
            }

            // ✅ Process
            paymentService.processDecision(event);

            idempotencyService.markProcessed(paymentId);

            log.info("✅ Payment processed paymentId={}", paymentId);

        } catch (Exception ex) {

            log.error("❌ Error processing paymentId={} → DLQ",
                    event.getPaymentId(), ex);

            dlqService.save(event, ex.getMessage());

            throw ex; // important for retry
        } finally {
            MDC.clear();
        }
    }
}