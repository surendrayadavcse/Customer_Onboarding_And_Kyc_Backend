package com.kyc.onboarding.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kyc.onboarding.model.Consent;

public interface ConsentRepository extends JpaRepository<Consent, Integer> {

	Consent findByUserId(int userId);

}
