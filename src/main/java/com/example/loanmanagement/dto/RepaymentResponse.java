package com.example.loanmanagement.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.loanmanagement.enums.RepaymentStatus;

public class RepaymentResponse {

    private Long repaymentId;
    private Long loanId;
    private Integer installmentNumber;
    private LocalDate dueDate;
    private BigDecimal emiAmount;
    private BigDecimal principalAmount;
    private BigDecimal interestAmount;
    private LocalDate paymentDate;
    private RepaymentStatus status;

    public RepaymentResponse() {
    }

    public RepaymentResponse(
            Long repaymentId,
            Long loanId,
            Integer installmentNumber,
            LocalDate dueDate,
            BigDecimal emiAmount,
            BigDecimal principalAmount,
            BigDecimal interestAmount,
            LocalDate paymentDate,
            RepaymentStatus status) {

        this.repaymentId = repaymentId;
        this.loanId = loanId;
        this.installmentNumber = installmentNumber;
        this.dueDate = dueDate;
        this.emiAmount = emiAmount;
        this.principalAmount = principalAmount;
        this.interestAmount = interestAmount;
        this.paymentDate = paymentDate;
        this.status = status;
    }

    public Long getRepaymentId() {
        return repaymentId;
    }

    public Long getLoanId() {
        return loanId;
    }

    public Integer getInstallmentNumber() {
        return installmentNumber;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public BigDecimal getEmiAmount() {
        return emiAmount;
    }

    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }

    public BigDecimal getInterestAmount() {
        return interestAmount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public RepaymentStatus getStatus() {
        return status;
    }
}