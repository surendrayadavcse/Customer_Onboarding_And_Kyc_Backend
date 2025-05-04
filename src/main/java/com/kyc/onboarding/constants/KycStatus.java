package com.kyc.onboarding.constants;

public final class KycStatus {

    public static final String PENDING = "PENDING";
    public static final String  STEP1_COMPLETED= "STEP 1 COMPLETED";
    public static final String STEP2_COMPLETED = "STEP 2 COMPLETED";
    public static final String KYC_COMPLETED = "KYC COMPLETED";

    // Private constructor to prevent instantiation
    private KycStatus() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
