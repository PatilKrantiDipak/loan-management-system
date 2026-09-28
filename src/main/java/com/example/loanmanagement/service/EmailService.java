package com.example.loanmanagement.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendLoanApprovalEmail(
            String customerEmail,
            String customerName,
            Long loanId) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(customerEmail);
        message.setSubject("Loan Application Approved");
        message.setText(
                "Dear " + customerName + ",\n\n"
                + "Congratulations!\n\n"
                + "Your loan application has been approved.\n\n"
                + "Loan ID: " + loanId + "\n\n"
                + "Our team will contact you regarding the next steps.\n\n"
                + "Thank you,\n"
                + "Loan Management System"
        );

        mailSender.send(message);
    }

    public void sendLoanRejectionEmail(
            String customerEmail,
            String customerName,
            Long loanId) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(customerEmail);
        message.setSubject("Loan Application Update");
        message.setText(
                "Dear " + customerName + ",\n\n"
                + "We regret to inform you that your loan application "
                + "has been rejected.\n\n"
                + "Loan ID: " + loanId + "\n\n"
                + "Please contact our support team for more information.\n\n"
                + "Thank you,\n"
                + "Loan Management System"
        );

        mailSender.send(message);
    }
    public void sendPasswordResetEmail(
            String email,
            String name,
            String token) {

        String resetLink =
                "http://localhost:8080/reset-password.html?token="
                        + token;

        String subject = "Loan Management System - Password Reset";

        String message =
                "Hello " + name + ",\n\n"
                + "We received a request to reset your password.\n\n"
                + "Click the link below to reset your password:\n"
                + resetLink + "\n\n"
                + "This link is valid for 15 minutes.\n\n"
                + "If you did not request a password reset, "
                + "please ignore this email.\n\n"
                + "Regards,\n"
                + "Loan Management System";

        sendEmail(email, subject, message);
    }
    private void sendEmail(
            String email,
            String subject,
            String messageText) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(email);
        message.setSubject(subject);
        message.setText(messageText);

        mailSender.send(message);
    }
}