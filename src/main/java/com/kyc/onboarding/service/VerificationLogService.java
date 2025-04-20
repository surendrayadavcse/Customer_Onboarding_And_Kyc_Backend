package com.kyc.onboarding.service;

import com.kyc.onboarding.model.User;
import com.kyc.onboarding.model.VerificationLog;
import com.kyc.onboarding.repository.VerificationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
}
