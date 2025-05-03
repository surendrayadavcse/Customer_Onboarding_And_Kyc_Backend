package com.kyc.onboarding.dto;

public class SelfieResponse {
    private int documentId;
    private String selfieImageUrl;

    public SelfieResponse(int documentId, String selfieImageUrl) {
        this.documentId = documentId;
        this.selfieImageUrl = selfieImageUrl;
    }

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public String getSelfieImageUrl() {
        return selfieImageUrl;
    }

    public void setSelfieImageUrl(String selfieImageUrl) {
        this.selfieImageUrl = selfieImageUrl;
    }
}
