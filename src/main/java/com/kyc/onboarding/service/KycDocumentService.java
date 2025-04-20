package com.kyc.onboarding.service;

import com.kyc.onboarding.model.KycDocument;
import com.kyc.onboarding.model.User;
import com.kyc.onboarding.repository.KycDocumentsRepository;
import com.kyc.onboarding.repository.UserRepository;
import com.kyc.onboarding.ocr.OcrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class KycDocumentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KycDocumentsRepository kycDocumentRepository;

    @Autowired
    private OcrService ocrService;

    @Autowired
    private VerificationLogService logService;

    @Autowired
    private UserService userService;  // ✅ Inject UserService to call checkAndUpdateKycStatus

    public String uploadAadhar(int userId, MultipartFile aadharImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        KycDocument kyc = user.getKycDocument();
        if (kyc == null) {
            kyc = new KycDocument();
            kyc.setUser(user);
        }

        if (kyc.getAadhaarVerified() !=false && kyc.getAadhaarVerified()) {
            logService.logAttempt(user, "AADHAR", "SKIPPED", "Aadhar already verified. Skipping re-verification.");
            return "SKIPPED";
        }

        String uploadDir = "uploads/aadhar/";
        String fileName = UUID.randomUUID() + "_" + aadharImage.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);

        try {
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, aadharImage.getBytes());

            kyc.setAadharImage(filePath.toString());
            kycDocumentRepository.save(kyc);
        } catch (Exception e) {
            logService.logAttempt(user, "AADHAR", "FAILED", "Failed to store Aadhar document: " + e.getMessage());
            throw new RuntimeException("Failed to store Aadhar document", e);
        }

        try {
            String extractedText = ocrService.extractTextFromImage(aadharImage);

            if (!isValidAadharText(extractedText)) {
                logService.logAttempt(user, "AADHAR", "FAILED", "OCR error: Pattern mismatch or invalid Aadhar number");
                return "FAILED";
            } else {
                kyc.setAadharVerified(true);
                kycDocumentRepository.save(kyc);

                logService.logAttempt(user, "AADHAR", "SUCCESS", "Aadhar verified via OCR");
                
                userService.checkAndUpdateKycStatus(user);  // ✅ Update KYC status here
            }
        } catch (Exception e) {
            logService.logAttempt(user, "AADHAR", "FAILED", "OCR failed: " + e.getMessage());
            throw new RuntimeException("Failed during OCR verification", e);
        }

        return "SUCCESS";
    }

    public String uploadPan(int userId, MultipartFile panImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        KycDocument kyc = user.getKycDocument();
        if (kyc == null) {
            kyc = new KycDocument();
            kyc.setUser(user);
        }

        if (kyc.getPanVerified() != false && kyc.getPanVerified()) {
            logService.logAttempt(user, "PAN", "SKIPPED", "PAN already verified. Skipping re-verification.");
            return "SKIPPED";
        }

        String uploadDir = "uploads/pan/";
        String fileName = UUID.randomUUID() + "_" + panImage.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);

        try {
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, panImage.getBytes());

            kyc.setPanImage(filePath.toString());
            kycDocumentRepository.save(kyc);
        } catch (Exception e) {
            logService.logAttempt(user, "PAN", "FAILED", "Failed to store PAN document: " + e.getMessage());
            throw new RuntimeException("Failed to store PAN document", e);
        }

        try {
            String extractedText = ocrService.extractTextFromImage(panImage);

            if (!isValidPanText(extractedText)) {
                logService.logAttempt(user, "PAN", "FAILED", "OCR error: Pattern mismatch or invalid PAN number");
                return "FAILED";
            } else {
                kyc.setPanVerified(true);
                kycDocumentRepository.save(kyc);

                logService.logAttempt(user, "PAN", "SUCCESS", "PAN verified via OCR");
                
                userService.checkAndUpdateKycStatus(user);  // ✅ Update KYC status here
            }
        } catch (Exception e) {
            logService.logAttempt(user, "PAN", "FAILED", "OCR failed: " + e.getMessage());
            throw new RuntimeException("Failed during PAN OCR verification", e);
        }

        return "SUCCESS";
    }

    public String uploadSelfie(int userId, MultipartFile selfieImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        KycDocument kyc = user.getKycDocument();
        if (kyc == null) {
            kyc = new KycDocument();
            kyc.setUser(user);
        }

        String uploadDir = "uploads/selfie/";
        String fileName = UUID.randomUUID() + "_" + selfieImage.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);

        try {
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, selfieImage.getBytes());

            kyc.setSelfieImage(filePath.toString());
            kycDocumentRepository.save(kyc);

            logService.logAttempt(user, "SELFIE", "SUCCESS", "Selfie uploaded successfully.");

            userService.checkAndUpdateKycStatus(user);  // ✅ Update KYC status here

            return "SUCCESS";
        } catch (Exception e) {
            logService.logAttempt(user, "SELFIE", "FAILED", "Failed to upload selfie: " + e.getMessage());
            throw new RuntimeException("Selfie upload failed", e);
        }
    }

    // --- Helper Methods ---
    private boolean isValidAadharText(String extractedText) {
        String digitsOnly = extractedText.replaceAll("[^0-9]", "");
        if (digitsOnly.length() == 12) {
            return true;
        }
        Pattern pattern = Pattern.compile("\\d{4}\\s?\\d{4}\\s?\\d{4}");
        Matcher matcher = pattern.matcher(extractedText);
        return matcher.find();
    }

    private boolean isValidPanText(String text) {
        Pattern pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]");
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }
}
