package com.kyc.onboarding.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kyc.onboarding.model.KycDocument;

public interface KycDocumentsRepository extends JpaRepository<KycDocument, Integer> {
	
	Optional<KycDocument> findByUserId(int userId);


}
