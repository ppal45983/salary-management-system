package com.sms.controller;

import com.sms.dto.ApiResponse;
import com.sms.dto.DashboardMetricsDto;
import com.sms.dto.DepartmentDistributionDto;
import com.sms.dto.PayEquityDto;
import com.sms.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for HR Analytics, Dashboard Metrics, and Pay Equity Analysis.
 */
@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics & Reporting", description = "APIs for executive dashboards, salary distribution, and pay equity")
@SecurityRequirement(name = "bearer-jwt")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    /**
     * Get executive dashboard metrics.
     * GET /analytics/dashboard
     */
    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard metrics", description = "Retrieve overall metrics, payroll by currency, and headcount breakdowns")
    public ResponseEntity<ApiResponse<DashboardMetricsDto>> getDashboardMetrics() {
        DashboardMetricsDto response = analyticsService.getDashboardMetrics();
        return ResponseEntity.ok(ApiResponse.success("Dashboard metrics retrieved successfully", response));
    }

    /**
     * Get salary distribution by department.
     * GET /analytics/distribution
     */
    @GetMapping("/distribution")
    @Operation(summary = "Get salary distribution", description = "Retrieve Min, Max, Mean, Median, and Quartile salary distribution by department")
    public ResponseEntity<ApiResponse<List<DepartmentDistributionDto>>> getDepartmentDistributions() {
        List<DepartmentDistributionDto> response = analyticsService.getDepartmentDistributions();
        return ResponseEntity.ok(ApiResponse.success("Department salary distributions retrieved successfully", response));
    }

    /**
     * Get pay equity analysis by designation.
     * GET /analytics/pay-equity
     */
    @GetMapping("/pay-equity")
    @Operation(summary = "Get pay equity analysis", description = "Retrieve salary ranges, ratios, and equity metrics by designation")
    public ResponseEntity<ApiResponse<List<PayEquityDto>>> getPayEquityAnalysis() {
        List<PayEquityDto> response = analyticsService.getPayEquityAnalysis();
        return ResponseEntity.ok(ApiResponse.success("Pay equity analysis retrieved successfully", response));
    }
}
