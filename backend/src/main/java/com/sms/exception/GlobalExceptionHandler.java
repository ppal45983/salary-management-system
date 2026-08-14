package com.sms.exception;

import com.sms.dto.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for REST API.
 * Handles all exceptions and returns consistent error responses.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle ResourceNotFoundException.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        
        log.warn("Resource not found: {}", ex.getMessage());
        
        ApiErrorResponse errorResponse = ApiErrorResponse.of(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                "RESOURCE_NOT_FOUND",
                getRequestPath(request)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle BusinessException.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(
            BusinessException ex, WebRequest request) {
        
        log.warn("Business logic error: {}", ex.getMessage());
        
        ApiErrorResponse errorResponse = ApiErrorResponse.of(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                ex.getMessage(),
                ex.getErrorCode() != null ? ex.getErrorCode() : "BUSINESS_ERROR",
                getRequestPath(request)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * Handle ValidationException.
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            ValidationException ex, WebRequest request) {
        
        log.warn("Validation error: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        if (ex.getFieldName() != null) {
            errors.put(ex.getFieldName(), ex.getMessage());
        }
        
        ApiErrorResponse errorResponse = ApiErrorResponse.ofValidation(
                HttpStatus.BAD_REQUEST.value(),
                errors,
                getRequestPath(request)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle MethodArgumentNotValidException for @Valid validation.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        log.warn("Method argument validation failed");
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ApiErrorResponse errorResponse = ApiErrorResponse.ofValidation(
                HttpStatus.BAD_REQUEST.value(),
                errors,
                getRequestPath(request)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle NoHandlerFoundException (404).
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoHandlerFound(
            NoHandlerFoundException ex, WebRequest request) {
        
        log.warn("Endpoint not found: {} {}", ex.getHttpMethod(), ex.getRequestURL());
        
        ApiErrorResponse errorResponse = ApiErrorResponse.of(
                HttpStatus.NOT_FOUND.value(),
                "Endpoint not found",
                "ENDPOINT_NOT_FOUND",
                getRequestPath(request)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle generic Exception.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {
        
        log.error("Unexpected error occurred", ex);
        
        ApiErrorResponse errorResponse = ApiErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred. Please try again later.",
                "INTERNAL_SERVER_ERROR",
                getRequestPath(request)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Extract request path from WebRequest.
     */
    private String getRequestPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
