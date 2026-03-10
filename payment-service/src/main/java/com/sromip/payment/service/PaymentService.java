package com.sromip.payment.service;

import com.sromip.common.event.FraudDecisionEvent;
import com.sromip.common.event.OtpRequestEvent;
import com.sromip.common.event.PaymentEvent;
import com.sromip.common.event.PaymentEventType;

import com.sromip.payment.entity.Payment;
import com.sromip.payment.entity.UserPreference;
import com.sromip.payment.repository.PaymentRepository;
import com.sromip.payment.repository.UserPreferenceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserPreferenceRepository userPreferenceRepository;

    private final KafkaTemplate<String, PaymentEvent> paymentKafkaTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String PAYMENT_TOPIC = "payment-topic";
    private static final String OTP_REQUEST_TOPIC = "otp-request-topic";

    public void processDecision(FraudDecisionEvent event) {

        log.info("📥 Fraud decision received for {}", event.getUserEmail());

        double riskScore = event.getRiskScore();
        String riskLevel = determineRiskLevel(riskScore);

        if ("BLOCKED".equals(event.getStatus())) {
            log.warn("❌ Blocked by Fraud Service");
            return;
        }

        boolean otpRequired = false;
        String finalStatus;

        switch (riskLevel) {

            case "HIGH":
                finalStatus = "BLOCKED";
                break;

            case "MEDIUM":
                finalStatus = "OTP_REQUIRED";
                otpRequired = true;
                break;

            default:
                finalStatus = "APPROVED";
        }

        if ("BLOCKED".equals(finalStatus)) {
            log.warn("❌ Payment blocked due to HIGH risk");
            return;
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

                case "ROUND_UP":
                    roundedAmount = Math.ceil(originalAmount);
                    break;

                case "ROUND_DOWN":
                    roundedAmount = Math.floor(originalAmount);
                    break;

                case "NEAREST":
                    roundedAmount = Math.round(originalAmount);
                    break;

                default:
                    roundedAmount = originalAmount;
                    roundingStrategy = "NO_ROUND";
            }

        } else {

            if ("LOW".equals(riskLevel)) {
                roundedAmount = Math.ceil(originalAmount);
                roundingStrategy = "RISK_ROUND_UP";
            }
            else if ("MEDIUM".equals(riskLevel)) {
                roundedAmount = Math.round(originalAmount);
                roundingStrategy = "RISK_NEAREST";
            }
            else {
                roundedAmount = originalAmount;
                roundingStrategy = "RISK_NO_ROUND";
            }
        }

        double spareAmount = roundedAmount - originalAmount;

        Payment payment = new Payment();

        payment.setUserEmail(event.getUserEmail());
        payment.setOriginalAmount(originalAmount);
        payment.setRoundedAmount(roundedAmount);
        payment.setSpareAmount(spareAmount);
        payment.setRiskScore(riskScore);
        payment.setRiskLevel(riskLevel);
        payment.setOtpRequired(otpRequired);
        payment.setOtpVerified(!otpRequired);
        payment.setRoundingStrategy(roundingStrategy);
        payment.setStatus(finalStatus);

        Payment saved = paymentRepository.save(payment);

        log.info("💾 Payment stored with status {}", finalStatus);

        if ("OTP_REQUIRED".equals(finalStatus)) {

            kafkaTemplate.send(
                    OTP_REQUEST_TOPIC,
                    new OtpRequestEvent(saved.getUserEmail())
            );

            log.info("📤 OTP request emitted");
            return;
        }

        if ("APPROVED".equals(finalStatus)) {

            emitPaymentCompleted(
                    saved,
                    event.getPaymentId()   // ✔ Correct pipeline ID
            );
        }
    }

    public void resumeAfterOtp(String userEmail) {

        Payment payment = paymentRepository
                .findTopByUserEmailOrderByIdDesc(userEmail)
                .orElseThrow();

        payment.setOtpVerified(true);
        payment.setStatus("APPROVED");

        paymentRepository.save(payment);

        emitPaymentCompleted(
                payment,
                payment.getId()   // used only when OTP resumes
        );
    }

    private void emitPaymentCompleted(Payment payment, long paymentId) {

        PaymentEvent event = new PaymentEvent(
                PaymentEventType.PAYMENT_COMPLETED,
                String.valueOf(paymentId),
                payment.getUserEmail(),
                payment.getOriginalAmount(),
                payment.getSpareAmount(),
                LocalDateTime.now()
        );

        paymentKafkaTemplate.send(PAYMENT_TOPIC, event);

        log.info("📤 PAYMENT_COMPLETED emitted for {}", paymentId);
    }

    private String determineRiskLevel(double riskScore) {

        if (riskScore > 0.8) return "HIGH";
        if (riskScore > 0.4) return "MEDIUM";

        return "LOW";
    }
}