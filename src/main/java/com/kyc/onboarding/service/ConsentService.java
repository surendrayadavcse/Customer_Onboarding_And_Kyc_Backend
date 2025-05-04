package com.kyc.onboarding.service;

import com.kyc.onboarding.exception.UserNotFoundException;
import com.kyc.onboarding.model.Consent;
import com.kyc.onboarding.model.User;
import com.kyc.onboarding.repository.ConsentRepository;
import com.kyc.onboarding.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Service
public class ConsentService {

    private static final String USER_NOT_FOUND_MSG = "User with id ";
    private static final String USER_NOT_FOUND_SUFFIX = " not found";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConsentRepository consentRepository;

    public Consent submitConsent(int userId, boolean consentGiven) {
        // Check if the user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MSG + userId + USER_NOT_FOUND_SUFFIX));

        // Retrieve or create the consent
        Consent consent = consentRepository.findByUserId(userId);
        if (consent == null) {
            consent = new Consent();
            consent.setUser(user);
        }

        consent.setConsentGiven(consentGiven);
        consent.setCompletedAt(LocalDateTime.now());
        
        // Save consent and return
        return consentRepository.save(consent);
    }

    public Boolean getConsentStatus(int userId) {
        // Check if the user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MSG + userId + USER_NOT_FOUND_SUFFIX));

        // Check if consent exists
        Consent consent = consentRepository.findByUserId(userId);
        if (consent == null) {
            throw new UserNotFoundException(USER_NOT_FOUND_MSG + userId + USER_NOT_FOUND_SUFFIX);
        }

        return consent.isConsentGiven();
    }
}
