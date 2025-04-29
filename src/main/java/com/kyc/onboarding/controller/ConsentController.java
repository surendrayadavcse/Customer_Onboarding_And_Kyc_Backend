package com.kyc.onboarding.controller;

import com.kyc.onboarding.dto.ConsentRequestDTO;
import com.kyc.onboarding.service.ConsentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/consent")
@CrossOrigin(origins = "*")
public class ConsentController {

    @Autowired
    private ConsentService consentService;

    @PostMapping("/submit")
    public ResponseEntity<?> submitConsent(@RequestBody ConsentRequestDTO consentRequestDTO) {
        consentService.submitConsent(consentRequestDTO.getUserId(), consentRequestDTO.isConsentGiven());
        return ResponseEntity.ok(Map.of("message", "Consent submitted successfully"));
    }
    @GetMapping("/status/{userId}")
    public ResponseEntity<?> getConsentStatus(@PathVariable int userId) {
        boolean consentGiven = consentService.getConsentStatus(userId);
        return ResponseEntity.ok(Map.of("consentGiven", consentGiven));
    }

}
