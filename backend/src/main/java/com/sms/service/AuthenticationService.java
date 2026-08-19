package com.sms.service;

import com.sms.dto.JwtAuthenticationResponse;
import com.sms.dto.LoginRequest;
import com.sms.entity.User;
import com.sms.exception.BusinessException;
import com.sms.exception.ResourceNotFoundException;
import com.sms.repository.UserRepository;
import com.sms.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service for authentication operations including login and token generation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuditService auditService;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long ACCOUNT_LOCK_DURATION_MINUTES = 30;

    /**
     * Authenticate user and generate JWT tokens.
     */
    public JwtAuthenticationResponse login(LoginRequest loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getUsernameOrEmail());

        // Find user by username or email
        User user = userRepository.findByUsernameOrEmail(loginRequest.getUsernameOrEmail())
                .orElseThrow(() -> {
                    log.warn("User not found: {}", loginRequest.getUsernameOrEmail());
                    auditService.logFailedAction("LOGIN", "USER", null, 
                            loginRequest.getUsernameOrEmail(), "User not found");
                    return new ResourceNotFoundException("User", "username/email", 
                            loginRequest.getUsernameOrEmail());
                });

        // Check if account is active
        if (!user.getIsActive()) {
            throw new BusinessException("ACCOUNT_INACTIVE", "User account is inactive");
        }

        // Check if account is locked
        if (user.getAccountLocked()) {
            throw new BusinessException("ACCOUNT_LOCKED", 
                    "User account is locked. Please contact administrator.");
        }

        // Check if password matches
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            log.warn("Invalid password for user: {}", user.getUsername());
            incrementFailedLoginAttempts(user);
            auditService.logFailedAction("LOGIN", "USER", user.getId(), 
                    user.getUsername(), "Invalid password");
            throw new BusinessException("INVALID_CREDENTIALS", "Invalid username or password");
        }

        // Reset failed login attempts on successful login
        resetFailedLoginAttempts(user);

        // Update last login timestamp
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Generate tokens
        String accessToken = generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

        log.info("User logged in successfully: {}", user.getUsername());
        auditService.logAction("LOGIN", "USER", user.getId(), user.getUsername(), null, "Login successful");

        return JwtAuthenticationResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getTokenExpirationTime())
                .refreshToken(refreshToken)
                .refreshTokenExpiresIn(604800L) // 7 days
                .build();
    }

    /**
     * Refresh access token using refresh token.
     */
    public JwtAuthenticationResponse refreshToken(String refreshToken) {
        log.debug("Refreshing access token");

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException("INVALID_REFRESH_TOKEN", "Refresh token is invalid or expired");
        }

        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        String newAccessToken = generateAccessToken(user);

        return JwtAuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getTokenExpirationTime())
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Generate access token with user information.
     */
    private String generateAccessToken(User user) {
        java.util.Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("role", user.getRole());
        claims.put("email", user.getEmail());
        
        org.springframework.security.core.userdetails.UserDetails userDetails = 
                new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPasswordHash(),
                        java.util.Collections.singletonList(
                                new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                        "ROLE_" + user.getRole())));

        return jwtTokenProvider.generateToken(userDetails, claims);
    }

    /**
     * Increment failed login attempts.
     */
    private void incrementFailedLoginAttempts(User user) {
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);

        if (user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
            user.setAccountLocked(true);
            log.warn("User account locked due to too many failed login attempts: {}", user.getUsername());
        }

        userRepository.save(user);
    }

    /**
     * Reset failed login attempts.
     */
    private void resetFailedLoginAttempts(User user) {
        if (user.getFailedLoginAttempts() > 0) {
            user.setFailedLoginAttempts(0);
            userRepository.save(user);
        }
    }

    /**
     * Lock user account.
     */
    public void lockUserAccount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        user.setAccountLocked(true);
        userRepository.save(user);
        log.info("User account locked: {}", username);
    }

    /**
     * Unlock user account.
     */
    public void unlockUserAccount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        userRepository.save(user);
        log.info("User account unlocked: {}", username);
    }
}
