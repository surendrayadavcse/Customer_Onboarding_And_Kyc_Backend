package com.kyc.onboarding.controller;

import com.kyc.onboarding.service.KycDocumentService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/kyc")
public class KycDocumentController {

    @Autowired
    private KycDocumentService kycDocumentService;

@PostMapping("/upload-aadhar")
public ResponseEntity<String> uploadAadhar(
        @RequestParam("userId") int userId,
        @RequestParam("file") MultipartFile aadharImage) {
    try {
    	 String result = kycDocumentService.uploadAadhar(userId, aadharImage);
    	 System.out.println(result+"i am result ");
         switch (result) {
             case "SKIPPED":
                 return ResponseEntity.badRequest().body("Aadhar already verified. Skipping re-verification.");
             case "FAILED":
                 return ResponseEntity.badRequest().body("Aadhar OCR verification failed. Please upload a clearer image.");
             case "SUCCESS":
             default:
                 return ResponseEntity.ok("Aadhar uploaded and verified successfully!");
         }
    } catch (Exception e) {
        return ResponseEntity.badRequest().body("Failed to upload Aadhar: " + e.getMessage());
    }
}

@PostMapping("/upload-pan")
public ResponseEntity<String> uploadPan(
        @RequestParam("userId") int userId,
        @RequestParam("file") MultipartFile panImage) {
    try {
        String result = kycDocumentService.uploadPan(userId, panImage);

        switch (result) {
            case "SKIPPED":
                return ResponseEntity.badRequest().body("PAN already verified. Skipping re-verification.");
            case "FAILED":
                return ResponseEntity.badRequest().body("OCR failed: Invalid PAN image or unreadable content.");
            case "SUCCESS":
            default:
                return ResponseEntity.ok("PAN uploaded and verified successfully!");
        } 

    } catch (Exception e) {
        return ResponseEntity.badRequest().body("Failed to upload PAN: " + e.getMessage());
    }
}
@PostMapping("/upload-selfie")
public ResponseEntity<String> uploadSelfie(
        @RequestParam("userId") int userId,
        @RequestParam("file") MultipartFile selfieImage) {
    try {
        String result = kycDocumentService.uploadSelfie(userId, selfieImage);

        if ("SUCCESS".equals(result)) {
            return ResponseEntity.ok("Selfie uploaded successfully!");
        } else {
            return ResponseEntity.badRequest().body("Failed to upload selfie.");
        }
    } catch (Exception e) {
        return ResponseEntity.badRequest().body("Selfie upload failed: " + e.getMessage());
    }
}




}
