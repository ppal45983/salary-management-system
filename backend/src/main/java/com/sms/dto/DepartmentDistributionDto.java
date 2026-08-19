package com.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for department salary distribution metrics (Min, Max, Avg, Median, Percentiles).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDistributionDto {

    private Long departmentId;
    private String departmentName;
    private String currency;
    private long employeeCount;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private BigDecimal averageSalary;
    private BigDecimal medianSalary;
    private BigDecimal p25Salary;
    private BigDecimal p75Salary;
    private BigDecimal totalGrossSalary;
    private BigDecimal totalTax;
    private BigDecimal totalNetSalary;
}
