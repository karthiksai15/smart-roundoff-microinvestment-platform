package com.sromip.notification.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;
    private String message;

    // 🔹 Stage 11.4 additions
    private String channel;   // EMAIL / SMS / PUSH
    private String status;    // SENT / FAILED

    private LocalDateTime createdAt = LocalDateTime.now();
}
