package com.kyc.onboarding.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.kyc.onboarding.model.VerificationLog;
import java.util.List;

@Repository
public interface VerificationLogRepository extends JpaRepository<VerificationLog, Integer> {
    List<VerificationLog> findByUserId(int userId);
}
