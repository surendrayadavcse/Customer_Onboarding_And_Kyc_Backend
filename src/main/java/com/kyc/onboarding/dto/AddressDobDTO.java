package com.kyc.onboarding.dto;

import java.time.LocalDate;

public class AddressDobDTO {
    private String address;
    private LocalDate dob;

    public AddressDobDTO() {}

    public AddressDobDTO(String address, LocalDate dob) {
        this.address = address;
        this.dob = dob;
    }

    // Getters and setters
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }
}
