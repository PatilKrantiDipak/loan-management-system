package com.example.loanmanagement.dto;

import java.time.LocalDateTime;

import com.example.loanmanagement.enums.DocumentStatus;
import com.example.loanmanagement.enums.DocumentType;

public class AdminDocumentResponse {

    private Long documentId;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private DocumentType documentType;
    private String fileName;
    private DocumentStatus verificationStatus;
    private LocalDateTime uploadedDate;
    private LocalDateTime verifiedDate;

    public AdminDocumentResponse() {
    }

    public AdminDocumentResponse(
            Long documentId,
            Long customerId,
            String customerName,
            String customerEmail,
            DocumentType documentType,
            String fileName,
            DocumentStatus verificationStatus,
            LocalDateTime uploadedDate,
            LocalDateTime verifiedDate) {

        this.documentId = documentId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.documentType = documentType;
        this.fileName = fileName;
        this.verificationStatus = verificationStatus;
        this.uploadedDate = uploadedDate;
        this.verifiedDate = verifiedDate;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public String getFileName() {
        return fileName;
    }

    public DocumentStatus getVerificationStatus() {
        return verificationStatus;
    }

    public LocalDateTime getUploadedDate() {
        return uploadedDate;
    }

    public LocalDateTime getVerifiedDate() {
        return verifiedDate;
    }
}