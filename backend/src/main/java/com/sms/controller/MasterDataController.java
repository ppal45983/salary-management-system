package com.sms.controller;

import com.sms.dto.ApiResponse;
import com.sms.dto.DepartmentDto;
import com.sms.dto.DesignationDto;
import com.sms.dto.TaxBracketDto;
import com.sms.service.MasterDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Master Data (Departments, Designations, Tax Brackets, Countries).
 */
@RestController
@RequestMapping("/masters")
@RequiredArgsConstructor
@Tag(name = "Master Data", description = "APIs for master lookups (departments, designations, tax rates, countries)")
@SecurityRequirement(name = "bearer-jwt")
public class MasterDataController {

    private final MasterDataService masterDataService;

    /**
     * Get all departments.
     * GET /masters/departments
     */
    @GetMapping("/departments")
    @Operation(summary = "Get all departments", description = "Retrieve list of all active departments with employee headcounts")
    public ResponseEntity<ApiResponse<List<DepartmentDto>>> getAllDepartments() {
        List<DepartmentDto> response = masterDataService.getAllDepartments();
        return ResponseEntity.ok(ApiResponse.success("Departments retrieved successfully", response));
    }

    /**
     * Get department by ID.
     * GET /masters/departments/{id}
     */
    @GetMapping("/departments/{id}")
    @Operation(summary = "Get department by ID", description = "Retrieve single department details")
    public ResponseEntity<ApiResponse<DepartmentDto>> getDepartmentById(@PathVariable Long id) {
        DepartmentDto response = masterDataService.getDepartmentById(id);
        return ResponseEntity.ok(ApiResponse.success("Department retrieved successfully", response));
    }

    /**
     * Get all designations.
     * GET /masters/designations
     */
    @GetMapping("/designations")
    @Operation(summary = "Get all designations", description = "Retrieve list of all active designations and job levels")
    public ResponseEntity<ApiResponse<List<DesignationDto>>> getAllDesignations() {
        List<DesignationDto> response = masterDataService.getAllDesignations();
        return ResponseEntity.ok(ApiResponse.success("Designations retrieved successfully", response));
    }

    /**
     * Get tax brackets.
     * GET /masters/tax-brackets
     */
    @GetMapping("/tax-brackets")
    @Operation(summary = "Get tax brackets", description = "Retrieve tax brackets with optional country and tax year filters")
    public ResponseEntity<ApiResponse<List<TaxBracketDto>>> getTaxBrackets(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) Integer taxYear) {
        List<TaxBracketDto> response = masterDataService.getTaxBrackets(country, taxYear);
        return ResponseEntity.ok(ApiResponse.success("Tax brackets retrieved successfully", response));
    }

    /**
     * Get supported countries.
     * GET /masters/countries
     */
    @GetMapping("/countries")
    @Operation(summary = "Get supported countries", description = "Retrieve list of 9 supported countries and their ISO currencies")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getSupportedCountries() {
        List<Map<String, String>> response = masterDataService.getSupportedCountries();
        return ResponseEntity.ok(ApiResponse.success("Supported countries retrieved successfully", response));
    }
}
