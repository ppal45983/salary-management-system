package com.sms.service;

import com.sms.dto.JwtAuthenticationResponse;
import com.sms.dto.LoginRequest;
import com.sms.entity.User;
import com.sms.exception.BusinessException;
import com.sms.exception.ResourceNotFoundException;
import com.sms.repository.UserRepository;
import com.sms.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthenticationService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationService Unit Tests")
public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User mockUser;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("hr_admin");
        mockUser.setEmail("admin@acme.com");
        mockUser.setPasswordHash("$2a$10$hashedpassword");
        mockUser.setRole("HR_MANAGER");
        mockUser.setIsActive(true);
        mockUser.setAccountLocked(false);
        mockUser.setFailedLoginAttempts(0);

        loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail("hr_admin");
        loginRequest.setPassword("password123");
    }

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void testLogin_Success() {
        when(userRepository.findByUsernameOrEmail("hr_admin")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password123", "$2a$10$hashedpassword")).thenReturn(true);
        when(jwtTokenProvider.generateToken(any(), any())).thenReturn("mock-jwt-token");
        when(jwtTokenProvider.generateRefreshToken("hr_admin")).thenReturn("mock-refresh-token");
        when(jwtTokenProvider.getTokenExpirationTime()).thenReturn(86400L);

        JwtAuthenticationResponse response = authenticationService.login(loginRequest);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("mock-refresh-token", response.getRefreshToken());
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user does not exist")
    void testLogin_UserNotFound() {
        when(userRepository.findByUsernameOrEmail("unknown")).thenReturn(Optional.empty());
        loginRequest.setUsernameOrEmail("unknown");

        assertThrows(ResourceNotFoundException.class, () -> {
            authenticationService.login(loginRequest);
        });
    }

    @Test
    @DisplayName("Should throw BusinessException when password is wrong")
    void testLogin_InvalidPassword() {
        when(userRepository.findByUsernameOrEmail("hr_admin")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("wrongpass", "$2a$10$hashedpassword")).thenReturn(false);
        loginRequest.setPassword("wrongpass");

        assertThrows(BusinessException.class, () -> {
            authenticationService.login(loginRequest);
        });
        assertEquals(1, mockUser.getFailedLoginAttempts());
    }

    @Test
    @DisplayName("Should throw BusinessException when account is locked")
    void testLogin_AccountLocked() {
        mockUser.setAccountLocked(true);
        when(userRepository.findByUsernameOrEmail("hr_admin")).thenReturn(Optional.of(mockUser));

        assertThrows(BusinessException.class, () -> {
            authenticationService.login(loginRequest);
        });
    }
}
