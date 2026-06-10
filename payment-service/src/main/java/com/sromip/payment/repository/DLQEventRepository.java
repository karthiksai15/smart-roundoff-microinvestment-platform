package com.sromip.payment.repository;

import com.sromip.payment.entity.DLQEvent;
import com.sromip.payment.entity.DLQStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DLQEventRepository extends JpaRepository<DLQEvent, UUID> {

    List<DLQEvent> findByStatusAndNextRetryAtBefore(
            DLQStatus status,
            LocalDateTime time
    );

    // 🔥 NEW (IMPORTANT)
    Optional<DLQEvent> findByPaymentId(String paymentId);
}