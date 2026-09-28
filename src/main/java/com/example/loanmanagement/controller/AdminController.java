package com.example.loanmanagement.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.loanmanagement.dto.AdminDashboardResponse;
import com.example.loanmanagement.dto.AdminDocumentResponse;
import com.example.loanmanagement.dto.CustomerResponse;
import com.example.loanmanagement.dto.LoanOfficerResponse;
import com.example.loanmanagement.dto.LoanResponse;
import com.example.loanmanagement.dto.RegisterRequest;
import com.example.loanmanagement.dto.RegisterResponse;
import com.example.loanmanagement.service.AdminService;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponse> getDashboard() {

        AdminDashboardResponse response =
                adminService.getDashboardData();

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/customers")
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {

        List<CustomerResponse> customers =
                adminService.getAllCustomers();

        return ResponseEntity.ok(customers);
    }
    
    @GetMapping("/loan-officers")
    public ResponseEntity<List<LoanOfficerResponse>> getAllLoanOfficers() {

        List<LoanOfficerResponse> officers =
                adminService.getAllLoanOfficers();

        return ResponseEntity.ok(officers);
    }
    
    @GetMapping("/loans")
    public ResponseEntity<List<LoanResponse>> getAllLoans() {

        List<LoanResponse> loans =
                adminService.getAllLoans();

        return ResponseEntity.ok(loans);
    }
    
    @PostMapping("/loan-officers")
    public ResponseEntity<RegisterResponse> createLoanOfficer(
            @RequestBody RegisterRequest request) {

        RegisterResponse response =
                adminService.createLoanOfficer(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    @GetMapping("/customers/{customerId}/loans")
    public ResponseEntity<List<LoanResponse>> getCustomerLoans(
            @PathVariable Long customerId) {

        List<LoanResponse> loans =
                adminService.getCustomerLoans(customerId);

        return ResponseEntity.ok(loans);
    }
    @GetMapping("/customers/{customerId}/documents")
    public ResponseEntity<List<AdminDocumentResponse>> getCustomerDocuments(
            @PathVariable Long customerId) {

        List<AdminDocumentResponse> documents =
                adminService.getCustomerDocuments(customerId);

        return ResponseEntity.ok(documents);
    }
}