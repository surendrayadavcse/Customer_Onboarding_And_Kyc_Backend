package com.kyc.onboarding.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class KycDocumentResponse {

    private String aadharNumber;
    private String aadharImageUrl;
    private String panNumber;
    private String panImageUrl;

    public KycDocumentResponse() {
    }
   
    public KycDocumentResponse(String aadharNumber, String aadharImageUrl, String panNumber, String panImageUrl) {
        this.aadharNumber = aadharNumber;
        this.aadharImageUrl = aadharImageUrl;
        this.panNumber = panNumber;
        this.panImageUrl = panImageUrl;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    public String getAadharImageUrl() {
        return aadharImageUrl;
    }

    public void setAadharImageUrl(String aadharImageUrl) {
        this.aadharImageUrl = aadharImageUrl;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getPanImageUrl() {
        return panImageUrl;
    }

    public void setPanImageUrl(String panImageUrl) {
        this.panImageUrl = panImageUrl;
    }
}
