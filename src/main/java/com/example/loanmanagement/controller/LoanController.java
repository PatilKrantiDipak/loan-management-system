package com.example.loanmanagement.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.loanmanagement.dto.LoanRequest;
import com.example.loanmanagement.dto.LoanResponse;
import com.example.loanmanagement.service.LoanService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<LoanResponse> applyForLoan(
            @Valid @RequestBody LoanRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        LoanResponse response =
                loanService.applyForLoan(request, email);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    
    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<LoanResponse>> getMyLoans(
            Authentication authentication) {

        String email = authentication.getName();

        List<LoanResponse> loans =
                loanService.getMyLoans(email);

        return ResponseEntity.ok(loans);
    }
    @GetMapping
    @PreAuthorize("hasRole('LOAN_OFFICER')")
    public ResponseEntity<List<LoanResponse>> getAllLoans() {

        List<LoanResponse> loans =
                loanService.getAllLoans();

        return ResponseEntity.ok(loans);
    }
    @PutMapping("/{loanId}/approve")
    @PreAuthorize("hasRole('LOAN_OFFICER')")
    public ResponseEntity<LoanResponse> approveLoan(
            @PathVariable Long loanId) {

        LoanResponse response =
                loanService.approveLoan(loanId);

        return ResponseEntity.ok(response);
    }
    @PutMapping("/{loanId}/reject")
    @PreAuthorize("hasRole('LOAN_OFFICER')")
    public ResponseEntity<LoanResponse> rejectLoan(
            @PathVariable Long loanId) {

        LoanResponse response =
                loanService.rejectLoan(loanId);

        return ResponseEntity.ok(response);
    }
}