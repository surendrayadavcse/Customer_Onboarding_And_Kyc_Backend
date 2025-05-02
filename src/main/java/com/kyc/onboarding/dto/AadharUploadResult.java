package com.kyc.onboarding.dto;




public class AadharUploadResult {
	public enum AadharUploadStatus {
	    SKIPPED, FAILED, PENDING, SUCCESS
	}
    private AadharUploadStatus status;
    private String message;
    private String extractedText; // valid Aadhaar number or null

    public AadharUploadResult(AadharUploadStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public AadharUploadResult(AadharUploadStatus status, String message, String extractedText) {
        this.status = status;
        this.message = message;
        this.extractedText = extractedText;
    }

    // Getters
    public AadharUploadStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getExtractedText() {
        return extractedText;
    }
}
