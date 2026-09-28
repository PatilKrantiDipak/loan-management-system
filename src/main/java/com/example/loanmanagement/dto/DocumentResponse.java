package com.example.loanmanagement.dto;

import java.time.LocalDateTime;

import com.example.loanmanagement.enums.DocumentStatus;
import com.example.loanmanagement.enums.DocumentType;

public class DocumentResponse {

    private Long documentId;
    private Long userId;
    private DocumentType documentType;
    private String fileName;
    private DocumentStatus verificationStatus;
    private LocalDateTime uploadedDate;
    private LocalDateTime verifiedDate;

    public DocumentResponse() {
    }

    public DocumentResponse(
            Long documentId,
            Long userId,
            DocumentType documentType,
            String fileName,
            DocumentStatus verificationStatus,
            LocalDateTime uploadedDate,
            LocalDateTime verifiedDate) {

        this.documentId = documentId;
        this.userId = userId;
        this.documentType = documentType;
        this.fileName = fileName;
        this.verificationStatus = verificationStatus;
        this.uploadedDate = uploadedDate;
        this.verifiedDate = verifiedDate;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public Long getUserId() {
        return userId;
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