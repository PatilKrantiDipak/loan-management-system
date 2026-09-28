package com.example.loanmanagement.dto;

import java.math.BigDecimal;

import com.example.loanmanagement.enums.LoanType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class LoanRequest {

    @NotNull(message = "Loan type is required")
    private LoanType loanType;

    @NotNull(message = "Loan amount is required")
    @DecimalMin(value = "10000.00",
            message = "Loan amount must be at least 10000")
    private BigDecimal amount;

    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "0.01",
            message = "Interest rate must be greater than 0")
    private BigDecimal interestRate;

    @NotNull(message = "Tenure is required")
    @Min(value = 6,
         message = "Tenure must be at least 6 months")
    private Integer tenureMonths;

    public LoanRequest() {
    }

    public LoanType getLoanType() {
        return loanType;
    }

    public void setLoanType(LoanType loanType) {
        this.loanType = loanType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getTenureMonths() {
        return tenureMonths;
    }

    public void setTenureMonths(Integer tenureMonths) {
        this.tenureMonths = tenureMonths;
    }
}