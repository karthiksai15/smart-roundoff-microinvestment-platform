package com.sromip.payment.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;
    private double originalAmount;
    private double roundedAmount;
    private double spareAmount;
    private String status;
}
