package com.kyc.onboarding.controller;

import com.kyc.onboarding.service.KycDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/kyc")
public class KycDocumentController {

    @Autowired
    private KycDocumentService kycDocumentService;

    @PostMapping("/uploadaadhar")
    public ResponseEntity<String> uploadAadhar(
            @RequestParam("userId") int userId,
            @RequestParam("file") MultipartFile aadharImage) {

        String result = kycDocumentService.uploadAadhar(userId, aadharImage);

        return switch (result) {
            case "SKIPPED" -> ResponseEntity.badRequest().body("Aadhar already verified. Skipping re-verification.");
            case "FAILED" -> ResponseEntity.badRequest().body("Aadhar OCR verification failed. Please upload a clearer image.");
            default -> ResponseEntity.ok("Aadhar uploaded and verified successfully!");
        };
    }

    @PostMapping("/uploadpan")
    public ResponseEntity<String> uploadPan(
            @RequestParam("userId") int userId,
            @RequestParam("file") MultipartFile panImage) {

        String result = kycDocumentService.uploadPan(userId, panImage);

        return switch (result) {
            case "SKIPPED" -> ResponseEntity.badRequest().body("PAN already verified. Skipping re-verification.");
            case "FAILED" -> ResponseEntity.badRequest().body("OCR failed: Invalid PAN image or unreadable content.");
            default -> ResponseEntity.ok("PAN uploaded and verified successfully!");
        };
    }

    @PostMapping("/uploadselfie")
    public ResponseEntity<String> uploadSelfie(
            @RequestParam("userId") int userId,
            @RequestParam("file") MultipartFile selfieImage) {

        String result = kycDocumentService.uploadSelfie(userId, selfieImage);
        return "SUCCESS".equals(result)
                ? ResponseEntity.ok("Selfie uploaded successfully!")
                : ResponseEntity.badRequest().body("Failed to upload selfie.");
    }
}
