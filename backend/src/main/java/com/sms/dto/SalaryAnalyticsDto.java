package com.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for salary analytics and statistics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryAnalyticsDto {

    private Long departmentId;
    private String departmentName;
    private Long employeeCount;
    private BigDecimal averageSalary;
    private BigDecimal averageGrossSalary;
    private BigDecimal averageTax;
    private BigDecimal averageNetSalary;
    private BigDecimal totalSalaryBudget;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private String currency;
}
