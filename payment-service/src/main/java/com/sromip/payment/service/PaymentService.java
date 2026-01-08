package com.sromip.payment.service;

import com.sromip.common.event.PaymentEvent;
import com.sromip.common.event.PaymentEventType;
import com.sromip.payment.dto.PaymentRequest;
import com.sromip.payment.entity.Payment;
import com.sromip.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    public Payment processPayment(PaymentRequest request) {

        // 1️⃣ Calculate round-off
        double original = request.getAmount();
        double rounded = Math.ceil(original / 100) * 100;
        double spare = rounded - original;

        // 2️⃣ Save payment in DB
        Payment payment = new Payment();
        payment.setUserEmail(request.getUserEmail());
        payment.setOriginalAmount(original);
        payment.setRoundedAmount(rounded);
        payment.setSpareAmount(spare);
        payment.setStatus("SUCCESS");

        Payment saved = paymentRepository.save(payment);

        // 3️⃣ Build Kafka event (MATCHES common-libs CONSTRUCTOR)
        PaymentEvent event = new PaymentEvent(
                PaymentEventType.PAYMENT_SUCCESS,
                saved.getId().toString(),      // transactionId ✅
                saved.getUserEmail(),          // userEmail ✅
                saved.getOriginalAmount(),     // amount ✅
                saved.getSpareAmount(),        // spareAmount ✅
                LocalDateTime.now()            // eventTime ✅
        );

        // 4️⃣ Publish to Kafka
        kafkaTemplate.send("payment-events", event);

        return saved;
    }
}
