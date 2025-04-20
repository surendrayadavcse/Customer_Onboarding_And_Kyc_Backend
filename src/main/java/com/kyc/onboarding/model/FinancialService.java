package com.kyc.onboarding.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.ManyToOne;

//package com.kyc.onboarding.model;

import jakarta.persistence.*;

@Entity
public class FinancialService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String serviceName;
    private String serviceDetails;

    private String serviceIconPath;  

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceDetails() {
        return serviceDetails;
    }

    public void setServiceDetails(String serviceDetails) {
        this.serviceDetails = serviceDetails;
    }

    public String getServiceIconPath() {
        return serviceIconPath;
    }

    public void setServiceIconPath(String serviceIconPath) {
        this.serviceIconPath = serviceIconPath;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
