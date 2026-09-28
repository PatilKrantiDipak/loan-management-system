package com.example.loanmanagement.dto;

import com.example.loanmanagement.enums.Role;

public class RegisterResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private Role role;

    public RegisterResponse() {
    }

    public RegisterResponse(Long id, String name, String email,
                            String phone, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public Long getId() {
        return id;
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