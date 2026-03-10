package com.sromip.fraud.repository;

import com.sromip.fraud.entity.RiskScore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiskScoreRepository extends JpaRepository<RiskScore, Long> {
}
