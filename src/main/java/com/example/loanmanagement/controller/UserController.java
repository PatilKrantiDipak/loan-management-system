package com.example.loanmanagement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.loanmanagement.dto.ChangePasswordRequest;
import com.example.loanmanagement.dto.ForgotPasswordRequest;
import com.example.loanmanagement.dto.LoginRequest;
import com.example.loanmanagement.dto.LoginResponse;
import com.example.loanmanagement.dto.ProfileUpdateRequest;
import com.example.loanmanagement.dto.RegisterRequest;
import com.example.loanmanagement.dto.RegisterResponse;
import com.example.loanmanagement.dto.ResetPasswordRequest;
import com.example.loanmanagement.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        RegisterResponse response =
                userService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response =
                userService.login(request);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/profile")
    public ResponseEntity<RegisterResponse> profile(
            Authentication authentication) {

        String email = authentication.getName();

        RegisterResponse response =
                userService.getProfile(email);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/customer-test")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> customerTest() {

        return ResponseEntity.ok(
                "Customer access successful"
        );
    }
    
    //officer creation
    @PostMapping("/create-officer")
    public ResponseEntity<RegisterResponse> createOfficer(
            @Valid @RequestBody RegisterRequest request) {

        RegisterResponse response =
                userService.createLoanOfficer(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    @PostMapping("/create-admin")
    public ResponseEntity<RegisterResponse> createAdmin(
            @RequestBody RegisterRequest request) {

        RegisterResponse response = userService.createAdmin(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/profile")
    public ResponseEntity<RegisterResponse> updateProfile(
            @Valid @RequestBody ProfileUpdateRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        RegisterResponse response =
                userService.updateProfile(email, request);

        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/profile")
    public ResponseEntity<String> deleteProfile(
            Authentication authentication) {

        String email = authentication.getName();

        userService.deleteProfile(email);

        return ResponseEntity.ok(
                "User account deleted successfully"
        );
    }
    
 // =====================================================
 // CHANGE PASSWORD
 // =====================================================

 @PutMapping("/change-password")
 public ResponseEntity<String> changePassword(
         @Valid @RequestBody ChangePasswordRequest request,
         Authentication authentication) {

     String email = authentication.getName();

     userService.changePassword(
             email,
             request
     );

     return ResponseEntity.ok(
             "Password changed successfully"
     );
 }
 
 @PostMapping("/forgot-password")
 public ResponseEntity<String> forgotPassword(
         @Valid @RequestBody ForgotPasswordRequest request) {

     userService.forgotPassword(request);

     return ResponseEntity.ok(
             "If the email is registered, a password reset link has been sent."
     );
 }
 @PostMapping("/reset-password")
 public ResponseEntity<String> resetPassword(
         @Valid @RequestBody ResetPasswordRequest request) {

     userService.resetPassword(request);

     return ResponseEntity.ok(
             "Password reset successfully. Please login with your new password."
     );
 }
}
