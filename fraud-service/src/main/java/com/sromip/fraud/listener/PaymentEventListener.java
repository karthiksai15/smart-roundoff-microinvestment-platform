package com.sromip.fraud.listener;

import com.sromip.common.event.PaymentEvent;
import com.sromip.fraud.entity.FraudCheck;
import com.sromip.fraud.repository.FraudCheckRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final FraudCheckRepository fraudCheckRepository;

    @KafkaListener(
            topics = "payment-events",
            groupId = "fraud-service-group"
    )
    public void handlePaymentEvent(PaymentEvent event) {

        log.warn("🚨 Fraud Service received payment event");
        log.warn("User: {}", event.getUserEmail());
        log.warn("Amount: {}", event.getAmount());

        // 🔎 Simple fraud rule (for now)
        boolean suspicious = event.getAmount() > 1500;
        String reason = suspicious
                ? "High transaction amount"
                : "Amount within safe limit";

        // 🗄️ Save to DB
        FraudCheck fraudCheck = new FraudCheck();
        fraudCheck.setUserEmail(event.getUserEmail());
        fraudCheck.setAmount(event.getAmount());
        fraudCheck.setSuspicious(suspicious);
        fraudCheck.setReason(reason);
        fraudCheck.setCheckedAt(LocalDateTime.now());

        fraudCheckRepository.save(fraudCheck);

        log.warn("✅ Fraud check saved to database");
    }
}
