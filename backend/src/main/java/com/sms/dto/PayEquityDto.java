package com.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for Pay Equity and salary band analysis by designation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayEquityDto {

    private Long designationId;
    private String designationTitle;
    private String departmentName;
    private String currency;
    private long employeeCount;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private BigDecimal averageSalary;
    private BigDecimal medianSalary;
    private BigDecimal salarySpreadRatio; // max / min ratio
    private BigDecimal standardDeviation;
}
