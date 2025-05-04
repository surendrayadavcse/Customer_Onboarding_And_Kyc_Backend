package com.kyc.onboarding.service;

import com.kyc.onboarding.dto.KycDocumentResponse;
import com.kyc.onboarding.dto.SelfieResponse;
import com.kyc.onboarding.exception.*;
import com.kyc.onboarding.model.KycDocument;
import com.kyc.onboarding.model.User;
import com.kyc.onboarding.ocr.OcrService;
import com.kyc.onboarding.repository.KycDocumentsRepository;
import com.kyc.onboarding.repository.UserRepository;
import com.kyc.onboarding.security.EncryptionUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.Optional;
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
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        if (logService.isDocumentAlreadyVerified(userId, "AADHAR")) {
            logService.logAttempt(user, "AADHAR", "SKIPPED", "Aadhar already verified. Skipping re-verification.");
            throw new AadhaarAlreadyVerifiedException("Aadhar already verified. Skipping re-verification.");
        }

        String extractedText = ocrService.extractTextFromImage(aadharImage);
        if (!isValidAadharText(extractedText)) {
            logService.logAttempt(user, "AADHAR", "FAILED", "OCR content invalid for Aadhar context");
            throw new InvalidDocumentFormatException("Uploaded image does not appear to be a valid Aadhaar card");
        }

        String validAadhar = extractValidAadhar(extractedText);
        if (validAadhar == null) {
            logService.logAttempt(user, "AADHAR", "FAILED", "OCR error: Aadhar number not found");
            throw new InvalidDocumentFormatException("Could not extract a valid Aadhar number from the image");
        }

        logService.logAttempt(user, "AADHAR", "PENDING", "OCR validated, waiting for OTP verification");

        return validAadhar;
    }

    

    public String saveVerifiedAadhar(String email, MultipartFile aadharImage) {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found"));

    String uploadDir = "uploads/aadhar/";
    String fileName = UUID.randomUUID() + "_" + aadharImage.getOriginalFilename();
    Path filePath = Paths.get(uploadDir + fileName);

    storeDocument(filePath, aadharImage, user, "AADHAR");

    KycDocument kyc = user.getKycDocument();
    if (kyc == null) {
        kyc = new KycDocument();
        kyc.setUser(user);
        user.setKycDocument(kyc); 
    }

    kyc.setAadharImage(filePath.toString());
    String aadharNumber = uploadAadhar(user.getId(), aadharImage);
    String encryptedAadhar = EncryptionUtil.encrypt(aadharNumber); // Encrypt here
    kyc.setAadharNumber(encryptedAadhar); // Store encrypted value


    kycDocumentRepository.save(kyc);

    logService.logAttempt(user, "AADHAR", "SUCCESS", "Aadhar verified via OTP");
    userService.checkAndUpdateKycStatus(user);

    return "Aadhar Stored Successfully";
}




    public String uploadPan(int userId, MultipartFile panImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        if (logService.isDocumentAlreadyVerified(userId, "PAN")) {
            logService.logAttempt(user, "PAN", "SKIPPED", "PAN already verified. Skipping re-verification.");
            throw new PanAlreadyVerifiedException("PAN already verified. Skipping re-verification.");
        }

        String extractedText = ocrService.extractTextFromImage(panImage);
        if (!isValidPanText(extractedText)) {
            logService.logAttempt(user, "PAN", "FAILED", "OCR content invalid for PAN context");
            throw new InvalidDocumentFormatException("Uploaded image does not appear to be a valid PAN card");
        }

        String validPan = extractValidPan(extractedText);
        if (validPan == null) {
            logService.logAttempt(user, "PAN", "FAILED", "OCR error: PAN number not found");
            throw new InvalidDocumentFormatException("Could not extract a valid PAN number from the image");
        }

        logService.logAttempt(user, "PAN", "PENDING", "OCR 	, waiting for OTP verification");

        return validPan;
    }

    
    public String saveVerifiedPan(String email, MultipartFile panImage) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String uploadDir = "uploads/pan/";
        String fileName = UUID.randomUUID() + "_" + panImage.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);

        storeDocument(filePath, panImage, user, "PAN");

        KycDocument kyc = user.getKycDocument();
        if (kyc == null) {
            kyc = new KycDocument();
            kyc.setUser(user);
            user.setKycDocument(kyc);
        }

        kyc.setPanImage(filePath.toString());
        String panNumber = uploadPan(user.getId(), panImage);
        String encryptedPan = EncryptionUtil.encrypt(panNumber); // Encrypt here
        kyc.setPanNumber(encryptedPan); // Store PAN number as well
        kycDocumentRepository.save(kyc);

        logService.logAttempt(user, "PAN", "SUCCESS", "PAN verified and saved");
        userService.checkAndUpdateKycStatus(user);

        return "PAN Verification Successful";
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
    
    
    private void storeDocument(Path filePath, MultipartFile file, User user, String docType) {
        try {
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, file.getBytes());
        } catch (Exception e) {
            logService.logAttempt(user, docType, "FAILED", "Failed to store " + docType + " document: " + e.getMessage());
            throw new DocumentStorageException("Failed to store " + docType + " document");
        }
    }

    private String extractValidAadhar(String text) {
        Matcher matcher = Pattern.compile("\\d{4}\\s?\\d{4}\\s?\\d{4}").matcher(text);
        if (matcher.find()) {
            return matcher.group().replaceAll("\\s+", ""); // Return cleaned 12-digit Aadhar number
        }
        return null;
    }


    private String extractValidPan(String text) {
        Matcher matcher = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]").matcher(text);
        if (matcher.find()) {
            return matcher.group(); // Return matched PAN
        }
        return null;
    }
    
    private boolean isValidAadharText(String text) {
        text = text.toLowerCase();

        // Normalize text
        text = text.replaceAll("[^\\x00-\\x7F]", "")     // Remove non-ASCII junk
                   .replaceAll("\\s+", " ")              // Normalize all whitespace
                   .replaceAll("[^a-z0-9 /]", "")        // Remove punctuation
                   .trim();

       

        boolean hasGov = text.contains("government of india") || text.contains("govt of india") || text.contains("india");
        boolean hasAadhar = text.contains("aadhaar") || text.contains("unique identification");
        boolean hasGender = text.contains("male") || text.contains("female") || text.contains("transgender");
        boolean hasDob = text.contains("dob") || text.matches(".*\\d{2}/\\d{2}/\\d{4}.*");

        return hasGov  && hasGender && hasDob;
    }


    private boolean isValidPanText(String text) {
    	System.out.println(text.toLowerCase());
        text = text.toLowerCase();
        text = text.replaceAll("[^\\x00-\\x7F]", "")  // Remove non-ASCII junk
                .replaceAll("\\s+", " ")          // Replace all whitespace/newlines with single space
                .trim();

     System.out.println("Normalized Text: " + text);
        boolean hasGov = text.contains("income tax department") || text.contains("govt. of india");
        boolean hasPan = text.contains("permanent account number") || text.contains("pan");
        boolean hasName = text.matches(".*[a-zA-Z]{2,}\\s[a-zA-Z]{2,}.*");
        return hasGov && hasPan && hasName;
    }

    public KycDocumentResponse getKycDocument(int userId) {
        KycDocument document = kycDocumentRepository.findByUserId(userId)
            .orElseThrow(() -> new KycDocumentNotFoundException("KYC document not found for user ID: " + userId));

        // The base URL for serving the uploaded files
        String baseUrl = "http://localhost:9999/";

        // Normalize the paths to ensure proper file structure
        String normalizedAadharPath = document.getAadharImage().replace("\\", "/");
        String normalizedPanPath = document.getPanImage().replace("\\", "/");

     

        // Return the KycDocumentResponse with the fully qualified URLs
        return new KycDocumentResponse(
        		EncryptionUtil.decrypt(document.getAadharNumber()),
            baseUrl + normalizedAadharPath,
            EncryptionUtil.decrypt(document.getPanNumber()),
            baseUrl + normalizedPanPath
        );
    }


  


	public String getSelfie(int userId) {
		KycDocument document = kycDocumentRepository.findByUserId(userId)
	            .orElseThrow(() -> new KycDocumentNotFoundException("KYC document not found for user ID: " + userId));
	 String baseUrl = "http://localhost:9999/";

     // Normalize the paths to ensure proper file structure
     String normalizedSelfiePath= document.getSelfieImage().replace("\\", "/");
     return baseUrl+ normalizedSelfiePath;
	
	
	}
    
    
 







}
