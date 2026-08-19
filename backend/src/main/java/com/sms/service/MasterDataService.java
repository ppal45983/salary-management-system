package com.sms.service;

import com.sms.dto.DepartmentDto;
import com.sms.dto.DesignationDto;
import com.sms.dto.TaxBracketDto;
import com.sms.entity.Department;
import com.sms.entity.Designation;
import com.sms.entity.TaxBracket;
import com.sms.exception.BusinessException;
import com.sms.exception.ResourceNotFoundException;
import com.sms.repository.DepartmentRepository;
import com.sms.repository.DesignationRepository;
import com.sms.repository.EmployeeRepository;
import com.sms.repository.TaxBracketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for master data operations (Departments, Designations, Tax Brackets, Countries).
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MasterDataService {

    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final TaxBracketRepository taxBracketRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * Get all departments.
     */
    public List<DepartmentDto> getAllDepartments() {
        Map<Long, Long> empCounts = employeeRepository.findAll().stream()
                .filter(e -> Boolean.TRUE.equals(e.getIsActive()))
                .collect(Collectors.groupingBy(e -> e.getDepartmentId() != null ? e.getDepartmentId() : 0L, Collectors.counting()));

        return departmentRepository.findAll().stream()
                .filter(d -> Boolean.TRUE.equals(d.getIsActive()))
                .map(d -> DepartmentDto.builder()
                        .id(d.getId())
                        .name(d.getName())
                        .description(d.getDescription())
                        .departmentCode(d.getDepartmentCode())
                        .managerId(d.getManagerId())
                        .budget(d.getBudget())
                        .location(d.getLocation())
                        .employeeCount(empCounts.getOrDefault(d.getId(), 0L))
                        .build())
                .toList();
    }

    /**
     * Get department by ID.
     */
    public DepartmentDto getDepartmentById(Long id) {
        Department d = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
        return DepartmentDto.builder()
                .id(d.getId())
                .name(d.getName())
                .description(d.getDescription())
                .departmentCode(d.getDepartmentCode())
                .managerId(d.getManagerId())
                .budget(d.getBudget())
                .location(d.getLocation())
                .build();
    }

    /**
     * Get all designations.
     */
    public List<DesignationDto> getAllDesignations() {
        Map<Long, String> deptMap = departmentRepository.findAll().stream()
                .collect(Collectors.toMap(Department::getId, Department::getName));

        Map<Long, Long> empCounts = employeeRepository.findAll().stream()
                .filter(e -> Boolean.TRUE.equals(e.getIsActive()))
                .collect(Collectors.groupingBy(e -> e.getDesignationId() != null ? e.getDesignationId() : 0L, Collectors.counting()));

        return designationRepository.findAll().stream()
                .filter(d -> Boolean.TRUE.equals(d.getIsActive()))
                .map(d -> DesignationDto.builder()
                        .id(d.getId())
                        .title(d.getName())
                        .description(d.getDescription())
                        .level(d.getLevel())
                        .minSalary(d.getMinSalary())
                        .maxSalary(d.getMaxSalary())
                        .departmentName(d.getDepartmentId() != null ? deptMap.getOrDefault(d.getDepartmentId(), "General") : "General")
                        .employeeCount(empCounts.getOrDefault(d.getId(), 0L))
                        .build())
                .toList();
    }

    /**
     * Get all tax brackets for country and year.
     */
    public List<TaxBracketDto> getTaxBrackets(String country, Integer taxYear) {
        List<TaxBracket> brackets;
        if (country != null && taxYear != null) {
            brackets = taxBracketRepository.findByCountryAndTaxYearAndIsActiveTrueOrderByIncomeFrom(country, taxYear);
        } else if (country != null) {
            brackets = taxBracketRepository.findAll().stream()
                    .filter(t -> country.equalsIgnoreCase(t.getCountry()) && Boolean.TRUE.equals(t.getIsActive()))
                    .toList();
        } else {
            brackets = taxBracketRepository.findAll().stream()
                    .filter(t -> Boolean.TRUE.equals(t.getIsActive()))
                    .toList();
        }

        return brackets.stream()
                .map(t -> TaxBracketDto.builder()
                        .id(t.getId())
                        .country(t.getCountry())
                        .taxYear(t.getTaxYear())
                        .incomeFrom(t.getIncomeFrom())
                        .incomeTo(t.getIncomeTo())
                        .taxRate(t.getTaxRate())
                        .effectiveFrom(t.getEffectiveFrom())
                        .effectiveTo(t.getEffectiveTo())
                        .description(t.getDescription())
                        .currency(t.getCurrency())
                        .build())
                .toList();
    }

    /**
     * Get list of supported countries with their standard currencies.
     */
    public List<Map<String, String>> getSupportedCountries() {
        return List.of(
                Map.of("code", "US", "name", "United States", "currency", "USD", "symbol", "$"),
                Map.of("code", "GB", "name", "United Kingdom", "currency", "GBP", "symbol", "£"),
                Map.of("code", "IN", "name", "India", "currency", "INR", "symbol", "₹"),
                Map.of("code", "DE", "name", "Germany", "currency", "EUR", "symbol", "€"),
                Map.of("code", "FR", "name", "France", "currency", "EUR", "symbol", "€"),
                Map.of("code", "CA", "name", "Canada", "currency", "CAD", "symbol", "$"),
                Map.of("code", "AU", "name", "Australia", "currency", "AUD", "symbol", "$"),
                Map.of("code", "JP", "name", "Japan", "currency", "JPY", "symbol", "¥"),
                Map.of("code", "SG", "name", "Singapore", "currency", "SGD", "symbol", "$")
        );
    }
}
