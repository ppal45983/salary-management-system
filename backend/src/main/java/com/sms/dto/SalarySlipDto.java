package com.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO representing a full, formatted Salary Slip.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalarySlipDto {

    private String slipNumber;
    private Long salaryRecordId;
    private String payPeriod;
    private LocalDate generatedDate;

    // Employee Information
    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private String email;
    private String departmentName;
    private String designationTitle;
    private String country;
    private String currency;
    private String taxId;
    private String bankAccount;
    private String bankCode;

    // Earnings
    private BigDecimal baseSalary;
    private BigDecimal allowances;
    private BigDecimal grossSalary;

    // Deductions
    private BigDecimal standardDeductions;
    private BigDecimal incomeTax;
    private BigDecimal totalDeductions;

    // Net Pay
    private BigDecimal netSalary;
    private String netSalaryInWords;

    // Tax Details
    private BigDecimal effectiveTaxRate;
    private List<TaxCalculationResponseDto.TaxBracketBreakdownDto> taxBreakdown;

    // Company Information
    private String companyName;
    private String companyAddress;
}
