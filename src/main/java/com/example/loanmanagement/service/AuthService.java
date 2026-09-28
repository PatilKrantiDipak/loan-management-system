package com.example.loanmanagement.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.loanmanagement.dto.RegisterRequest;
import com.example.loanmanagement.dto.RegisterResponse;
import com.example.loanmanagement.entity.User;
import com.example.loanmanagement.enums.Role;
import com.example.loanmanagement.exception.EmailAlreadyExistsException;
import com.example.loanmanagement.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public RegisterResponse register(RegisterRequest request) {

        // 1. Check email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "Email already registered"
            );
        }

        // 2. Create User
        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        // 3. Encrypt password
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        // 4. Default role
        user.setRole(Role.CUSTOMER);

        // 5. Save user
        User savedUser = userRepository.save(user);

        // 6. Return response without password
        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                savedUser.getRole()
        );
    }
}