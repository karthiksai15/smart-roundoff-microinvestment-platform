package com.sromip.investment.listener;

import com.sromip.common.event.PaymentEvent;
import com.sromip.common.event.PaymentEventType;
import com.sromip.investment.dto.AddInvestmentRequest;
import com.sromip.investment.service.InvestmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEventListener {

    private final InvestmentService investmentService;

    @KafkaListener(
            topics = "payment-events",
            groupId = "investment-service-group"
    )
    public void handlePaymentEvent(PaymentEvent event) {

        // ✅ process only successful payments
        if (event.getEventType() != PaymentEventType.PAYMENT_SUCCESS) {
            return;
        }

        log.info("💰 Investment Service received payment event");
        log.info("User: {}", event.getUserEmail());
        log.info("Spare Amount to Invest: {}", event.getSpareAmount());

        AddInvestmentRequest req = new AddInvestmentRequest();
        req.setUserEmail(event.getUserEmail());
        req.setSpareAmount(event.getSpareAmount());

        investmentService.addInvestment(req);

        log.info("✅ Auto-investment completed for {}", event.getUserEmail());
    }
}
