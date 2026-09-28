package com.example.loanmanagement.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.loanmanagement.dto.DocumentResponse;
import com.example.loanmanagement.entity.UserDocument;
import com.example.loanmanagement.enums.DocumentType;
import com.example.loanmanagement.service.DocumentService;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }


    // =====================================================
    // CUSTOMER - UPLOAD DOCUMENT
    // =====================================================

    @PostMapping("/upload")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<DocumentResponse> uploadDocument(
            @RequestParam("documentType") DocumentType documentType,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) throws IOException {

        String email = authentication.getName();

        DocumentResponse response =
                documentService.uploadDocument(
                        documentType,
                        file,
                        email
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // =====================================================
    // CUSTOMER - VIEW MY DOCUMENTS
    // =====================================================

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<DocumentResponse>> getMyDocuments(
            Authentication authentication) {

        String email = authentication.getName();

        List<DocumentResponse> documents =
                documentService.getMyDocuments(email);

        return ResponseEntity.ok(documents);
    }


    // =====================================================
    // OFFICER - VIEW ALL DOCUMENTS
    // =====================================================

    @GetMapping("/all")
    @PreAuthorize("hasRole('LOAN_OFFICER')")
    public ResponseEntity<List<DocumentResponse>> getAllDocuments() {

        List<DocumentResponse> documents =
                documentService.getAllDocuments();

        return ResponseEntity.ok(documents);
    }


    // =====================================================
    // OFFICER - VERIFY DOCUMENT
    // =====================================================

    @PutMapping("/{documentId}/verify")
    @PreAuthorize("hasRole('LOAN_OFFICER')")
    public ResponseEntity<DocumentResponse> verifyDocument(
            @PathVariable Long documentId) {

        DocumentResponse response =
                documentService.verifyDocument(documentId);

        return ResponseEntity.ok(response);
    }


    // =====================================================
    // OFFICER - REJECT DOCUMENT
    // =====================================================

    @PutMapping("/{documentId}/reject")
    @PreAuthorize("hasRole('LOAN_OFFICER')")
    public ResponseEntity<DocumentResponse> rejectDocument(
            @PathVariable Long documentId) {

        DocumentResponse response =
                documentService.rejectDocument(documentId);

        return ResponseEntity.ok(response);
    }


    // =====================================================
    // OFFICER - VIEW DOCUMENT FILE
    // =====================================================

    @GetMapping("/{documentId}/view")
    @PreAuthorize("hasRole('LOAN_OFFICER')")
    public ResponseEntity<Resource> viewDocument(
            @PathVariable Long documentId) {

        try {

            UserDocument document =
                    documentService.getDocument(documentId);

            Path path =
                    Paths.get(document.getFilePath());

            Resource resource =
                    new UrlResource(path.toUri());

            if (!resource.exists() ||
                    !resource.isReadable()) {

                return ResponseEntity
                        .notFound()
                        .build();
            }

            String contentType =
                    Files.probeContentType(path);

            if (contentType == null) {

                contentType =
                        "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(
                            MediaType.parseMediaType(
                                    contentType
                            )
                    )
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" +
                            document.getFileName() +
                            "\""
                    )
                    .body(resource);

        } catch (Exception e) {

            return ResponseEntity
                    .notFound()
                    .build();
        }
    }
 // =====================================================
 // CUSTOMER - REPLACE REJECTED DOCUMENT
 // =====================================================

 @PostMapping("/replace/{documentId}")
 @PreAuthorize("hasRole('CUSTOMER')")
 public ResponseEntity<DocumentResponse> replaceDocument(
         @PathVariable Long documentId,
         @RequestParam("file") MultipartFile file,
         Authentication authentication) throws IOException {

     String email =
             authentication.getName();

     DocumentResponse response =
             documentService.replaceDocument(
                     documentId,
                     file,
                     email
             );

     return ResponseEntity
             .status(HttpStatus.CREATED)
             .body(response);
 }
}