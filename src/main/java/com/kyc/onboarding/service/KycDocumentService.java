package com.kyc.onboarding.service;

import com.kyc.onboarding.exception.*;
import com.kyc.onboarding.model.KycDocument;
import com.kyc.onboarding.model.User;
import com.kyc.onboarding.ocr.OcrService;
import com.kyc.onboarding.repository.KycDocumentsRepository;
import com.kyc.onboarding.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.UUID;
import java.util.regex.*;

@Service
public class KycDocumentService {

    @Autowired private UserRepository userRepository;
    @Autowired private KycDocumentsRepository kycDocumentRepository;
    @Autowired private OcrService ocrService;
    @Autowired private VerificationLogService logService;
    @Autowired private UserService userService;

    public String uploadAadhar(int userId, MultipartFile aadharImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        KycDocument kyc = user.getKycDocument();
        if (kyc == null) {
            kyc = new KycDocument();
            kyc.setUser(user);
        }

        if (logService.isDocumentAlreadyVerified(userId, "AADHAR")) {
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
            throw new DocumentStorageException("Failed to store Aadhar document");
        }

        try {
            String extractedText = ocrService.extractTextFromImage(aadharImage);
            if (!isValidAadharText(extractedText)) {
                logService.logAttempt(user, "AADHAR", "FAILED", "OCR error: Pattern mismatch or invalid Aadhar number");
                return "FAILED";
            }
            kycDocumentRepository.save(kyc);
            logService.logAttempt(user, "AADHAR", "SUCCESS", "Aadhar verified via OCR");
            userService.checkAndUpdateKycStatus(user);
        } catch (Exception e) {
            throw new OcrVerificationException("OCR verification failed for Aadhar");
        }

        return "SUCCESS";
    }

    public String uploadPan(int userId, MultipartFile panImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        KycDocument kyc = user.getKycDocument();
        if (kyc == null) {
            kyc = new KycDocument();
            kyc.setUser(user);
        }

        if (logService.isDocumentAlreadyVerified(userId, "PAN")) {
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
            throw new DocumentStorageException("Failed to store PAN document");
        }

        try {
            String extractedText = ocrService.extractTextFromImage(panImage);
            if (!isValidPanText(extractedText)) {
                logService.logAttempt(user, "PAN", "FAILED", "OCR error: Pattern mismatch or invalid PAN number");
                return "FAILED";
            }
            kycDocumentRepository.save(kyc);
            logService.logAttempt(user, "PAN", "SUCCESS", "PAN verified via OCR");
            userService.checkAndUpdateKycStatus(user);
        } catch (Exception e) {
            throw new OcrVerificationException("OCR verification failed for PAN");
        }

        return "SUCCESS";
    }

    public String uploadSelfie(int userId, MultipartFile selfieImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

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
            userService.checkAndUpdateKycStatus(user);
            return "SUCCESS";
        } catch (Exception e) {
            logService.logAttempt(user, "SELFIE", "FAILED", "Failed to upload selfie: " + e.getMessage());
            throw new DocumentStorageException("Selfie upload failed");
        }
    }

    // --- Helper Methods ---
    private boolean isValidAadharText(String text) {
        return Pattern.compile("\\d{4}\\s?\\d{4}\\s?\\d{4}").matcher(text).find();
    }

    private boolean isValidPanText(String text) {
        return Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]").matcher(text).find();
    }
}
