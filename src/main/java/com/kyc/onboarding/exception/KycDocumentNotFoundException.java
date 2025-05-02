package com.kyc.onboarding.exception;

public class KycDocumentNotFoundException extends RuntimeException {
    public KycDocumentNotFoundException(String message) {
        super(message);
    }
}
