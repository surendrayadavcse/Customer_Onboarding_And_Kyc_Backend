package com.kyc.onboarding.model;

import jakarta.persistence.*;


import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class Consent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private boolean consentGiven;
    private LocalDateTime completedAt;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public boolean isConsentGiven() { return consentGiven; }
    public void setConsentGiven(boolean consentGiven) { this.consentGiven = consentGiven; }

    public  LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt( LocalDateTime completedAt) { this.completedAt = completedAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
