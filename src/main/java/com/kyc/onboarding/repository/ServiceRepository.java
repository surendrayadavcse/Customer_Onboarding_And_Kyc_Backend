package com.kyc.onboarding.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.kyc.onboarding.model.FinancialService;


public interface ServiceRepository extends JpaRepository<FinancialService, Integer> {

}
