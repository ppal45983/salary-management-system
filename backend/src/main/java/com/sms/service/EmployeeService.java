package com.sms.service;

import com.sms.dto.EmployeeDto;
import com.sms.dto.EmployeeResponseDto;
import com.sms.dto.PageResponse;
import com.sms.entity.Employee;
import com.sms.exception.BusinessException;
import com.sms.exception.ResourceNotFoundException;
import com.sms.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service for employee management operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AuditService auditService;

    /**
     * Create a new employee.
     */
    public EmployeeResponseDto createEmployee(EmployeeDto dto, String createdBy) {
        log.info("Creating new employee: {}", dto.getEmail());

        // Validate employee ID is unique
        if (employeeRepository.existsByEmployeeIdAndIsActiveTrue(dto.getEmployeeId())) {
            throw new BusinessException("EMPLOYEE_ID_EXISTS", 
                    "Employee ID already exists: " + dto.getEmployeeId());
        }

        // Validate email is unique
        if (employeeRepository.existsByEmailAndIsActiveTrue(dto.getEmail())) {
            throw new BusinessException("EMAIL_EXISTS", 
                    "Email already exists: " + dto.getEmail());
        }

        Employee employee = new Employee();
        employee.setEmployeeId(dto.getEmployeeId());
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setDateOfBirth(dto.getDateOfBirth());
        employee.setGender(dto.getGender());
        employee.setDepartmentId(dto.getDepartmentId());
        employee.setDesignationId(dto.getDesignationId());
        employee.setHireDate(dto.getHireDate());
        employee.setEmploymentType(dto.getEmploymentType());
        employee.setCountry(dto.getCountry());
        employee.setCurrency(dto.getCurrency());
        employee.setTaxId(dto.getTaxId());
        employee.setBankAccount(dto.getBankAccount());
        employee.setBankCode(dto.getBankCode());
        employee.setStatus(dto.getStatus());
        employee.setAddress(dto.getAddress());
        employee.setCity(dto.getCity());
        employee.setState(dto.getState());
        employee.setPostalCode(dto.getPostalCode());
        employee.setManagerId(dto.getManagerId());
        employee.setCreatedBy(createdBy);
        employee.setIsActive(true);

        Employee saved = employeeRepository.save(employee);
        log.info("Employee created successfully with ID: {}", saved.getId());

        auditService.logAction("CREATE", "EMPLOYEE", saved.getId(), createdBy, null, saved.toString());

        return mapToResponseDto(saved);
    }

    /**
     * Get employee by ID.
     */
    @Transactional(readOnly = true)
    public EmployeeResponseDto getEmployeeById(Long id) {
        log.debug("Fetching employee with ID: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        return mapToResponseDto(employee);
    }

    /**
     * Get employee by employee ID.
     */
    @Transactional(readOnly = true)
    public EmployeeResponseDto getEmployeeByEmployeeId(String employeeId) {
        log.debug("Fetching employee with employee ID: {}", employeeId);
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "employeeId", employeeId));
        return mapToResponseDto(employee);
    }

    /**
     * Update employee.
     */
    public EmployeeResponseDto updateEmployee(Long id, EmployeeDto dto, String updatedBy) {
        log.info("Updating employee with ID: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        String oldData = employee.toString();

        // Update fields
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setPhone(dto.getPhone());
        employee.setDateOfBirth(dto.getDateOfBirth());
        employee.setGender(dto.getGender());
        employee.setDepartmentId(dto.getDepartmentId());
        employee.setDesignationId(dto.getDesignationId());
        employee.setEmploymentType(dto.getEmploymentType());
        employee.setCountry(dto.getCountry());
        employee.setCurrency(dto.getCurrency());
        employee.setTaxId(dto.getTaxId());
        employee.setBankAccount(dto.getBankAccount());
        employee.setBankCode(dto.getBankCode());
        employee.setStatus(dto.getStatus());
        employee.setTerminationDate(dto.getTerminationDate());
        employee.setAddress(dto.getAddress());
        employee.setCity(dto.getCity());
        employee.setState(dto.getState());
        employee.setPostalCode(dto.getPostalCode());
        employee.setManagerId(dto.getManagerId());
        employee.setUpdatedBy(updatedBy);

        Employee updated = employeeRepository.save(employee);
        log.info("Employee updated successfully: {}", id);

        auditService.logAction("UPDATE", "EMPLOYEE", updated.getId(), updatedBy, oldData, updated.toString());

        return mapToResponseDto(updated);
    }

    /**
     * Deactivate employee.
     */
    public void deactivateEmployee(Long id, String deactivatedBy) {
        log.info("Deactivating employee with ID: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        employee.setIsActive(false);
        employee.setStatus("TERMINATED");
        employee.setTerminationDate(java.time.LocalDate.now());
        employee.setUpdatedBy(deactivatedBy);

        employeeRepository.save(employee);
        auditService.logAction("DELETE", "EMPLOYEE", id, deactivatedBy, null, "Employee deactivated");
    }

    /**
     * Get all employees with pagination.
     */
    @Transactional(readOnly = true)
    public PageResponse<EmployeeResponseDto> getAllEmployees(int page, int size, String sortBy) {
        log.debug("Fetching employees - page: {}, size: {}", page, size);

        Sort.Direction direction = Sort.Direction.ASC;
        String sortField = "id";

        if (sortBy != null) {
            if (sortBy.startsWith("-")) {
                direction = Sort.Direction.DESC;
                sortField = sortBy.substring(1);
            } else {
                sortField = sortBy;
            }
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<Employee> employeePage = employeeRepository.findByIsActiveTrue(pageable);

        return buildPageResponse(employeePage);
    }

    /**
     * Search employees.
     */
    @Transactional(readOnly = true)
    public PageResponse<EmployeeResponseDto> searchEmployees(String searchTerm, int page, int size) {
        log.debug("Searching employees with term: {}", searchTerm);

        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employeePage = employeeRepository.searchEmployees(searchTerm, pageable);

        return buildPageResponse(employeePage);
    }

    /**
     * Get employees by department.
     */
    @Transactional(readOnly = true)
    public PageResponse<EmployeeResponseDto> getEmployeesByDepartment(Long departmentId, int page, int size) {
        log.debug("Fetching employees for department: {}", departmentId);

        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employeePage = employeeRepository.findByDepartmentIdAndIsActiveTrue(departmentId, pageable);

        return buildPageResponse(employeePage);
    }

    /**
     * Get employees by status.
     */
    @Transactional(readOnly = true)
    public PageResponse<EmployeeResponseDto> getEmployeesByStatus(String status, int page, int size) {
        log.debug("Fetching employees with status: {}", status);

        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employeePage = employeeRepository.findByStatusAndIsActiveTrue(status, pageable);

        return buildPageResponse(employeePage);
    }

    /**
     * Get employees by country.
     */
    @Transactional(readOnly = true)
    public PageResponse<EmployeeResponseDto> getEmployeesByCountry(String country, int page, int size) {
        log.debug("Fetching employees from country: {}", country);

        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employeePage = employeeRepository.findByCountryAndIsActiveTrue(country, pageable);

        return buildPageResponse(employeePage);
    }

    /**
     * Map Employee entity to response DTO.
     */
    private EmployeeResponseDto mapToResponseDto(Employee employee) {
        return EmployeeResponseDto.builder()
                .id(employee.getId())
                .employeeId(employee.getEmployeeId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .dateOfBirth(employee.getDateOfBirth())
                .gender(employee.getGender())
                .departmentId(employee.getDepartmentId())
                .designationId(employee.getDesignationId())
                .hireDate(employee.getHireDate())
                .employmentType(employee.getEmploymentType())
                .country(employee.getCountry())
                .currency(employee.getCurrency())
                .status(employee.getStatus())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .createdBy(employee.getCreatedBy())
                .build();
    }

    /**
     * Build pagination response.
     */
    private PageResponse<EmployeeResponseDto> buildPageResponse(Page<Employee> page) {
        return PageResponse.<EmployeeResponseDto>builder()
                .content(page.getContent().stream()
                        .map(this::mapToResponseDto)
                        .toList())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}
