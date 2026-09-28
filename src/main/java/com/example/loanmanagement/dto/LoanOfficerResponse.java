package com.example.loanmanagement.dto;

import com.example.loanmanagement.enums.Role;

public class LoanOfficerResponse {

    private Long userId;
    private String name;
    private String email;
    private String phone;
    private Role role;

    public LoanOfficerResponse() {
    }

    public LoanOfficerResponse(
            Long userId,
            String name,
            String email,
            String phone,
            Role role) {

        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
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
}