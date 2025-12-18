package com.sromip.fraud.repository;

import com.sromip.fraud.entity.FraudCheck;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FraudCheckRepository extends JpaRepository<FraudCheck, Long> {
}
