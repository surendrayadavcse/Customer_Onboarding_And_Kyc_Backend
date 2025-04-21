package com.kyc.onboarding.service;

import com.kyc.onboarding.model.User;
import com.kyc.onboarding.model.VerificationLog;
import com.kyc.onboarding.repository.VerificationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VerificationLogService {

    @Autowired
    private VerificationLogRepository logRepository;

    public void logAttempt(User user, String type, String status, String message) {
        VerificationLog log = new VerificationLog();
        log.setUser(user);
        log.setVerificationType(type);
        log.setStatus(status);
        log.setMessage(message);
        log.setAttemptedAt(LocalDateTime.now());

        logRepository.save(log);
    }

    public List<VerificationLog> getLogsByUser(int userId) {
        List<VerificationLog> logs = logRepository.findByUserId(userId);
        System.out.println(logs+"i am from logs");
        return logs != null ? logs : List.of(); // if null, return empty list
    }
    public boolean hasSuccessfulVerification(int userId, String verificationType) {
        return logRepository.existsByUserIdAndVerificationTypeIgnoreCaseAndStatusIgnoreCase(userId, verificationType, "SUCCESS");
    }
    public boolean isDocumentAlreadyVerified(int userId, String verificationType) {
        return logRepository.existsByUserIdAndVerificationTypeIgnoreCaseAndStatusIgnoreCase(
            userId,
            verificationType,
            "SUCCESS"
        );
    }
}
