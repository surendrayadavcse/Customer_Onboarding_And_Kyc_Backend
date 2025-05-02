package com.kyc.onboarding.exception;

public class AadhaarAlreadyVerifiedException extends RuntimeException {
    public AadhaarAlreadyVerifiedException(String message) {
        super(message);
    }
}
