package com.sromip.dashboard.service;

import com.sromip.dashboard.dto.PipelineStatusResponse;
import com.sromip.dashboard.dto.PipelineView;
import com.sromip.dashboard.entity.DashboardTransaction;
import com.sromip.dashboard.repository.DashboardTransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardUpdateService {

    private final DashboardTransactionRepository repo;

    // =====================================================
    // FIND TRANSACTION
    // =====================================================

    private DashboardTransaction findTransaction(Long paymentId) {
        return repo.findByPaymentId(paymentId).orElse(null);
    }

    // =====================================================
    // CREATE TRANSACTION SAFELY
    // =====================================================

    private DashboardTransaction createTransaction(Long paymentId, String email) {

        DashboardTransaction existing = repo.findByPaymentId(paymentId).orElse(null);

        if (existing != null) {
            return existing;
        }

        DashboardTransaction tx = new DashboardTransaction();

        tx.setPaymentId(paymentId);
        tx.setUserEmail(email);

        tx.setPaymentStatus("PENDING");
        tx.setFraudStatus("PENDING");
        tx.setInvestmentStatus("PENDING");
        tx.setNotificationStatus("PENDING");

        tx.setLastUpdated(LocalDateTime.now());

        return repo.save(tx);
    }

    // =====================================================
    // PAYMENT UPDATE
    // =====================================================

    public void updatePayment(Long paymentId, String email) {

        DashboardTransaction tx = findTransaction(paymentId);

        if (tx == null) {
            tx = createTransaction(paymentId, email);
        }

        tx.setPaymentStatus("COMPLETED");
        tx.setLastUpdated(LocalDateTime.now());

        repo.save(tx);
    }

    // =====================================================
    // FRAUD UPDATE
    // =====================================================

    public void updateFraud(Long paymentId, String email, String status, Double riskScore) {

        DashboardTransaction tx = findTransaction(paymentId);

        if (tx == null) {
            tx = createTransaction(paymentId, email);
        }

        tx.setFraudStatus(status);
        tx.setMlRiskScore(riskScore);
        tx.setLastUpdated(LocalDateTime.now());

        repo.save(tx);
    }

    // =====================================================
    // INVESTMENT UPDATE
    // =====================================================

    public void updateInvestment(Long paymentId, String email, Double amount) {

        DashboardTransaction tx = findTransaction(paymentId);

        if (tx == null) {
            tx = createTransaction(paymentId, email);
        }

        tx.setInvestmentStatus("EXECUTED");
        tx.setInvestedAmount(amount);
        tx.setLastUpdated(LocalDateTime.now());

        repo.save(tx);

        log.info("Investment pipeline updated for {}", paymentId);
    }

    // =====================================================
    // NOTIFICATION UPDATE
    // =====================================================

    public void updateNotification(Long paymentId, String email) {

        DashboardTransaction tx;

        if (paymentId != null) {
            tx = repo.findByPaymentId(paymentId).orElse(null);
        } else {
            tx = repo.findTopByUserEmailOrderByPaymentIdDesc(email).orElse(null);
        }

        if (tx == null) {
            log.warn("No dashboard transaction found for {}", email);
            return;
        }

        tx.setNotificationStatus("SENT");
        tx.setLastUpdated(LocalDateTime.now());

        repo.save(tx);
    }

    // =====================================================
    // PIPELINE STATUS
    // =====================================================

    public PipelineStatusResponse getPipelineStatus(Long paymentId) {

        DashboardTransaction tx = repo.findByPaymentId(paymentId).orElse(null);

        if (tx == null) {

            return new PipelineStatusResponse(
                    "NOT_FOUND",
                    "NOT_FOUND",
                    "NOT_FOUND",
                    "NOT_FOUND"
            );

        }

        return new PipelineStatusResponse(
                tx.getPaymentStatus(),
                tx.getFraudStatus(),
                tx.getInvestmentStatus(),
                tx.getNotificationStatus()
        );
    }

    // =====================================================
    // METRICS
    // =====================================================

    public long getTotalTransactions() {
        return repo.count();
    }

    public long getApprovedTransactions() {
        return repo.countByFraudStatus("APPROVED");
    }

    public long getBlockedTransactions() {
        return repo.countByFraudStatus("BLOCKED");
    }

    public Double getTotalInvested() {

        return repo.findAll()
                .stream()
                .map(tx -> tx.getInvestedAmount() == null ? 0.0 : tx.getInvestedAmount())
                .reduce(0.0, Double::sum);

    }

    // =====================================================
    // PIPELINE VIEW
    // =====================================================

    public List<PipelineView> getAllPipelines() {

        return repo.findAll()
                .stream()
                .map(tx -> new PipelineView(
                        tx.getPaymentId(),
                        tx.getPaymentStatus(),
                        tx.getFraudStatus(),
                        tx.getInvestmentStatus(),
                        tx.getNotificationStatus()
                ))
                .toList();
    }
}