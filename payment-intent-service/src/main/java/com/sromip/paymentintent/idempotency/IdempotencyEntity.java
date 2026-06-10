package com.sromip.paymentintent.idempotency;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "idempotency_keys")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdempotencyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String idempotencyKey;

    private String requestHash;

    @Column(columnDefinition = "TEXT")
    private String response;

    private String status; // PROCESSING, COMPLETED

    private LocalDateTime createdAt;
}