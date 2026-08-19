package com.sms.controller;

import com.sms.dto.*;
import com.sms.entity.SalaryHistory;
import com.sms.service.SalaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for salary management operations.
 */
@RestController
@RequestMapping("/salaries")
@RequiredArgsConstructor
@Tag(name = "Salary Management", description = "APIs for salary CRUD, tax calculation preview, approvals, slips, and history")
@SecurityRequirement(name = "bearer-jwt")
public class SalaryController {

    private final SalaryService salaryService;
    private final com.sms.service.CsvExportService csvExportService;

    /**
     * Export all salary records to CSV.
     * GET /salaries/export
     */
    @GetMapping(value = "/export", produces = "text/csv")
    @Operation(summary = "Export salaries as CSV", description = "Download all active salary records as CSV")
    public ResponseEntity<String> exportSalariesCsv() {
        String csvContent = csvExportService.exportSalariesToCsv();
        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"salaries.csv\"")
                .body(csvContent);
    }

    /**
     * Create a new salary record.
     * POST /salaries
     */
    @PostMapping
    @Operation(summary = "Create salary record", description = "Create a new salary record with automatic multi-country tax calculation")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Salary record created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input or validation error"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<ApiResponse<SalaryRecordResponseDto>> createSalaryRecord(
            @Valid @RequestBody SalaryRecordDto dto,
            Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "HR_MANAGER";
        SalaryRecordResponseDto response = salaryService.createSalaryRecord(dto, username);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Salary record created successfully", response, 201));
    }

    /**
     * Get all salary records with pagination and filters.
     * GET /salaries
     */
    @GetMapping
    @Operation(summary = "List salary records", description = "Retrieve paginated salary records with optional filters")
    public ResponseEntity<ApiResponse<PageResponse<SalaryRecordResponseDto>>> getAllSalaries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String status) {
        PageResponse<SalaryRecordResponseDto> response = salaryService.getAllSalaries(page, size, employeeId, status);
        return ResponseEntity.ok(ApiResponse.success("Salary records retrieved successfully", response));
    }

    /**
     * Get salary record by ID.
     * GET /salaries/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get salary record by ID", description = "Retrieve detailed salary breakdown by record ID")
    public ResponseEntity<ApiResponse<SalaryRecordResponseDto>> getSalaryById(@PathVariable Long id) {
        SalaryRecordResponseDto response = salaryService.getSalaryRecordById(id);
        return ResponseEntity.ok(ApiResponse.success("Salary record retrieved successfully", response));
    }

    /**
     * Update an existing salary record.
     * PUT /salaries/{id}
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update salary record", description = "Update salary record details and recalculate taxes")
    public ResponseEntity<ApiResponse<SalaryRecordResponseDto>> updateSalaryRecord(
            @PathVariable Long id,
            @Valid @RequestBody SalaryRecordDto dto,
            Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "HR_MANAGER";
        SalaryRecordResponseDto response = salaryService.updateSalaryRecord(id, dto, username);
        return ResponseEntity.ok(ApiResponse.success("Salary record updated successfully", response));
    }

    /**
     * Delete / Archive salary record.
     * DELETE /salaries/{id}
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete salary record", description = "Deactivate and archive a salary record")
    public ResponseEntity<ApiResponse<Void>> deleteSalaryRecord(
            @PathVariable Long id,
            Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "HR_MANAGER";
        salaryService.deleteSalaryRecord(id, username);
        return ResponseEntity.ok(ApiResponse.success("Salary record archived successfully"));
    }

    /**
     * Approve salary record.
     * PUT /salaries/{id}/approve
     */
    @PutMapping("/{id}/approve")
    @Operation(summary = "Approve salary record", description = "Approve salary record and make it active")
    public ResponseEntity<ApiResponse<SalaryRecordResponseDto>> approveSalary(
            @PathVariable Long id,
            Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "HR_MANAGER";
        SalaryRecordResponseDto response = salaryService.approveSalaryRecord(id, username);
        return ResponseEntity.ok(ApiResponse.success("Salary record approved successfully", response));
    }

    /**
     * Get active salary for an employee.
     * GET /salaries/employee/{employeeId}/active
     */
    @GetMapping("/employee/{employeeId}/active")
    @Operation(summary = "Get active salary for employee", description = "Retrieve current active salary record for an employee")
    public ResponseEntity<ApiResponse<SalaryRecordResponseDto>> getActiveSalaryForEmployee(
            @PathVariable Long employeeId) {
        SalaryRecordResponseDto response = salaryService.getActiveSalaryForEmployee(employeeId);
        return ResponseEntity.ok(ApiResponse.success("Active salary retrieved successfully", response));
    }

    /**
     * Get salary history progression for an employee.
     * GET /salaries/employee/{employeeId}/history
     */
    @GetMapping("/employee/{employeeId}/history")
    @Operation(summary = "Get employee salary history", description = "Retrieve full historical audit trail of salary changes")
    public ResponseEntity<ApiResponse<List<SalaryHistory>>> getSalaryHistoryForEmployee(
            @PathVariable Long employeeId) {
        List<SalaryHistory> history = salaryService.getSalaryHistoryForEmployee(employeeId);
        return ResponseEntity.ok(ApiResponse.success("Salary history retrieved successfully", history));
    }

    /**
     * Real-time Tax preview calculation.
     * POST /salaries/calculate-tax
     */
    @PostMapping("/calculate-tax")
    @Operation(summary = "Preview tax calculation", description = "Calculate progressive tax brackets and net salary preview for given gross inputs")
    public ResponseEntity<ApiResponse<TaxCalculationResponseDto>> calculateTaxPreview(
            @Valid @RequestBody TaxCalculationRequestDto request) {
        TaxCalculationResponseDto response = salaryService.calculateTaxPreview(request);
        return ResponseEntity.ok(ApiResponse.success("Tax calculation preview computed successfully", response));
    }

    /**
     * Generate salary slip.
     * GET /salaries/{id}/slip
     */
    @GetMapping("/{id}/slip")
    @Operation(summary = "Generate salary slip", description = "Retrieve formatted salary slip breakdown with employee, earnings, deductions, and tax data")
    public ResponseEntity<ApiResponse<SalarySlipDto>> generateSalarySlip(@PathVariable Long id) {
        SalarySlipDto response = salaryService.generateSalarySlip(id);
        return ResponseEntity.ok(ApiResponse.success("Salary slip generated successfully", response));
    }

    /**
     * Get pending approvals.
     * GET /salaries/pending-approvals
     */
    @GetMapping("/pending-approvals")
    @Operation(summary = "List pending approvals", description = "Retrieve all salary records awaiting HR manager approval")
    public ResponseEntity<ApiResponse<PageResponse<SalaryRecordResponseDto>>> getPendingApprovals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<SalaryRecordResponseDto> response = salaryService.getPendingApprovals(page, size);
        return ResponseEntity.ok(ApiResponse.success("Pending approvals retrieved successfully", response));
    }
}
