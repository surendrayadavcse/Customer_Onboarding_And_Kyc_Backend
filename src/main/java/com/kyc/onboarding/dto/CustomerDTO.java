package com.kyc.onboarding.dto;



public class CustomerDTO {
 private String fullName;
 private String email;
 private String kycStatus;

 public CustomerDTO() {}

 public CustomerDTO(String fullName, String email, String kycStatus) {
     this.fullName = fullName;
     this.email = email;
     this.kycStatus = kycStatus;
 }

 // Getters and Setters
 public String getFullName() { return fullName; }
 public void setFullName(String fullName) { this.fullName = fullName; }

 public String getEmail() { return email; }
 public void setEmail(String email) { this.email = email; }

 public String getKycStatus() { return kycStatus; }
 public void setKycStatus(String kycStatus) { this.kycStatus = kycStatus; }
}
