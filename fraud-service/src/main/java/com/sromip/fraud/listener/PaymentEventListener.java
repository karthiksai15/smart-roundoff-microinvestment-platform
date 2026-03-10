package com.sromip.fraud.listener;

import com.sromip.common.event.PaymentIntentEvent;
import com.sromip.common.event.FraudDecisionEvent;
import com.sromip.fraud.dto.FraudCheckRequest;
import com.sromip.fraud.dto.FraudCheckResponse;
import com.sromip.fraud.entity.FraudCheck;
import com.sromip.fraud.repository.FraudCheckRepository;
import com.sromip.fraud.service.FraudService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final KafkaTemplate<String, FraudDecisionEvent> kafkaTemplate;
    private final FraudCheckRepository fraudCheckRepository;
    private final FraudService fraudService;

    @KafkaListener(
            topics = "payment-intent-topic",
            groupId = "fraud-service-group"
    )
    public void handlePaymentIntentEvent(PaymentIntentEvent event) {

        log.info("🚨 Fraud Service received INTENT event");
        log.info("User: {}", event.getUserEmail());
        log.info("Amount: {}", event.getRequestedAmount());

        // Convert UUID → Long
        Long paymentId = event.getIntentId().getMostSignificantBits();

        FraudCheckRequest request = new FraudCheckRequest();

        request.setPaymentId(paymentId);
        request.setUserEmail(event.getUserEmail());
        request.setAmount(event.getRequestedAmount());
        request.setRoundOff(event.getSpareAmountPreview());
        request.setTransactionsLast1Hr(5);
        request.setTransactionsLast24Hr(10);
        request.setAvgAmount7d(400);
        request.setAmountDeviation(1.2);
        request.setHourOfDay(LocalDateTime.now().getHour());
        request.setIsNewUser(0);
        request.setIpAddress("127.0.0.1");
        request.setDeviceId("device-123");

        FraudCheckResponse response = fraudService.check(request);

        FraudCheck fraudCheck = new FraudCheck();

        fraudCheck.setUserEmail(event.getUserEmail());
        fraudCheck.setAmount(event.getRequestedAmount());
        fraudCheck.setSuspicious(!response.isAllow());
        fraudCheck.setReason(response.getReason());
        fraudCheck.setCheckedAt(LocalDateTime.now());

        fraudCheckRepository.save(fraudCheck);

        String status = response.isAllow() ? "APPROVED" : "BLOCKED";

        log.info("🔥 Fraud Decision: {}", status);

        FraudDecisionEvent decisionEvent = new FraudDecisionEvent(
                paymentId,
                event.getUserEmail(),
                event.getRequestedAmount(),
                response.getRiskScore(),
                status,
                LocalDateTime.now()
        );

        kafkaTemplate.send("trust-decision-topic", decisionEvent);

        log.info("📤 Sent decision → trust-decision-topic");
    }
}