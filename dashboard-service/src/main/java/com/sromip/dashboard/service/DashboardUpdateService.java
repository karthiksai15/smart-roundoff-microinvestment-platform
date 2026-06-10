package com.sromip.dashboard.service;

import com.sromip.dashboard.dto.PipelineStatusResponse;
import com.sromip.dashboard.dto.PipelineView;
import com.sromip.dashboard.entity.DashboardTransaction;
import com.sromip.dashboard.repository.DashboardTransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardUpdateService {

    private final DashboardTransactionRepository repo;

    // 🔥 SAFE CREATE (NO OVERWRITE BUG)
    @Transactional
    private DashboardTransaction getOrCreate(String paymentId, String email) {

        return repo.findByPaymentId(paymentId)
                .orElseGet(() -> {
                    DashboardTransaction tx = new DashboardTransaction();

                    tx.setPaymentId(paymentId);
                    tx.setUserEmail(email);

                    // ✅ FIX: use neutral defaults
                    tx.setPaymentStatus("PENDING");
                    tx.setFraudStatus("UNKNOWN");   // 🔥 FIX
                    tx.setInvestmentStatus("PENDING");
                    tx.setNotificationStatus("PENDING");

                    tx.setLastUpdated(LocalDateTime.now());

                    return repo.save(tx);
                });
    }

    // ================= PAYMENT =================
    @Transactional
    public void updatePayment(String paymentId, String email, String paymentStatus, String fraudStatus) {

        DashboardTransaction tx = getOrCreate(paymentId, email);

        // ✅ ALWAYS UPDATE (source of truth)
        tx.setPaymentStatus(paymentStatus);
        tx.setFraudStatus(fraudStatus);

        tx.setLastUpdated(LocalDateTime.now());

        repo.save(tx);

        log.info("Dashboard updated PAYMENT {}", paymentId);
    }

    // ================= INVESTMENT =================
    @Transactional
    public void updateInvestment(String paymentId, String email, Double amount) {

        DashboardTransaction tx = getOrCreate(paymentId, email);

        // ✅ DO NOT TOUCH FRAUD / PAYMENT
        tx.setInvestmentStatus("EXECUTED");
        tx.setInvestedAmount(amount);

        tx.setLastUpdated(LocalDateTime.now());

        repo.save(tx);

        log.info("Dashboard updated INVESTMENT {}", paymentId);
    }

    // ================= NOTIFICATION =================
    @Transactional
    public void updateNotification(String paymentId, String email) {

        DashboardTransaction tx = getOrCreate(paymentId, email);

        // ✅ DO NOT TOUCH OTHER FIELDS
        tx.setNotificationStatus("SENT");

        tx.setLastUpdated(LocalDateTime.now());

        repo.save(tx);

        log.info("Dashboard updated NOTIFICATION {}", paymentId);
    }

    // ================= API =================
    public PipelineStatusResponse getPipelineStatus(String paymentId) {

        DashboardTransaction tx = repo.findByPaymentId(paymentId).orElse(null);

        if (tx == null) {
            return new PipelineStatusResponse("NOT_FOUND","NOT_FOUND","NOT_FOUND","NOT_FOUND");
        }

        return new PipelineStatusResponse(
                tx.getPaymentStatus(),
                tx.getFraudStatus(),
                tx.getInvestmentStatus(),
                tx.getNotificationStatus()
        );
    }

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
}