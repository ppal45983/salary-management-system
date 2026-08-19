package com.sms.controller;

import com.sms.dto.ApiResponse;
import com.sms.dto.JwtAuthenticationResponse;
import com.sms.dto.LoginRequest;
import com.sms.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * REST Controller for authentication operations.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs for user authentication and token management")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    /**
     * Authenticate user and get JWT tokens.
     * POST /auth/login
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and get JWT access token")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Account locked or inactive")
    })
    public ResponseEntity<ApiResponse<JwtAuthenticationResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest) {
        
        JwtAuthenticationResponse response = authenticationService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    /**
     * Refresh access token.
     * POST /auth/refresh
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Generate new access token using refresh token")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token refreshed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid or expired refresh token")
    })
    public ResponseEntity<ApiResponse<JwtAuthenticationResponse>> refreshToken(
            @RequestHeader("Authorization") String authHeader) {
        
        // Extract token from Bearer prefix
        String token = authHeader.replace("Bearer ", "");
        JwtAuthenticationResponse response = authenticationService.refreshToken(token);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }

    /**
     * Health check endpoint (public).
     * GET /auth/health
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check authentication service health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Authentication service is healthy", "OK"));
    }
}
