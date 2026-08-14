package com.sms.exception;

/**
 * Custom exception for validation errors.
 */
public class ValidationException extends RuntimeException {

    private String fieldName;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getFieldName() {
        return fieldName;
    }
}
