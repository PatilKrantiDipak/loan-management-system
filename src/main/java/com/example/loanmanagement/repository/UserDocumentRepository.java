package com.example.loanmanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.loanmanagement.entity.User;
import com.example.loanmanagement.entity.UserDocument;
import com.example.loanmanagement.enums.DocumentStatus;
import com.example.loanmanagement.enums.DocumentType;

public interface UserDocumentRepository
        extends JpaRepository<UserDocument, Long> {

    List<UserDocument> findByUser(User user);

    long countByUserIdAndVerificationStatus(
            Long userId,
            DocumentStatus verificationStatus);
    Optional<UserDocument> findTopByUserAndDocumentTypeOrderByUploadedDateDesc(
            User user,
            DocumentType documentType);
}