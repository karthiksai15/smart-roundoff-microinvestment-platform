package com.sromip.dashboard.repository;

import com.sromip.dashboard.entity.DashboardTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DashboardTransactionRepository
        extends JpaRepository<DashboardTransaction, Long> {

    Optional<DashboardTransaction> findByPaymentId(String paymentId);

    Optional<DashboardTransaction> findTopByUserEmailOrderByPaymentIdDesc(String userEmail);

    long countByFraudStatus(String status);
}