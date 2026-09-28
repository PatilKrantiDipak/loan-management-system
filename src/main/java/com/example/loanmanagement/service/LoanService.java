package com.example.loanmanagement.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.loanmanagement.dto.LoanRequest;
import com.example.loanmanagement.dto.LoanResponse;
import com.example.loanmanagement.entity.Loan;
import com.example.loanmanagement.entity.User;
import com.example.loanmanagement.entity.UserDocument;
import com.example.loanmanagement.enums.DocumentStatus;
import com.example.loanmanagement.enums.DocumentType;
import com.example.loanmanagement.enums.LoanStatus;
import com.example.loanmanagement.repository.LoanRepository;
import com.example.loanmanagement.repository.UserDocumentRepository;
import com.example.loanmanagement.repository.UserRepository;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final EmiService emiService;
    private final UserDocumentRepository documentRepository;
    private final RepaymentService repaymentService;

    public LoanService(
            LoanRepository loanRepository,
            UserRepository userRepository,
            EmailService emailService,
            EmiService emiService,
            UserDocumentRepository documentRepository,
            RepaymentService repaymentService) {

        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.emiService = emiService;
        this.documentRepository = documentRepository;
        this.repaymentService = repaymentService;
    }

    // =====================================================
    // CUSTOMER - APPLY FOR LOAN
    // =====================================================

    public LoanResponse applyForLoan(
            LoanRequest request,
            String email) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Loan loan = new Loan();

        loan.setUser(user);
        loan.setLoanType(request.getLoanType());
        loan.setAmount(request.getAmount());
        loan.setInterestRate(request.getInterestRate());
        loan.setTenureMonths(request.getTenureMonths());

        loan.setStatus(LoanStatus.APPLIED);
        loan.setAppliedDate(LocalDate.now());

        Loan savedLoan = loanRepository.save(loan);

        return new LoanResponse(
                savedLoan.getId(),
                user.getId(),
                user.getName(),
                user.getEmail(),
                savedLoan.getLoanType(),
                savedLoan.getAmount(),
                savedLoan.getInterestRate(),
                savedLoan.getEmiAmount(),
                savedLoan.getTenureMonths(),
                savedLoan.getStatus(),
                savedLoan.getAppliedDate(),
                savedLoan.getApprovedDate()
        );
    }

    // =====================================================
    // CUSTOMER - VIEW MY LOANS
    // =====================================================

    public List<LoanResponse> getMyLoans(
            String email) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        List<Loan> loans =
                loanRepository.findByUser(user);

        return loans.stream()
                .map(loan -> new LoanResponse(
                        loan.getId(),
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        loan.getLoanType(),
                        loan.getAmount(),
                        loan.getInterestRate(),
                        loan.getEmiAmount(),
                        loan.getTenureMonths(),
                        loan.getStatus(),
                        loan.getAppliedDate(),
                        loan.getApprovedDate()
                ))
                .toList();
    }

    // =====================================================
    // LOAN OFFICER - VIEW ALL LOANS
    // =====================================================

    public List<LoanResponse> getAllLoans() {

        List<Loan> loans =
                loanRepository
                        .findAllByOrderByAppliedDateDesc();

        return loans.stream()
                .map(loan -> new LoanResponse(
                        loan.getId(),
                        loan.getUser().getId(),
                        loan.getUser().getName(),
                        loan.getUser().getEmail(),
                        loan.getLoanType(),
                        loan.getAmount(),
                        loan.getInterestRate(),
                        loan.getEmiAmount(),
                        loan.getTenureMonths(),
                        loan.getStatus(),
                        loan.getAppliedDate(),
                        loan.getApprovedDate()
                ))
                .toList();
    }

    // =====================================================
    // LOAN OFFICER - APPROVE LOAN
    // =====================================================

    public LoanResponse approveLoan(Long loanId) {

        Loan loan = loanRepository
                .findById(loanId)
                .orElseThrow(() ->
                        new RuntimeException("Loan not found"));

        // =================================================
        // CHECK LOAN STATUS
        // =================================================

        if (loan.getStatus() == LoanStatus.APPROVED) {
            throw new RuntimeException(
                    "Loan is already approved");
        }

        if (loan.getStatus() == LoanStatus.REJECTED) {
            throw new RuntimeException(
                    "Rejected loan cannot be approved");
        }

        // =================================================
        // CHECK CUSTOMER KYC DOCUMENTS
        // =================================================

        Set<DocumentType> requiredDocuments =
                EnumSet.of(
                        DocumentType.PAN,
                        DocumentType.AADHAAR,
                        DocumentType.ADDRESS_PROOF,
                        DocumentType.INCOME_PROOF
                );

        Set<DocumentType> missingDocuments =
                EnumSet.noneOf(DocumentType.class);

        Set<DocumentType> unverifiedDocuments =
                EnumSet.noneOf(DocumentType.class);

        // Check latest document for every required type
        for (DocumentType documentType :
                requiredDocuments) {

            UserDocument latestDocument =
                    documentRepository
                            .findTopByUserAndDocumentTypeOrderByUploadedDateDesc(
                                    loan.getUser(),
                                    documentType
                            )
                            .orElse(null);

            // ---------------------------------------------
            // Document not uploaded
            // ---------------------------------------------

            if (latestDocument == null) {

                missingDocuments.add(
                        documentType
                );

                continue;
            }

            // ---------------------------------------------
            // Latest document is not verified
            // ---------------------------------------------

            if (latestDocument.getVerificationStatus()
                    != DocumentStatus.VERIFIED) {

                unverifiedDocuments.add(
                        documentType
                );
            }
        }

        // =================================================
        // MISSING DOCUMENT CHECK
        // =================================================

        if (!missingDocuments.isEmpty()) {

            throw new RuntimeException(
                    "Loan cannot be approved. "
                    + "Missing KYC documents: "
                    + missingDocuments
            );
        }

        // =================================================
        // VERIFICATION CHECK
        // =================================================

        if (!unverifiedDocuments.isEmpty()) {

            throw new RuntimeException(
                    "Loan cannot be approved. "
                    + "KYC documents not verified: "
                    + unverifiedDocuments
            );
        }

        // =================================================
        // CALCULATE EMI
        // =================================================

        BigDecimal emi =
                emiService.calculateEmi(
                        loan.getAmount(),
                        loan.getInterestRate(),
                        loan.getTenureMonths()
                );

        // Store EMI
        loan.setEmiAmount(emi);

        // =================================================
        // UPDATE LOAN STATUS
        // =================================================

        loan.setStatus(LoanStatus.APPROVED);
        loan.setApprovedDate(LocalDate.now());

        Loan updatedLoan =
                loanRepository.save(loan);

        // =================================================
        // GENERATE REPAYMENT SCHEDULE
        // =================================================

        repaymentService.generateRepaymentSchedule(
                updatedLoan.getId()
        );

        // =================================================
        // SEND APPROVAL EMAIL
        // =================================================

        try {

            emailService.sendLoanApprovalEmail(
                    loan.getUser().getEmail(),
                    loan.getUser().getName(),
                    loan.getId()
            );

        } catch (Exception e) {

            System.out.println(
                    "Approval email could not be sent: "
                    + e.getMessage()
            );
        }

        return convertToResponse(updatedLoan);
    }

    // =====================================================
    // LOAN OFFICER - REJECT LOAN
    // =====================================================

    public LoanResponse rejectLoan(Long loanId) {

        Loan loan = loanRepository
                .findById(loanId)
                .orElseThrow(() ->
                        new RuntimeException("Loan not found"));

        // Cannot reject approved loan
        if (loan.getStatus() == LoanStatus.APPROVED) {

            throw new RuntimeException(
                    "Approved loan cannot be rejected");
        }

        // Already rejected
        if (loan.getStatus() == LoanStatus.REJECTED) {

            throw new RuntimeException(
                    "Loan is already rejected");
        }

        // =================================================
        // UPDATE STATUS
        // =================================================

        loan.setStatus(LoanStatus.REJECTED);

        Loan updatedLoan =
                loanRepository.save(loan);

        // =================================================
        // SEND REJECTION EMAIL
        // =================================================

        try {

            emailService.sendLoanRejectionEmail(
                    loan.getUser().getEmail(),
                    loan.getUser().getName(),
                    loan.getId()
            );

        } catch (Exception e) {

            System.out.println(
                    "Rejection email could not be sent: "
                    + e.getMessage()
            );
        }

        return convertToResponse(updatedLoan);
    }

    // =====================================================
    // CONVERT ENTITY TO RESPONSE
    // =====================================================

    private LoanResponse convertToResponse(Loan loan) {

        return new LoanResponse(
                loan.getId(),
                loan.getUser().getId(),
                loan.getUser().getName(),
                loan.getUser().getEmail(),
                loan.getLoanType(),
                loan.getAmount(),
                loan.getInterestRate(),
                loan.getEmiAmount(),
                loan.getTenureMonths(),
                loan.getStatus(),
                loan.getAppliedDate(),
                loan.getApprovedDate()
        );
    }
}