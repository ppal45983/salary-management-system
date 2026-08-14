package com.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for creating or updating a Salary Record.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryRecordDto {

    private Long id;

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Base salary is required")
    @DecimalMin(value = "0.0", message = "Base salary must be positive")
    private BigDecimal baseSalary;

    @DecimalMin(value = "0.0", message = "Allowances must be positive")
    private BigDecimal allowances;

    @DecimalMin(value = "0.0", message = "Deductions must be positive")
    private BigDecimal deductions;

    private BigDecimal grossSalary;
    private BigDecimal tax;
    private BigDecimal netSalary;

    @NotNull(message = "Effective date is required")
    private LocalDate effectiveDate;

    private LocalDate endDate;

    @NotBlank(message = "Status is required")
    private String status;

    @NotBlank(message = "Pay frequency is required")
    private String payFrequency;

    @NotBlank(message = "Currency is required")
    private String currency;

    private Long approvedBy;
    private java.time.LocalDateTime approvedAt;
    private String comments;
}
