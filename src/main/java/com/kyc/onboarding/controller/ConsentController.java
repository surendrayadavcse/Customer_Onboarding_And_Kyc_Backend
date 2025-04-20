package com.kyc.onboarding.controller;

import com.kyc.onboarding.service.ConsentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/consent")
@CrossOrigin(origins = "*")
public class ConsentController {

    @Autowired
    private ConsentService consentService;

    @PostMapping("/submit")
    public ResponseEntity<?> submitConsent(@RequestBody Map<String, Object> payload) {
        try {
            int userId = (int) payload.get("userId");
            boolean consentGiven = (boolean) payload.get("consentGiven");

            consentService.submitConsent(userId, consentGiven);

            return ResponseEntity.ok(Map.of("message", "Consent submitted successfully"));
        } catch (ResponseStatusException ex) {
            return ResponseEntity
                    .status(ex.getStatusCode())
                    .body(Map.of("error", ex.getReason()));
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Something went wrong"));
        }
    }
}
