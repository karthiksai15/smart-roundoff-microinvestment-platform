package com.sromip.investment.repository;

import com.sromip.investment.entity.Investment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvestmentRepository extends JpaRepository<Investment, Long> {

    List<Investment> findByUserEmail(String email);

    // ✅ FIX: IDEMPOTENCY SUPPORT
    Optional<Investment> findByPaymentId(String paymentId);
}