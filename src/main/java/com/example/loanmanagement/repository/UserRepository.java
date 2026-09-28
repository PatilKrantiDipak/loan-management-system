package com.example.loanmanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.loanmanagement.entity.User;
import com.example.loanmanagement.enums.Role;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    
    long countByRole(Role role);
    List<User> findByRole(Role role);
}