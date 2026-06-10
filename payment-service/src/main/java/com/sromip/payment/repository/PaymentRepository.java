package com.sromip.payment.repository;

import java.util.List;
import com.sromip.payment.entity.Payment;
import com.sromip.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findTopByUserEmailOrderByIdDesc(String userEmail);

    Optional<Payment> findByPaymentId(String paymentId);

    List<Payment> findByStatus(PaymentStatus status);
}