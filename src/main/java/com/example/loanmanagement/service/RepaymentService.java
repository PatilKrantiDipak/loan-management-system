package com.example.loanmanagement.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.loanmanagement.dto.RepaymentResponse;
import com.example.loanmanagement.entity.Loan;
import com.example.loanmanagement.entity.Repayment;
import com.example.loanmanagement.enums.LoanStatus;
import com.example.loanmanagement.enums.RepaymentStatus;
import com.example.loanmanagement.repository.LoanRepository;
import com.example.loanmanagement.repository.RepaymentRepository;

@Service
public class RepaymentService {

    private final RepaymentRepository repaymentRepository;
    private final LoanRepository loanRepository;

    public RepaymentService(
            RepaymentRepository repaymentRepository,
            LoanRepository loanRepository) {

        this.repaymentRepository = repaymentRepository;
        this.loanRepository = loanRepository;
    }

    // =====================================================
    // GENERATE REPAYMENT SCHEDULE
    // =====================================================

    public List<RepaymentResponse> generateRepaymentSchedule(
            Long loanId) {

        Loan loan = loanRepository
                .findById(loanId)
                .orElseThrow(() ->
                        new RuntimeException("Loan not found"));

        // Schedule can be generated only for approved loan
        if (loan.getStatus() != LoanStatus.APPROVED) {

            throw new RuntimeException(
                    "Repayment schedule can be generated only for an approved loan");
        }

        // Prevent duplicate repayment schedules
        List<Repayment> existingRepayments =
                repaymentRepository
                        .findByLoanOrderByInstallmentNumberAsc(loan);

        if (!existingRepayments.isEmpty()) {

            return existingRepayments.stream()
                    .map(this::convertToResponse)
                    .toList();
        }

        BigDecimal outstandingPrincipal =
                loan.getAmount();

        BigDecimal annualInterestRate =
                loan.getInterestRate();

        BigDecimal monthlyInterestRate =
                annualInterestRate.divide(
                        BigDecimal.valueOf(12 * 100),
                        10,
                        RoundingMode.HALF_UP
                );

        BigDecimal emi =
                loan.getEmiAmount();

        Integer tenure =
                loan.getTenureMonths();

        List<Repayment> repayments =
                new ArrayList<>();

        LocalDate firstDueDate =
                LocalDate.now().plusMonths(1);

        for (int month = 1;
                month <= tenure;
                month++) {

            // =============================================
            // MONTHLY INTEREST
            // =============================================

            BigDecimal interestAmount =
                    outstandingPrincipal
                            .multiply(monthlyInterestRate)
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );

            // =============================================
            // PRINCIPAL AMOUNT
            // =============================================

            BigDecimal principalAmount =
                    emi.subtract(interestAmount)
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );

            // =============================================
            // LAST EMI ADJUSTMENT
            // =============================================

            BigDecimal installmentEmi = emi;

            if (month == tenure) {

                principalAmount =
                        outstandingPrincipal
                                .setScale(
                                        2,
                                        RoundingMode.HALF_UP
                                );

                installmentEmi =
                        principalAmount
                                .add(interestAmount)
                                .setScale(
                                        2,
                                        RoundingMode.HALF_UP
                                );
            }

            // =============================================
            // CREATE REPAYMENT
            // =============================================

            Repayment repayment =
                    new Repayment();

            repayment.setLoan(loan);

            repayment.setInstallmentNumber(
                    month
            );

            repayment.setDueDate(
                    firstDueDate.plusMonths(month - 1)
            );

            repayment.setEmiAmount(
                    installmentEmi
            );

            repayment.setPrincipalAmount(
                    principalAmount
            );

            repayment.setInterestAmount(
                    interestAmount
            );

            repayment.setStatus(
                    RepaymentStatus.PENDING
            );

            repayments.add(repayment);

            // =============================================
            // UPDATE OUTSTANDING PRINCIPAL
            // =============================================

            outstandingPrincipal =
                    outstandingPrincipal
                            .subtract(principalAmount)
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );

            if (outstandingPrincipal
                    .compareTo(BigDecimal.ZERO) < 0) {

                outstandingPrincipal =
                        BigDecimal.ZERO;
            }
        }

        // Save all installments
        List<Repayment> savedRepayments =
                repaymentRepository.saveAll(
                        repayments
                );

        return savedRepayments.stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =====================================================
    // GET REPAYMENT SCHEDULE
    // =====================================================

    public List<RepaymentResponse> getRepaymentSchedule(
            Long loanId,
            String email) {

        Loan loan = loanRepository
                .findById(loanId)
                .orElseThrow(() ->
                        new RuntimeException("Loan not found"));

        // Customer can view only their own loan
        if (!loan.getUser()
                .getEmail()
                .equals(email)) {

            throw new RuntimeException(
                    "You are not authorized to view this repayment schedule");
        }

        List<Repayment> repayments =
                repaymentRepository
                        .findByLoanOrderByInstallmentNumberAsc(
                                loan
                        );

        return repayments.stream()
                .map(this::convertToResponse)
                .toList();
    }

    public RepaymentResponse payInstallment(
            Long repaymentId,
            String email) {

        Repayment repayment =
                repaymentRepository
                        .findById(repaymentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Repayment not found"));

        if (!repayment.getLoan()
                .getUser()
                .getEmail()
                .equals(email)) {

            throw new RuntimeException(
                    "You are not authorized to make this payment");
        }

        if (repayment.getStatus()
                == RepaymentStatus.PAID) {

            throw new RuntimeException(
                    "This installment is already paid");
        }

        repayment.setStatus(
                RepaymentStatus.PAID
        );

        repayment.setPaymentDate(
                LocalDate.now()
        );

        Repayment updatedRepayment =
                repaymentRepository.save(repayment);


        // Check whether all installments are paid

        List<Repayment> repayments =
                repaymentRepository
                        .findByLoanOrderByInstallmentNumberAsc(
                                repayment.getLoan()
                        );

        boolean allPaid =
                repayments.stream()
                        .allMatch(r ->
                                r.getStatus() ==
                                RepaymentStatus.PAID
                        );


        // Close loan when all installments are paid

        if (allPaid) {

            Loan loan =
                    repayment.getLoan();

            loan.setStatus(
                    LoanStatus.CLOSED
            );

            loanRepository.save(loan);
        }


        return convertToResponse(
                updatedRepayment
        );
    }
    public void updateOverdueRepayments() {

        List<Repayment> repayments =
                repaymentRepository.findAll();

        LocalDate today =
                LocalDate.now();

        for (Repayment repayment : repayments) {

            if (repayment.getStatus()
                    == RepaymentStatus.PENDING
                    && repayment.getDueDate()
                    .isBefore(today)) {

                repayment.setStatus(
                        RepaymentStatus.OVERDUE
                );
            }
        }

        repaymentRepository.saveAll(repayments);
    }
    // =====================================================
    // CONVERT ENTITY TO RESPONSE
    // =====================================================

    private RepaymentResponse convertToResponse(
            Repayment repayment) {

        return new RepaymentResponse(
                repayment.getId(),
                repayment.getLoan().getId(),
                repayment.getInstallmentNumber(),
                repayment.getDueDate(),
                repayment.getEmiAmount(),
                repayment.getPrincipalAmount(),
                repayment.getInterestAmount(),
                repayment.getPaymentDate(),
                repayment.getStatus()
        );
    }
    @Scheduled(cron = "0 0 1 * * *")
    public void scheduledOverdueCheck() {

        updateOverdueRepayments();
    }
}