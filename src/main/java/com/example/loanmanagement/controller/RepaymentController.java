package com.example.loanmanagement.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.loanmanagement.dto.RepaymentResponse;
import com.example.loanmanagement.service.RepaymentService;

@RestController
@RequestMapping("/api/repayments")
public class RepaymentController {

    private final RepaymentService repaymentService;

    public RepaymentController(
            RepaymentService repaymentService) {

        this.repaymentService = repaymentService;
    }

    // =====================================================
    // CUSTOMER - VIEW REPAYMENT SCHEDULE
    // =====================================================

    @GetMapping("/loan/{loanId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<RepaymentResponse>>
    getRepaymentSchedule(
            @PathVariable Long loanId,
            Authentication authentication) {

        String email =
                authentication.getName();

        List<RepaymentResponse> repayments =
                repaymentService.getRepaymentSchedule(
                        loanId,
                        email
                );

        return ResponseEntity.ok(repayments);
    }
    @PutMapping("/{repaymentId}/pay")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<RepaymentResponse> payInstallment(
            @PathVariable Long repaymentId,
            Authentication authentication) {

        String email = authentication.getName();

        RepaymentResponse response =
                repaymentService.payInstallment(
                        repaymentId,
                        email
                );

        return ResponseEntity.ok(response);
    }
}