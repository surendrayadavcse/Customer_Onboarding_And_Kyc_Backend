package com.kyc.onboarding.exception;

public class PanAlreadyVerifiedException extends RuntimeException {
    public PanAlreadyVerifiedException(String message) {
        super(message);
    }
}