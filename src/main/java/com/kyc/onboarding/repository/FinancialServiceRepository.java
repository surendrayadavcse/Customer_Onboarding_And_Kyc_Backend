 package com.kyc.onboarding.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kyc.onboarding.model.FinancialService;

public interface FinancialServiceRepository extends JpaRepository<FinancialService, Integer> {
	boolean existsByServiceName(String serviceName);
}
