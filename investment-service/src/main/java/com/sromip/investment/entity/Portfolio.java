package com.sromip.investment.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "portfolio")
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;
    private double totalInvestment;
}

