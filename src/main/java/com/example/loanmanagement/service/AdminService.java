package com.example.loanmanagement.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.loanmanagement.dto.AdminDashboardResponse;
import com.example.loanmanagement.dto.AdminDocumentResponse;
import com.example.loanmanagement.dto.CustomerResponse;
import com.example.loanmanagement.dto.LoanOfficerResponse;
import com.example.loanmanagement.dto.LoanResponse;
import com.example.loanmanagement.dto.RegisterRequest;
import com.example.loanmanagement.dto.RegisterResponse;
import com.example.loanmanagement.entity.Loan;
import com.example.loanmanagement.entity.User;
import com.example.loanmanagement.entity.UserDocument;
import com.example.loanmanagement.enums.LoanStatus;
import com.example.loanmanagement.enums.Role;
import com.example.loanmanagement.exception.EmailAlreadyExistsException;
import com.example.loanmanagement.repository.LoanRepository;
import com.example.loanmanagement.repository.UserDocumentRepository;
import com.example.loanmanagement.repository.UserRepository;


@Service
public class AdminService {

    private final UserRepository userRepository;
    private final LoanRepository loanRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDocumentRepository userDocumentRepository;

    public AdminService(
            UserRepository userRepository,
            LoanRepository loanRepository,
            PasswordEncoder passwordEncoder,
            UserDocumentRepository userDocumentRepository) {

        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDocumentRepository = userDocumentRepository;
    }

    public AdminDashboardResponse getDashboardData() {

        long totalCustomers =
                userRepository.countByRole(Role.CUSTOMER);

        long totalLoanOfficers =
                userRepository.countByRole(Role.LOAN_OFFICER);

        long totalLoans =
                loanRepository.count();

        long appliedLoans =
                loanRepository.countByStatus(LoanStatus.APPLIED);

        long approvedLoans =
                loanRepository.countByStatus(LoanStatus.APPROVED);

        long rejectedLoans =
                loanRepository.countByStatus(LoanStatus.REJECTED);

        long activeLoans =
                loanRepository.countByStatus(LoanStatus.ACTIVE);

        long closedLoans =
                loanRepository.countByStatus(LoanStatus.CLOSED);

        return new AdminDashboardResponse(
                totalCustomers,
                totalLoanOfficers,
                totalLoans,
                appliedLoans,
                approvedLoans,
                rejectedLoans,
                activeLoans,
                closedLoans
        );
    }
    public List<CustomerResponse> getAllCustomers() {

        List<User> customers =
                userRepository.findByRole(Role.CUSTOMER);

        return customers.stream()
                .map(user -> new CustomerResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getRole(),
                        loanRepository.findByUser(user).size()
                ))
                .toList();
    }
    public List<LoanOfficerResponse> getAllLoanOfficers() {

        List<User> officers =
                userRepository.findByRole(Role.LOAN_OFFICER);

        return officers.stream()
                .map(user -> new LoanOfficerResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getRole()
                ))
                .toList();
    }
    public List<LoanResponse> getAllLoans() {

        List<Loan> loans =
                loanRepository.findAllByOrderByAppliedDateDesc();

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
    public RegisterResponse createLoanOfficer(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "Email already registered"
            );
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );
        user.setPhone(request.getPhone());
        user.setRole(Role.LOAN_OFFICER);

        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                savedUser.getRole()
        );
    }
    public List<LoanResponse> getCustomerLoans(Long customerId) {

        User customer = userRepository.findById(customerId)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found"));

        if (customer.getRole() != Role.CUSTOMER) {
            throw new RuntimeException("User is not a customer");
        }

        List<Loan> loans =
                loanRepository.findByUser(customer);

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
    public List<AdminDocumentResponse> getCustomerDocuments(Long customerId) {

        User customer = userRepository.findById(customerId)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found"));

        if (customer.getRole() != Role.CUSTOMER) {
            throw new RuntimeException("User is not a customer");
        }

        List<UserDocument> documents =
                userDocumentRepository.findByUser(customer);

        return documents.stream()
                .map(document -> new AdminDocumentResponse(
                        document.getId(),
                        customer.getId(),
                        customer.getName(),
                        customer.getEmail(),
                        document.getDocumentType(),
                        document.getFileName(),
                        document.getVerificationStatus(),
                        document.getUploadedDate(),
                        document.getVerifiedDate()
                ))
                .toList();
    }
}