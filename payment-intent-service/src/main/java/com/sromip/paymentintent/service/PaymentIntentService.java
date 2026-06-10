package com.sromip.paymentintent.service;

import com.sromip.paymentintent.client.FraudClient;
import com.sromip.paymentintent.dto.PaymentIntentRequest;
import com.sromip.paymentintent.dto.PaymentIntentResponse;
import com.sromip.paymentintent.dto.PaymentIntentStatusResponse;
import com.sromip.paymentintent.entity.PaymentIntent;
import com.sromip.paymentintent.repository.PaymentIntentRepository;
import com.sromip.paymentintent.idempotency.IdempotencyService;
import com.sromip.paymentintent.util.PaymentValidator;
import com.sromip.paymentintent.event.FraudDecisionEventPublisher;
import com.sromip.paymentintent.event.PaymentIntentEventPublisher;

import com.sromip.paymentintent.enums.PaymentIntentStatus;
import com.sromip.paymentintent.statemachine.PaymentStateMachine;

import com.sromip.common.event.FraudCheckRequest;
import com.sromip.common.event.FraudCheckResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentIntentService {

    private final PaymentIntentRepository paymentIntentRepository;
    private final IdempotencyService idempotencyService;
    private final FraudClient fraudClient;

    private final FraudDecisionEventPublisher decisionPublisher;
    private final PaymentIntentEventPublisher intentPublisher;

    @Transactional
    public PaymentIntentResponse createIntent(
            PaymentIntentRequest request,
            String idempotencyKey
    ) {

        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);

        log.info("🔥 PAYMENT INTENT START traceId={}", traceId);
        log.info("Request user={} amount={}", request.getUserEmail(), request.getAmount());

        String requestHash = null;

        try {

            PaymentValidator.validate(request);

            if (idempotencyKey != null) {

                requestHash = idempotencyService.hashRequest(request);

                var cached = idempotencyService.checkCache(idempotencyKey);
                if (cached.isPresent()) {
                    log.info("⚡ Idempotency cache hit");
                    return cached.get();
                }

                if (!idempotencyService.acquireLock(idempotencyKey)) {
                    throw new RuntimeException("Duplicate request in progress");
                }

                var existing = idempotencyService.find(idempotencyKey);

                if (existing.isPresent()) {

                    var entity = existing.get();

                    if (!entity.getRequestHash().equals(requestHash)) {
                        throw new RuntimeException("Idempotency key reused");
                    }

                    if ("COMPLETED".equals(entity.getStatus())) {
                        return idempotencyService.parseResponse(entity.getResponse());
                    }
                }

                idempotencyService.createProcessing(idempotencyKey, requestHash);
            }

            String paymentId = UUID.randomUUID().toString();

            double amount = request.getAmount();
            double roundoff = Math.ceil(amount);
            double spare = roundoff - amount;

            PaymentIntent intent = PaymentIntent.builder()
                    .paymentId(paymentId)
                    .userEmail(request.getUserEmail())
                    .requestedAmount(amount)
                    .currency("INR")
                    .status(PaymentIntentStatus.INIT)
                    .createdAt(LocalDateTime.now())
                    .roundoffPreview(roundoff)
                    .spareAmountPreview(spare)
                    .build();

            PaymentStateMachine.validateTransition(
                    intent.getStatus(),
                    PaymentIntentStatus.CREATED
            );

            intent.setStatus(PaymentIntentStatus.CREATED);

            PaymentIntent saved;
            try {
                saved = paymentIntentRepository.save(intent);
            } catch (Exception e) {
                throw new RuntimeException("Duplicate payment detected");
            }

            intentPublisher.publish(
                    paymentId,
                    request.getUserEmail(),
                    amount,
                    "INR"
            );

            FraudCheckRequest fraudRequest = new FraudCheckRequest();
            fraudRequest.setPaymentId(paymentId);
            fraudRequest.setUserEmail(request.getUserEmail());
            fraudRequest.setAmount(amount);
            fraudRequest.setRoundOff(spare);

            FraudCheckResponse fraudResponse = fraudClient.checkFraud(fraudRequest);

            PaymentStateMachine.validateTransition(
                    saved.getStatus(),
                    PaymentIntentStatus.FRAUD_CHECKED
            );

            saved.setStatus(PaymentIntentStatus.FRAUD_CHECKED);

            // ✅ STORE FRAUD DATA
            saved.setTrustScore(fraudResponse.getRiskScore());

            if (fraudResponse.getRiskScore() > 0.7) {
                saved.setRiskLevel("HIGH");
                saved.setRecommendedAction("BLOCK");
            } else if (fraudResponse.getRiskScore() > 0.4) {
                saved.setRiskLevel("MEDIUM");
                saved.setRecommendedAction("OTP");
            } else {
                saved.setRiskLevel("LOW");
                saved.setRecommendedAction("ALLOW");
            }

            PaymentIntentStatus nextState;

            if (!fraudResponse.isAllow()) {
                nextState = PaymentIntentStatus.BLOCKED;
            } else if (fraudResponse.getRiskScore() > 0.4) {
                nextState = PaymentIntentStatus.OTP_REQUIRED;
            } else {
                nextState = PaymentIntentStatus.APPROVED;
            }

            PaymentStateMachine.validateTransition(
                    saved.getStatus(),
                    nextState
            );

            saved.setStatus(nextState);

            paymentIntentRepository.save(saved);

            decisionPublisher.publish(
                    paymentId,
                    request.getUserEmail(),
                    amount,
                    fraudResponse.getRiskScore(),
                    fraudResponse.isAllow()
            );

            PaymentIntentResponse response = PaymentIntentResponse.builder()
                    .intentId(saved.getId())
                    .amount(amount)
                    .roundoffPreview(roundoff)
                    .spareAmount(spare)
                    .status(saved.getStatus().name())
                    .build();

            if (idempotencyKey != null) {
                idempotencyService.complete(idempotencyKey, response);
            }

            return response;

        } catch (Exception ex) {

            log.error("❌ Error in createIntent", ex);

            if (idempotencyKey != null) {
                idempotencyService.markFailed(idempotencyKey);
            }

            throw ex;

        } finally {

            if (idempotencyKey != null) {
                idempotencyService.releaseLock(idempotencyKey);
            }

            MDC.clear();
        }
    }

    public PaymentIntentStatusResponse getStatus(String paymentId) {

        PaymentIntent intent = paymentIntentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("PaymentIntent not found"));

        return PaymentIntentStatusResponse.builder()
                .paymentId(intent.getPaymentId())
                .userEmail(intent.getUserEmail())
                .amount(intent.getRequestedAmount())
                .status(intent.getStatus().name())
                .riskLevel(intent.getRiskLevel())
                .recommendedAction(intent.getRecommendedAction())
                .createdAt(intent.getCreatedAt())
                .build();
    }
}