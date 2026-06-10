package com.sromip.investment.listener;

import com.sromip.common.event.InvestmentTriggerEvent;
import com.sromip.common.event.InvestmentCompletedEvent;
import com.sromip.investment.entity.Investment;
import com.sromip.investment.repository.InvestmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class InvestmentTriggerListener {

    private final InvestmentRepository investmentRepository;
    private final KafkaTemplate<String, InvestmentCompletedEvent> kafkaTemplate;

    private static final String INVESTMENT_COMPLETED_TOPIC = "investment-completed-topic";

    @KafkaListener(
            topics = "investment-trigger-topic",
            groupId = "investment-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(InvestmentTriggerEvent event) {

        MDC.put("traceId", event.getTraceId());

        try {

            String paymentId = event.getPaymentId();
            String email = event.getUserEmail();
            double spare = event.getSpareAmount();

            log.info("💰 Investment trigger received paymentId={} spare={}", paymentId, spare);

            // ✅ VALIDATION
            if (paymentId == null || email == null) {
                log.warn("❌ Invalid event received");
                return;
            }

            if (spare <= 0) {
                log.warn("⚠️ No spare amount, skipping investment paymentId={}", paymentId);
                return;
            }

            // ✅ IDEMPOTENCY CHECK (CRITICAL)
            Optional<Investment> existing = investmentRepository.findByPaymentId(paymentId);
            if (existing.isPresent()) {
                log.warn("⚠️ Duplicate investment ignored paymentId={}", paymentId);
                return;
            }

            // ✅ SAVE TO DB
            Investment investment = new Investment();
            investment.setPaymentId(paymentId);
            investment.setUserEmail(email);
            investment.setAmount(spare);
            investment.setTraceId(event.getTraceId());

            investmentRepository.save(investment);

            log.info("✅ Investment saved paymentId={} amount={}", paymentId, spare);

            // ✅ SEND EVENT
            InvestmentCompletedEvent completedEvent = new InvestmentCompletedEvent(
                    event.getTraceId(),
                    paymentId,
                    email,
                    spare
            );

            kafkaTemplate.send(
                    INVESTMENT_COMPLETED_TOPIC,
                    paymentId,
                    completedEvent
            );

            log.info("📤 Investment event sent paymentId={}", paymentId);

        } catch (Exception e) {

            log.error("❌ Investment processing failed paymentId={}", event.getPaymentId(), e);

            // 🔥 IMPORTANT → retry + DLQ
            throw e;

        } finally {
            MDC.clear();
        }
    }
}