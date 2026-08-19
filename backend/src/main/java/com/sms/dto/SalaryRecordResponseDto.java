package com.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for Salary Record response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryRecordResponseDto {

    private Long id;
    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private BigDecimal baseSalary;
    private BigDecimal allowances;
    private BigDecimal deductions;
    private BigDecimal grossSalary;
    private BigDecimal tax;
    private BigDecimal netSalary;
    private LocalDate effectiveDate;
    private LocalDate endDate;
    private String status;
    private String payFrequency;
    private String currency;
    private String country;
    private Long approvedBy;
    private LocalDateTime approvedAt;
    private String approverName;
    private String comments;
    private LocalDateTime createdAt;
}
