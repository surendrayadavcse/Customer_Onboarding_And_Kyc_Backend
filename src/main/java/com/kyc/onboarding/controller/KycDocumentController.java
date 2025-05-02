package com.kyc.onboarding.controller;

import com.kyc.onboarding.dto.KycDocumentResponse;
import com.kyc.onboarding.service.KycDocumentService;

import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<Map<String, String>> uploadAadhar(
            @RequestParam("userId") int userId,
            @RequestParam("file") MultipartFile aadharImage) {

        String extractedAadhar = kycDocumentService.uploadAadhar(userId, aadharImage);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Aadhar OCR validated, awaiting OTP verification.");
        response.put("extractedText", extractedAadhar);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/uploadpan")
    public ResponseEntity<Map<String, String>> uploadPan(
            @RequestParam("userId") int userId,
            @RequestParam("file") MultipartFile panImage) {

        String extractedPan = kycDocumentService.uploadPan(userId, panImage);

        Map<String, String> response = new HashMap<>();
        response.put("message", "PAN OCR validated, awaiting OTP verification.");
        response.put("extractedText", extractedPan);
        return ResponseEntity.ok(response);
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
    @GetMapping("/getKycDetails/{userId}")
    public ResponseEntity<KycDocumentResponse> getKycDetails(@PathVariable("userId") int userId) {
        KycDocumentResponse response = kycDocumentService.getKycDocument(userId);
        return ResponseEntity.ok(response);
    }

}
