package com.sromip.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sromip.common.event.FraudDecisionEvent;
import com.sromip.payment.entity.DLQEvent;
import com.sromip.payment.entity.DLQStatus;
import com.sromip.payment.repository.DLQEventRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DLQService {

    private final DLQEventRepository repository;
    private final ObjectMapper objectMapper;

    // ❌ REMOVE KafkaTemplate usage (IMPORTANT)
    // private final KafkaTemplate<String, Object> kafkaTemplate;

    private final PaymentService paymentService; // 🔥 NEW

    private static final int MAX_RETRIES = 3;

    // ================= SAVE FAILED EVENT =================
    public void save(FraudDecisionEvent event, String reason) {

        try {

            Optional<DLQEvent> existing =
                    repository.findByPaymentId(event.getPaymentId());

            if (existing.isPresent()) {
                log.warn("⚠️ DLQ already exists for paymentId={}, skipping insert",
                        event.getPaymentId());
                return;
            }

            DLQEvent dlq = DLQEvent.builder()
                    .paymentId(event.getPaymentId())
                    .reason(reason)
                    .payload(objectMapper.writeValueAsString(event))
                    .createdAt(LocalDateTime.now())
                    .status(DLQStatus.NEW)
                    .retryCount(0)
                    .nextRetryAt(LocalDateTime.now().plusSeconds(10))
                    .build();

            repository.save(dlq);

            log.error("📥 DLQ stored paymentId={}", event.getPaymentId());

        } catch (Exception e) {
            log.error("❌ Failed to store DLQ", e);
        }
    }

    // ================= RETRY EVENT =================
    public void retryEvent(DLQEvent dlqEvent) {

        try {

            if (dlqEvent.getRetryCount() >= MAX_RETRIES) {
                dlqEvent.setStatus(DLQStatus.FAILED);
                repository.save(dlqEvent);

                log.error("❌ Max retries exceeded paymentId={}", dlqEvent.getPaymentId());
                return;
            }

            FraudDecisionEvent event =
                    objectMapper.readValue(dlqEvent.getPayload(), FraudDecisionEvent.class);

            // 🔥 DIRECT METHOD CALL (NO KAFKA → NO LOOP)
            paymentService.processDecision(event);

            int nextRetryCount = dlqEvent.getRetryCount() + 1;

            dlqEvent.setRetryCount(nextRetryCount);
            dlqEvent.setStatus(DLQStatus.NEW);
            dlqEvent.setNextRetryAt(calculateNextRetryTime(nextRetryCount));

            repository.save(dlqEvent);

            log.info("🔁 Retried DLQ paymentId={}", event.getPaymentId());

        } catch (Exception e) {

            int nextRetryCount = dlqEvent.getRetryCount() + 1;
            dlqEvent.setRetryCount(nextRetryCount);

            if (nextRetryCount >= MAX_RETRIES) {
                dlqEvent.setStatus(DLQStatus.FAILED);
            } else {
                dlqEvent.setStatus(DLQStatus.NEW);
                dlqEvent.setNextRetryAt(calculateNextRetryTime(nextRetryCount));
            }

            repository.save(dlqEvent);

            log.error("❌ Retry failed paymentId={}", dlqEvent.getPaymentId(), e);
        }
    }

    private LocalDateTime calculateNextRetryTime(int retryCount) {

        return switch (retryCount) {
            case 1 -> LocalDateTime.now().plusSeconds(10);
            case 2 -> LocalDateTime.now().plusSeconds(30);
            case 3 -> LocalDateTime.now().plusMinutes(2);
            default -> LocalDateTime.now().plusMinutes(5);
        };
    }
}