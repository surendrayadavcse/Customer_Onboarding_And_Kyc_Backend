package com.kyc.onboarding.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class KycDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String aadharNumber;
    private String aadharImage; 
    private boolean aadharVerified;

    private String panNumber;
    private String panImage;
    private boolean panVerified;

    private String selfieImage;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAadharNumber() { return aadharNumber; }
    public void setAadharNumber(String aadharNumber) { this.aadharNumber = aadharNumber; }

    public String getAadharImage() { return aadharImage; }
    public void setAadharImage(String aadharImage) { this.aadharImage = aadharImage; }

    public boolean isAadharVerified() { return aadharVerified; }
    public void setAadharVerified(boolean aadharVerified) { this.aadharVerified = aadharVerified; }

    public String getPanNumber() { return panNumber; }
    public void setPanNumber(String panNumber) { this.panNumber = panNumber; }

    public String getPanImage() { return panImage; }
    public void setPanImage(String panImage) { this.panImage = panImage; }

    public boolean isPanVerified() { return panVerified; }
    public void setPanVerified(boolean panVerified) { this.panVerified = panVerified; }

    public String getSelfieImage() { return selfieImage; }
    public void setSelfieImage(String selfieImage) { this.selfieImage = selfieImage; }
 
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public boolean getAadhaarVerified() {
        return aadharVerified;
    }
	public boolean getPanVerified() {
		// TODO Auto-generated method stub
		 return panVerified;
	}
}
