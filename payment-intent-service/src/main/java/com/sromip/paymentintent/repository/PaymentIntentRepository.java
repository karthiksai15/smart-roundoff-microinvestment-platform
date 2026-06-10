package com.sromip.paymentintent.repository;

import java.util.Optional;

import com.sromip.paymentintent.entity.PaymentIntent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentIntentRepository extends JpaRepository<PaymentIntent, UUID> {

    Optional<PaymentIntent> findTopByUserEmailOrderByCreatedAtDesc(String userEmail);

    // 🔥 NEW (IMPORTANT)
    Optional<PaymentIntent> findByPaymentId(String paymentId);
}