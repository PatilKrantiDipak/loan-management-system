package com.example.loanmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.loanmanagement.entity.Loan;
import com.example.loanmanagement.entity.User;
import com.example.loanmanagement.enums.LoanStatus;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByUser(User user);
    List<Loan> findAllByOrderByAppliedDateDesc();
    
    long countByStatus(LoanStatus status);
    
}
