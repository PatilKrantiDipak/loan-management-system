package com.example.loanmanagement.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.loanmanagement.dto.DocumentResponse;
import com.example.loanmanagement.entity.User;
import com.example.loanmanagement.entity.UserDocument;
import com.example.loanmanagement.enums.DocumentStatus;
import com.example.loanmanagement.enums.DocumentType;
import com.example.loanmanagement.repository.UserDocumentRepository;
import com.example.loanmanagement.repository.UserRepository;

@Service
public class DocumentService {

    private final UserDocumentRepository documentRepository;
    private final UserRepository userRepository;

    private final String uploadDirectory = "uploads";

    public DocumentService(
            UserDocumentRepository documentRepository,
            UserRepository userRepository) {

        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
    }


    // =====================================================
    // UPLOAD DOCUMENT
    // =====================================================

    public DocumentResponse uploadDocument(
            DocumentType documentType,
            MultipartFile file,
            String email) throws IOException {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is required");
        }

        Path uploadPath =
                Paths.get(uploadDirectory);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName =
                System.currentTimeMillis()
                + "_" + file.getOriginalFilename();

        Path filePath =
                uploadPath.resolve(fileName);

        Files.copy(
                file.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );


        UserDocument document =
                new UserDocument();

        document.setUser(user);

        document.setDocumentType(
                documentType
        );

        document.setFileName(
                file.getOriginalFilename()
        );

        document.setFilePath(
                filePath.toString()
        );

        document.setVerificationStatus(
                DocumentStatus.PENDING
        );

        document.setUploadedDate(
                LocalDateTime.now()
        );


        UserDocument savedDocument =
                documentRepository.save(document);


        return convertToResponse(
                savedDocument
        );
    }


    // =====================================================
    // GET CUSTOMER DOCUMENTS
    // =====================================================

    public List<DocumentResponse> getMyDocuments(
            String email) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));


        return documentRepository
                .findByUser(user)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // =====================================================
    // GET ALL DOCUMENTS - OFFICER
    // =====================================================

    public List<DocumentResponse> getAllDocuments() {

        return documentRepository
                .findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // =====================================================
    // VERIFY DOCUMENT
    // =====================================================

    public DocumentResponse verifyDocument(
            Long documentId) {

        UserDocument document =
                documentRepository
                        .findById(documentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Document not found"));


        if (document.getVerificationStatus()
                == DocumentStatus.VERIFIED) {

            throw new RuntimeException(
                    "Document is already verified");
        }


        if (document.getVerificationStatus()
                == DocumentStatus.REJECTED) {

            throw new RuntimeException(
                    "Rejected document cannot be verified");
        }


        document.setVerificationStatus(
                DocumentStatus.VERIFIED
        );

        document.setVerifiedDate(
                LocalDateTime.now()
        );


        UserDocument updatedDocument =
                documentRepository.save(document);


        return convertToResponse(
                updatedDocument
        );
    }

 // =====================================================
 // REPLACE REJECTED DOCUMENT
 // =====================================================

 public DocumentResponse replaceDocument(
         Long documentId,
         MultipartFile file,
         String email) throws IOException {

     UserDocument oldDocument =
             documentRepository
                     .findById(documentId)
                     .orElseThrow(() ->
                             new RuntimeException(
                                     "Document not found"));

     // Make sure the document belongs to the logged-in customer
     if (!oldDocument.getUser()
             .getEmail()
             .equals(email)) {

         throw new RuntimeException(
                 "You are not authorized to replace this document");
     }

     // Replacement is allowed only for rejected documents
     if (oldDocument.getVerificationStatus()
             != DocumentStatus.REJECTED) {

         throw new RuntimeException(
                 "Only rejected documents can be replaced");
     }

     if (file == null || file.isEmpty()) {

         throw new RuntimeException(
                 "File is required");
     }


     // Create upload directory if it doesn't exist
     Path uploadPath =
             Paths.get(uploadDirectory);

     if (!Files.exists(uploadPath)) {
         Files.createDirectories(uploadPath);
     }


     // Generate new file name
     String fileName =
             System.currentTimeMillis()
             + "_" + file.getOriginalFilename();

     Path filePath =
             uploadPath.resolve(fileName);


     Files.copy(
             file.getInputStream(),
             filePath,
             StandardCopyOption.REPLACE_EXISTING
     );


     // Create a NEW document record
     UserDocument newDocument =
             new UserDocument();

     newDocument.setUser(
             oldDocument.getUser()
     );

     newDocument.setDocumentType(
             oldDocument.getDocumentType()
     );

     newDocument.setFileName(
             file.getOriginalFilename()
     );

     newDocument.setFilePath(
             filePath.toString()
     );

     newDocument.setVerificationStatus(
             DocumentStatus.PENDING
     );

     newDocument.setUploadedDate(
             LocalDateTime.now()
     );


     UserDocument savedDocument =
             documentRepository.save(newDocument);


     return convertToResponse(
             savedDocument
     );
 }
    // =====================================================
    // REJECT DOCUMENT
    // =====================================================

    public DocumentResponse rejectDocument(
            Long documentId) {

        UserDocument document =
                documentRepository
                        .findById(documentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Document not found"));


        if (document.getVerificationStatus()
                == DocumentStatus.VERIFIED) {

            throw new RuntimeException(
                    "Verified document cannot be rejected");
        }


        if (document.getVerificationStatus()
                == DocumentStatus.REJECTED) {

            throw new RuntimeException(
                    "Document is already rejected");
        }


        document.setVerificationStatus(
                DocumentStatus.REJECTED
        );

        document.setVerifiedDate(
                LocalDateTime.now()
        );


        UserDocument updatedDocument =
                documentRepository.save(document);


        return convertToResponse(
                updatedDocument
        );
    }


    // =====================================================
    // GET DOCUMENT
    // =====================================================

    public UserDocument getDocument(
            Long documentId) {

        return documentRepository
                .findById(documentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Document not found"));
    }


    // =====================================================
    // CONVERT ENTITY → RESPONSE
    // =====================================================

    private DocumentResponse convertToResponse(
            UserDocument document) {

        return new DocumentResponse(
                document.getId(),
                document.getUser().getId(),
                document.getDocumentType(),
                document.getFileName(),
                document.getVerificationStatus(),
                document.getUploadedDate(),
                document.getVerifiedDate()
        );
    }
}