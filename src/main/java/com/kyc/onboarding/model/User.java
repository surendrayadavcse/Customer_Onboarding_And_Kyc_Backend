package com.kyc.onboarding.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String fullName;
    private String mobile;
    private String email;
    private String password;
    private String role;
    private String kycStatus;
    private LocalDate dob;
    private String address;
    private LocalDateTime registereddate;

    public LocalDateTime getRegistereddate() {
		return registereddate;
	}
	public void setRegistereddate(LocalDateTime registereddate) {
		this.registereddate = registereddate;
	}
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private KycDocument kycDocument;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Consent consent;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<FinancialService> services;

    // ✅ Added VerificationLogs relation
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<VerificationLog> verificationLogs;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getKycStatus() { return kycStatus; }
    public void setKycStatus(String kycStatus) { this.kycStatus = kycStatus; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public KycDocument getKycDocument() { return kycDocument; }
    public void setKycDocument(KycDocument kycDocument) { this.kycDocument = kycDocument; }

    public Consent getConsent() { return consent; }
    public void setConsent(Consent consent) { this.consent = consent; }

    public List<FinancialService> getServices() { return services; }
    public void setServices(List<FinancialService> services) { this.services = services; }

    public List<VerificationLog> getVerificationLogs() { return verificationLogs; }
    public void setVerificationLogs(List<VerificationLog> verificationLogs) { this.verificationLogs = verificationLogs; }
}
