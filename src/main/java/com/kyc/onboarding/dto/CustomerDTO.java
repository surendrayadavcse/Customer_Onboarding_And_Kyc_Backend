package com.kyc.onboarding.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomerDTO {
    private String fullName;
    private String email;
    private String kycStatus;
    private String registereddate;

    public CustomerDTO() {}

    public CustomerDTO(String fullName, String email, String kycStatus, LocalDateTime registereddate) {
        this.fullName = fullName;
        this.email = email;
        this.kycStatus = kycStatus;
        // Format the date here
        this.registereddate = registereddate != null 
            ? registereddate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) 
            : null;
    }

    // Getters and Setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getKycStatus() { return kycStatus; }
    public void setKycStatus(String kycStatus) { this.kycStatus = kycStatus; }

    public String getRegistereddate() { return registereddate; }
    public void setRegistereddate(String registereddate) { this.registereddate = registereddate; }
}
