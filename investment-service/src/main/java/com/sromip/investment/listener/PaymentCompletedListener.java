package com.sromip.investment.listener;

import com.sromip.common.event.PaymentEvent;
import com.sromip.common.event.PaymentEventType;
import com.sromip.common.event.InvestmentCompletedEvent;
import com.sromip.investment.entity.Investment;
import com.sromip.investment.repository.InvestmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentCompletedListener {

    private final InvestmentRepository investmentRepository;
    private final KafkaTemplate<String, InvestmentCompletedEvent> kafkaTemplate;

    private static final String INVESTMENT_COMPLETED_TOPIC = "investment-completed-topic";

    @KafkaListener(topics = "payment-topic", groupId = "investment-group")
    public void consume(PaymentEvent event) {

        try {

            if (event.getEventType() != PaymentEventType.PAYMENT_COMPLETED) {
                return;
            }

            log.info("💰 PAYMENT_COMPLETED received for {}", event.getUserEmail());

            double spare = event.getSpareAmount();

            if (spare <= 0) {
                log.info("No spare amount to invest.");
                return;
            }

            Investment investment = new Investment();
            investment.setUserEmail(event.getUserEmail());
            investment.setAmount(spare);

            investmentRepository.save(investment);

            log.info("💾 Investment stored");

            Long paymentId = Long.valueOf(event.getTransactionId());

            InvestmentCompletedEvent completed =
                    new InvestmentCompletedEvent(
                            paymentId,
                            event.getUserEmail(),
                            spare
                    );

            kafkaTemplate.send(INVESTMENT_COMPLETED_TOPIC, completed);

            log.info("📤 INVESTMENT_COMPLETED emitted");

        } catch (Exception e) {

            log.error("Investment listener failed", e);

        }
    }
}