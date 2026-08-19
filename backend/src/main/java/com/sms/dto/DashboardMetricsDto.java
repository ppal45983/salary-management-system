package com.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * High-level executive dashboard metrics DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardMetricsDto {

    private long totalEmployees;
    private long activeEmployees;
    private long inactiveEmployees;
    private long departmentsCount;
    private long designationsCount;
    private long pendingSalaryApprovals;

    // Currency-specific total payroll amounts
    private Map<String, BigDecimal> totalMonthlyPayrollByCurrency;
    private Map<String, BigDecimal> totalAnnualPayrollByCurrency;
    private Map<String, BigDecimal> totalTaxCollectedByCurrency;

    // Headcount distribution
    private List<HeadcountByDepartmentDto> headcountByDepartment;
    private List<HeadcountByCountryDto> headcountByCountry;
    private List<HeadcountByStatusDto> headcountByStatus;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HeadcountByDepartmentDto {
        private Long departmentId;
        private String departmentName;
        private long count;
        private double percentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HeadcountByCountryDto {
        private String country;
        private String currency;
        private long count;
        private double percentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HeadcountByStatusDto {
        private String status;
        private long count;
    }
}
