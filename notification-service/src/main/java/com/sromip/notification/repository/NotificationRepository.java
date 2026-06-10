package com.sromip.notification.repository;

import com.sromip.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserEmail(String userEmail);

    // IDEMPOTENCY SUPPORT
    Optional<Notification> findByPaymentIdAndMessage(String paymentId, String message);
}