package com.sromip.payment.service;

import com.sromip.common.event.*;
import com.sromip.payment.entity.*;
import com.sromip.payment.repository.PaymentRepository;
import com.sromip.payment.repository.UserPreferenceRepository;

import org.springframework.beans.factory.annotation.Qualifier;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final IdempotencyService idempotencyService;

    private final KafkaTemplate<String, PaymentEvent> paymentKafkaTemplate;

    @Qualifier("kafkaTemplate")
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String PAYMENT_TOPIC = "payment-topic";
    private static final String OTP_REQUEST_TOPIC = "otp-request-topic";
    private static final String INVESTMENT_TRIGGER_TOPIC = "investment-trigger-topic";

    public void processDecision(FraudDecisionEvent event) {

        String traceId = event.getTraceId();
        MDC.put("traceId", traceId);

        try {

            String paymentId = event.getPaymentId();

            log.info("🔥 EVENT RECEIVED → paymentId={}, amount={}, status={}",
                    paymentId, event.getAmount(), event.getStatus());

            // ✅ DUPLICATE DB CHECK (CRITICAL FIX)
            Optional<Payment> existing = paymentRepository.findByPaymentId(paymentId);
            if (existing.isPresent()) {
                log.warn("⚠️ Duplicate payment ignored paymentId={}", paymentId);
                return;
            }

            // ✅ IDEMPOTENCY CHECK
            if (idempotencyService.isAlreadyProcessed(paymentId)) {
                log.warn("⚠️ Idempotent duplicate ignored paymentId={}", paymentId);
                return;
            }

            if ("BLOCKED".equals(event.getStatus())) {

                Payment payment = new Payment();
                payment.setPaymentId(paymentId);
                payment.setUserEmail(event.getUserEmail());
                payment.setStatus(PaymentStatus.FAILED);
                payment.setRiskLevel("HIGH");

                paymentRepository.save(payment);

                // CRITICAL: EMIT FAILURE EVENT
                PaymentEvent failedEvent = new PaymentEvent(
                        traceId,
                        paymentId,
                        PaymentEventType.PAYMENT_FAILED,
                        event.getUserEmail(),
                        event.getAmount(),
                        0.0,
                        LocalDateTime.now()
                );

                paymentKafkaTemplate.send(PAYMENT_TOPIC, paymentId, failedEvent);

                log.warn("❌ Payment failed (fraud blocked) paymentId={}", paymentId);

                idempotencyService.markProcessed(paymentId);
                return;
            }

            double riskScore = event.getRiskScore();
            String riskLevel = determineRiskLevel(riskScore);

            boolean otpRequired = false;
            PaymentStatus finalStatus;

            switch (riskLevel) {
                case "HIGH":
                    finalStatus = PaymentStatus.FAILED;
                    break;
                case "MEDIUM":
                    finalStatus = PaymentStatus.PENDING_OTP;
                    otpRequired = true;
                    break;
                default:
                    finalStatus = PaymentStatus.APPROVED;
            }

            double originalAmount = event.getAmount();
            double roundedAmount;
            String roundingStrategy;

            UserPreference preference = userPreferenceRepository
                    .findByUserEmail(event.getUserEmail())
                    .orElse(null);

            if (preference != null) {

                roundingStrategy = preference.getRoundingPreference();

                switch (roundingStrategy) {
                    case "ROUND_UP": roundedAmount = Math.ceil(originalAmount); break;
                    case "ROUND_DOWN": roundedAmount = Math.floor(originalAmount); break;
                    case "NEAREST": roundedAmount = Math.round(originalAmount); break;
                    default:
                        roundedAmount = originalAmount;
                        roundingStrategy = "NO_ROUND";
                }

            } else {

                if ("LOW".equals(riskLevel)) {
                    roundedAmount = Math.ceil(originalAmount);
                    roundingStrategy = "RISK_ROUND_UP";
                } else if ("MEDIUM".equals(riskLevel)) {
                    roundedAmount = Math.round(originalAmount);
                    roundingStrategy = "RISK_NEAREST";
                } else {
                    roundedAmount = originalAmount;
                    roundingStrategy = "RISK_NO_ROUND";
                }
            }

            // ✅ FIX FLOAT ISSUE
            double spareAmount = BigDecimal.valueOf(roundedAmount)
                    .subtract(BigDecimal.valueOf(originalAmount))
                    .doubleValue();

            Payment payment = new Payment();
            payment.setPaymentId(paymentId);
            payment.setUserEmail(event.getUserEmail());
            payment.setOriginalAmount(originalAmount);
            payment.setRoundedAmount(roundedAmount);
            payment.setSpareAmount(spareAmount);
            payment.setRiskScore(riskScore);
            payment.setRiskLevel(riskLevel);
            payment.setOtpRequired(otpRequired);
            payment.setOtpVerified(false);
            payment.setRoundingStrategy(roundingStrategy);
            payment.setStatus(finalStatus);

            if (finalStatus == PaymentStatus.PENDING_OTP) {

                String otpSessionId = UUID.randomUUID().toString();
                long expiryTime = System.currentTimeMillis() + (5 * 60 * 1000);

                payment.setOtpSessionId(otpSessionId);
                payment.setOtpAttempts(0);
                payment.setOtpExpiryTime(expiryTime);

                paymentRepository.save(payment);

                kafkaTemplate.send(
                        OTP_REQUEST_TOPIC,
                        paymentId,
                        new OtpRequestEvent(
                                traceId,
                                paymentId,
                                otpSessionId,
                                event.getUserEmail(),
                                expiryTime,
                                5
                        )
                );

                return;
            }

            paymentRepository.save(payment);

            if (finalStatus == PaymentStatus.APPROVED) {
                emitPaymentCompleted(payment, paymentId, traceId);
            }

            idempotencyService.markProcessed(paymentId);

        } finally {
            MDC.clear();
        }
    }

    private void emitPaymentCompleted(Payment payment, String paymentId, String traceId) {

        PaymentEvent event = new PaymentEvent(
                traceId,
                paymentId,
                PaymentEventType.PAYMENT_COMPLETED,
                payment.getUserEmail(),
                payment.getOriginalAmount(),
                payment.getSpareAmount(),
                LocalDateTime.now()
        );

        paymentKafkaTemplate.send(PAYMENT_TOPIC, paymentId, event);

        kafkaTemplate.send(
                INVESTMENT_TRIGGER_TOPIC,
                paymentId,
                new InvestmentTriggerEvent(
                        traceId,
                        paymentId,
                        payment.getUserEmail(),
                        payment.getOriginalAmount(),
                        payment.getSpareAmount(),
                        payment.getRoundedAmount()
                )
        );
    }

    private String determineRiskLevel(double riskScore) {
        if (riskScore > 0.8) return "HIGH";
        if (riskScore > 0.4) return "MEDIUM";
        return "LOW";
    }

    public void resumeAfterOtp(String paymentId, String traceId) {

        MDC.put("traceId", traceId);

        try {

            Payment payment = paymentRepository
                    .findByPaymentId(paymentId)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            if (payment.getStatus() != PaymentStatus.PENDING_OTP) {
                log.warn("⚠️ Payment not in OTP state paymentId={}", paymentId);
                return;
            }

            payment.setOtpVerified(true);
            payment.setStatus(PaymentStatus.COMPLETED);

            paymentRepository.save(payment);

            log.info("✅ OTP verified → completing payment paymentId={}", paymentId);

            emitPaymentCompleted(payment, paymentId, traceId);

        } finally {
            MDC.clear();
        }
    }
}