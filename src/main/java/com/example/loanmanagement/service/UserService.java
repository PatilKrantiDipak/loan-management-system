package com.example.loanmanagement.service;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.loanmanagement.dto.ChangePasswordRequest;
import com.example.loanmanagement.dto.ForgotPasswordRequest;
import com.example.loanmanagement.dto.LoginRequest;
import com.example.loanmanagement.dto.LoginResponse;
import com.example.loanmanagement.dto.ProfileUpdateRequest;
import com.example.loanmanagement.dto.RegisterRequest;
import com.example.loanmanagement.dto.RegisterResponse;
import com.example.loanmanagement.dto.ResetPasswordRequest;
import com.example.loanmanagement.entity.PasswordResetToken;
import com.example.loanmanagement.entity.User;
import com.example.loanmanagement.enums.Role;
import com.example.loanmanagement.exception.EmailAlreadyExistsException;
import com.example.loanmanagement.repository.PasswordResetTokenRepository;
import com.example.loanmanagement.repository.UserRepository;
import com.example.loanmanagement.security.JwtService;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private PasswordResetTokenRepository passwordResetTokenRepository;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            PasswordResetTokenRepository passwordResetTokenRepository,
            EmailService emailService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.passwordResetTokenRepository =
                passwordResetTokenRepository;
        this.emailService = emailService;
    }

    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "Email already registered"
            );
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        // Every newly registered user is a CUSTOMER
        user.setRole(Role.CUSTOMER);

        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                savedUser.getRole()
        );
        
    }
    public LoginResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new BadCredentialsException(
                                "Invalid email or password"
                        ));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new BadCredentialsException(
                    "Invalid email or password"
            );
        }

        String token = jwtService.generateToken(user.getEmail());

        return new LoginResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
    public RegisterResponse createLoanOfficer(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "Email already registered");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

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

    public RegisterResponse createAdmin(RegisterRequest request) {

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

        user.setRole(Role.ADMIN);

        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                savedUser.getRole()
        );
    }
    public RegisterResponse updateProfile(
            String email,
            ProfileUpdateRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        user.setName(request.getName());
        user.setPhone(request.getPhone());

        User updatedUser = userRepository.save(user);

        return new RegisterResponse(
                updatedUser.getId(),
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getPhone(),
                updatedUser.getRole()
        );
    }
    public void deleteProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        userRepository.delete(user);
    }
    public RegisterResponse getProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return new RegisterResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole()
        );
    }
 // =====================================================
 // CHANGE PASSWORD
 // =====================================================

 public void changePassword(
         String email,
         ChangePasswordRequest request) {

     User user = userRepository.findByEmail(email)
             .orElseThrow(() ->
                     new RuntimeException("User not found"));

     // Verify current password
     if (!passwordEncoder.matches(
             request.getCurrentPassword(),
             user.getPassword())) {

         throw new RuntimeException(
                 "Current password is incorrect");
     }

     // Check new password and confirm password
     if (!request.getNewPassword()
             .equals(request.getConfirmPassword())) {

         throw new RuntimeException(
                 "New password and confirm password do not match");
     }

     // Prevent using the same password
     if (passwordEncoder.matches(
             request.getNewPassword(),
             user.getPassword())) {

         throw new RuntimeException(
                 "New password must be different from current password");
     }

     // Encode and save new password
     user.setPassword(
             passwordEncoder.encode(
                     request.getNewPassword()
             )
     );

     userRepository.save(user);
 }
 public void forgotPassword(ForgotPasswordRequest request) {

	    User user = userRepository.findByEmail(request.getEmail())
	            .orElse(null);

	    // Do not reveal whether the email exists
	    if (user == null) {
	        return;
	    }

	    // Invalidate previous unused tokens
	 // Invalidate previous unused token
	    passwordResetTokenRepository
	            .findByUserAndUsedFalse(user)
	            .ifPresent(oldToken -> {
	                oldToken.setUsed(true);
	                passwordResetTokenRepository.save(oldToken);
	            });

	    String token = UUID.randomUUID().toString();

	    PasswordResetToken resetToken =
	            new PasswordResetToken(
	                    token,
	                    user,
	                    LocalDateTime.now().plusMinutes(15)
	            );

	    passwordResetTokenRepository.save(resetToken);

	    emailService.sendPasswordResetEmail(
	            user.getEmail(),
	            user.getName(),
	            token
	    );
	}
 
 public void resetPassword(ResetPasswordRequest request) {

	    PasswordResetToken resetToken =
	            passwordResetTokenRepository
	                    .findByToken(request.getToken())
	                    .orElseThrow(() ->
	                            new RuntimeException(
	                                    "Invalid or expired reset token"));

	    if (resetToken.isUsed()) {
	        throw new RuntimeException(
	                "Reset token has already been used");
	    }

	    if (resetToken.getExpiryDate()
	            .isBefore(LocalDateTime.now())) {

	        throw new RuntimeException(
	                "Reset token has expired");
	    }

	    if (!request.getNewPassword()
	            .equals(request.getConfirmPassword())) {

	        throw new RuntimeException(
	                "New password and confirm password do not match");
	    }

	    User user = resetToken.getUser();

	    user.setPassword(
	            passwordEncoder.encode(
	                    request.getNewPassword()
	            )
	    );

	    userRepository.save(user);

	    // Make token single-use
	    resetToken.setUsed(true);

	    passwordResetTokenRepository.save(resetToken);
	}
}
