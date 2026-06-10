package com.sromip.payment.scheduler;

import com.sromip.payment.entity.Payment;
import com.sromip.payment.entity.PaymentStatus;
import com.sromip.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OtpTimeoutScheduler {

    private final PaymentRepository paymentRepository;

    @Scheduled(fixedRate = 60000)
    public void checkExpiredOtps() {

        log.info("Running OTP expiry check...");

        List<Payment> payments = paymentRepository.findByStatus(PaymentStatus.PENDING_OTP);

        int expiredCount = 0;

        for (Payment payment : payments) {

            Long expiry = payment.getOtpExpiryTime();

            if (expiry == null) continue;
            if (payment.isOtpVerified()) continue;

            if (System.currentTimeMillis() > expiry) {

                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);

                expiredCount++;

                log.warn("OTP expired → payment FAILED paymentId={}",
                        payment.getPaymentId());
            }
        }

        log.info("OTP expiry check completed. Expired payments={}", expiredCount);
    }
}