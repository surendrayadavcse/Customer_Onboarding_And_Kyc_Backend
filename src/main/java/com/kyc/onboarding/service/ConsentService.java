package com.kyc.onboarding.service;

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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConsentRepository consentRepository;

    public Consent submitConsent(int userId, boolean consentGiven) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User with id " + userId + " not found"
                ));

        Consent consent = consentRepository.findByUserId(userId);
        if (consent == null) {
            consent = new Consent();
            consent.setUser(user);
        }

        consent.setConsentGiven(consentGiven);
        consent.setCompletedAt(LocalDateTime.now());

        return consentRepository.save(consent);
    }
}
