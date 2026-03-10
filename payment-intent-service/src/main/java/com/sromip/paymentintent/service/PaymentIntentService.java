package com.sromip.paymentintent.service;

import com.sromip.common.event.PaymentIntentEvent;
import com.sromip.paymentintent.dto.PaymentIntentRequest;
import com.sromip.paymentintent.dto.PaymentIntentResponse;
import com.sromip.paymentintent.entity.PaymentIntent;
import com.sromip.paymentintent.repository.PaymentIntentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentIntentService {

    private final PaymentIntentRepository paymentIntentRepository;
    private final KafkaTemplate<String, PaymentIntentEvent> kafkaTemplate;

    private static final String INTENT_TOPIC = "payment-intent-topic";

    public PaymentIntentResponse createIntent(PaymentIntentRequest request) {

        log.info("💳 Creating payment intent for {}", request.getUserEmail());

        double originalAmount = request.getAmount();
        double roundoffPreview = Math.ceil(originalAmount);
        double spareAmount = roundoffPreview - originalAmount;

        // ==============================
        // 1️⃣ SAVE INTENT
        // ==============================

        PaymentIntent intent = PaymentIntent.builder()
                .userEmail(request.getUserEmail())
                .requestedAmount(originalAmount)
                .currency("INR")
                .status("CREATED")
                .createdAt(LocalDateTime.now())
                .roundoffPreview(roundoffPreview)
                .spareAmountPreview(spareAmount)
                .build();

        PaymentIntent savedIntent = paymentIntentRepository.save(intent);

        log.info("💾 PaymentIntent stored with id={}", savedIntent.getId());

        // ==============================
        // 2️⃣ SEND EVENT TO KAFKA
        // ==============================

        PaymentIntentEvent event = new PaymentIntentEvent(
                savedIntent.getId(),
                savedIntent.getUserEmail(),
                savedIntent.getRequestedAmount(),
                savedIntent.getRoundoffPreview(),
                savedIntent.getSpareAmountPreview(),
                savedIntent.getStatus(),
                savedIntent.getCreatedAt()
        );

        kafkaTemplate.send(INTENT_TOPIC, event);

        log.info("📤 PaymentIntentEvent sent to Kafka");

        // ==============================
        // 3️⃣ RESPONSE
        // ==============================

        return PaymentIntentResponse.builder()
                .intentId(savedIntent.getId())
                .amount(originalAmount)
                .roundoffPreview(roundoffPreview)
                .spareAmount(spareAmount)
                .status(savedIntent.getStatus())
                .build();
    }
}