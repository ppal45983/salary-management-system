package com.sms.controller;

import com.sms.dto.ApiResponse;
import com.sms.dto.EmployeeDto;
import com.sms.dto.EmployeeResponseDto;
import com.sms.dto.PageResponse;
import com.sms.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * REST Controller for employee management endpoints.
 */
@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
@Tag(name = "Employee Management", description = "APIs for employee CRUD operations")
@SecurityRequirement(name = "bearer-jwt")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final CsvExportService csvExportService;

    /**
     * Create a new employee.
     * POST /employees
     */
    @PostMapping
    @Operation(summary = "Create new employee", description = "Create a new employee in the system")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Employee created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Employee ID or email already exists")
    })
    public ResponseEntity<ApiResponse<EmployeeResponseDto>> createEmployee(
            @Valid @RequestBody EmployeeDto dto,
            Authentication authentication) {
        
        EmployeeResponseDto response = employeeService.createEmployee(dto, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Employee created successfully", response, 201));
    }

    /**
     * Get employee by ID.
     * GET /employees/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID", description = "Retrieve employee details by employee ID")
    public ResponseEntity<ApiResponse<EmployeeResponseDto>> getEmployee(@PathVariable Long id) {
        EmployeeResponseDto response = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(ApiResponse.success("Employee retrieved successfully", response));
    }

    /**
     * Update employee.
     * PUT /employees/{id}
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update employee", description = "Update employee information")
    public ResponseEntity<ApiResponse<EmployeeResponseDto>> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeDto dto,
            Authentication authentication) {
        
        EmployeeResponseDto response = employeeService.updateEmployee(id, dto, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Employee updated successfully", response));
    }

    /**
     * Delete/Deactivate employee.
     * DELETE /employees/{id}
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate employee", description = "Deactivate an employee (soft delete)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Employee deactivated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(
            @PathVariable Long id,
            Authentication authentication) {
        
        employeeService.deactivateEmployee(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Employee deactivated successfully"));
    }

    /**
     * Get all employees with pagination.
     * GET /employees
     */
    @GetMapping
    @Operation(summary = "List employees", description = "Get all active employees with pagination")
    @Parameter(name = "page", description = "Page number (0-based)", example = "0")
    @Parameter(name = "size", description = "Page size", example = "10")
    @Parameter(name = "sort", description = "Sort field and direction (e.g., 'firstName' or '-hire_date')", example = "firstName")
    public ResponseEntity<ApiResponse<PageResponse<EmployeeResponseDto>>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {
        
        PageResponse<EmployeeResponseDto> response = employeeService.getAllEmployees(page, size, sort);
        return ResponseEntity.ok(ApiResponse.success("Employees retrieved successfully", response));
    }

    /**
     * Search employees.
     * GET /employees/search
     */
    @GetMapping("/search")
    @Operation(summary = "Search employees", description = "Search employees by name, email, or employee ID")
    @Parameter(name = "query", description = "Search term", required = true)
    public ResponseEntity<ApiResponse<PageResponse<EmployeeResponseDto>>> searchEmployees(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        PageResponse<EmployeeResponseDto> response = employeeService.searchEmployees(query, page, size);
        return ResponseEntity.ok(ApiResponse.success("Search results", response));
    }

    /**
     * Get employees by department.
     * GET /employees/department/{departmentId}
     */
    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Get employees by department", description = "Retrieve all employees in a specific department")
    public ResponseEntity<ApiResponse<PageResponse<EmployeeResponseDto>>> getEmployeesByDepartment(
            @PathVariable Long departmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        PageResponse<EmployeeResponseDto> response = employeeService.getEmployeesByDepartment(departmentId, page, size);
        return ResponseEntity.ok(ApiResponse.success("Employees retrieved successfully", response));
    }

    /**
     * Get employees by status.
     * GET /employees/status/{status}
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Get employees by status", description = "Retrieve employees with specific status")
    public ResponseEntity<ApiResponse<PageResponse<EmployeeResponseDto>>> getEmployeesByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        PageResponse<EmployeeResponseDto> response = employeeService.getEmployeesByStatus(status, page, size);
        return ResponseEntity.ok(ApiResponse.success("Employees retrieved successfully", response));
    }

    /**
     * Get employees by country.
     * GET /employees/country/{country}
     */
    @GetMapping("/country/{country}")
    @Operation(summary = "Get employees by country", description = "Retrieve employees from specific country")
    public ResponseEntity<ApiResponse<PageResponse<EmployeeResponseDto>>> getEmployeesByCountry(
            @PathVariable String country,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        PageResponse<EmployeeResponseDto> response = employeeService.getEmployeesByCountry(country, page, size);
        return ResponseEntity.ok(ApiResponse.success("Employees retrieved successfully", response));
    }

    /**
     * Export all employees to CSV.
     * GET /employees/export
     */
    @GetMapping(value = "/export", produces = "text/csv")
    @Operation(summary = "Export employees as CSV", description = "Download full active employee directory as CSV")
    public ResponseEntity<String> exportEmployeesCsv() {
        String csvContent = csvExportService.exportEmployeesToCsv();
        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"employees.csv\"")
                .body(csvContent);
    }
}
