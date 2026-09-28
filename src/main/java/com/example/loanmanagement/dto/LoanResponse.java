package com.example.loanmanagement.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.loanmanagement.enums.LoanStatus;
import com.example.loanmanagement.enums.LoanType;

public class LoanResponse {

    private Long loanId;
    private Long userId;

    private String customerName;
    private String customerEmail;

    private LoanType loanType;
    private BigDecimal amount;
    private BigDecimal interestRate;
    private Integer tenureMonths;
    private LoanStatus status;
    private LocalDate appliedDate;
    private LocalDate approvedDate;
    private BigDecimal emiAmount;

    public LoanResponse(
            Long loanId,
            Long userId,
            String customerName,
            String customerEmail,
            LoanType loanType,
            BigDecimal amount,
            BigDecimal interestRate,
            BigDecimal emiAmount,
            Integer tenureMonths,
            LoanStatus status,
            LocalDate appliedDate,
            LocalDate approvedDate) {

        this.loanId = loanId;
        this.userId = userId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.loanType = loanType;
        this.amount = amount;
        this.interestRate = interestRate;
        this.emiAmount = emiAmount;
        this.tenureMonths = tenureMonths;
        this.status = status;
        this.appliedDate = appliedDate;
        this.approvedDate = approvedDate;
    }

    public Long getLoanId() {
        return loanId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public LoanType getLoanType() {
        return loanType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public Integer getTenureMonths() {
        return tenureMonths;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public LocalDate getAppliedDate() {
        return appliedDate;
    }

    public LocalDate getApprovedDate() {
        return approvedDate;
    }

    public BigDecimal getEmiAmount() {
        return emiAmount;
    }
}