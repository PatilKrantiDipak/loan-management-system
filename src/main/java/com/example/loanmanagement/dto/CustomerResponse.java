package com.example.loanmanagement.dto;

import com.example.loanmanagement.enums.Role;

public class CustomerResponse {

    private Long userId;
    private String name;
    private String email;
    private String phone;
    private Role role;
    private long totalLoans;

    public CustomerResponse() {
    }

    public CustomerResponse(
            Long userId,
            String name,
            String email,
            String phone,
            Role role,
            long totalLoans) {

        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.totalLoans = totalLoans;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Role getRole() {
        return role;
    }

    public long getTotalLoans() {
        return totalLoans;
    }
}