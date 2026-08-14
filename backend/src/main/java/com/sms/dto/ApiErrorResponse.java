package com.sms.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Standard API error response DTO.
 * Used to return consistent error responses to clients.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

    private int status;
    private String timestamp;
    private String message;
    private String errorCode;
    private String path;
    private Map<String, String> validationErrors;
    private List<String> details;
    private String traceId;

    /**
     * Create error response with timestamp.
     */
    public static ApiErrorResponse of(int status, String message, String errorCode, String path) {
        return ApiErrorResponse.builder()
                .status(status)
                .timestamp(LocalDateTime.now().toString())
                .message(message)
                .errorCode(errorCode)
                .path(path)
                .build();
    }

    /**
     * Create validation error response.
     */
    public static ApiErrorResponse ofValidation(int status, Map<String, String> validationErrors, String path) {
        return ApiErrorResponse.builder()
                .status(status)
                .timestamp(LocalDateTime.now().toString())
                .message("Validation failed")
                .errorCode("VALIDATION_ERROR")
                .path(path)
                .validationErrors(validationErrors)
                .build();
    }
}
