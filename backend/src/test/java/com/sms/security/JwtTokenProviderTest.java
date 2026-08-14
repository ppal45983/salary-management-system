package com.sms.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JwtTokenProvider.
 */
@SpringBootTest
@TestPropertySource(properties = {
        "app.jwt.secret=MyVeryLongSecureSecretKeyThatIsAtLeast32BytesForHS256AlgorithmTesting",
        "app.jwt.expiration=86400000",
        "app.jwt.refresh-expiration=604800000"
})
@DisplayName("JwtTokenProvider Tests")
public class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private UserDetails testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "password", new ArrayList<>());
    }

    @Test
    @DisplayName("Should generate valid JWT token")
    void testGenerateToken() {
        // Act
        String token = jwtTokenProvider.generateToken(testUser);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    @DisplayName("Should generate token with additional claims")
    void testGenerateTokenWithClaims() {
        // Arrange
        Map<String, Object> additionalClaims = new HashMap<>();
        additionalClaims.put("role", "ADMIN");
        additionalClaims.put("userId", 123L);

        // Act
        String token = jwtTokenProvider.generateToken(testUser, additionalClaims);

        // Assert
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    @DisplayName("Should extract username from token")
    void testGetUsernameFromToken() {
        // Arrange
        String token = jwtTokenProvider.generateToken(testUser);

        // Act
        String username = jwtTokenProvider.getUsernameFromToken(token);

        // Assert
        assertEquals("testuser", username);
    }

    @Test
    @DisplayName("Should validate valid token")
    void testValidateToken_Valid() {
        // Arrange
        String token = jwtTokenProvider.generateToken(testUser);

        // Act
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should reject invalid token")
    void testValidateToken_Invalid() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should detect non-expired token")
    void testIsTokenExpired_NotExpired() {
        // Arrange
        String token = jwtTokenProvider.generateToken(testUser);

        // Act
        boolean isExpired = jwtTokenProvider.isTokenExpired(token);

        // Assert
        assertFalse(isExpired);
    }

    @Test
    @DisplayName("Should generate refresh token")
    void testGenerateRefreshToken() {
        // Act
        String refreshToken = jwtTokenProvider.generateRefreshToken("testuser");

        // Assert
        assertNotNull(refreshToken);
        assertTrue(jwtTokenProvider.validateToken(refreshToken));
        assertEquals("testuser", jwtTokenProvider.getUsernameFromToken(refreshToken));
    }

    @Test
    @DisplayName("Should get token expiration time")
    void testGetTokenExpirationTime() {
        // Act
        long expirationTime = jwtTokenProvider.getTokenExpirationTime();

        // Assert
        assertTrue(expirationTime > 0);
        assertEquals(86400, expirationTime); // 24 hours in seconds
    }
}
