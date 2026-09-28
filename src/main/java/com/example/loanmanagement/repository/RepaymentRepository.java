package com.example.loanmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.loanmanagement.entity.Loan;
import com.example.loanmanagement.entity.Repayment;

public interface RepaymentRepository
        extends JpaRepository<Repayment, Long> {

    List<Repayment> findByLoanOrderByInstallmentNumberAsc(
            Loan loan);
}