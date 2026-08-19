package com.sms.service;

import com.sms.dto.*;
import com.sms.entity.Employee;
import com.sms.entity.SalaryRecord;
import com.sms.entity.SalaryHistory;
import com.sms.entity.Department;
import com.sms.entity.Designation;
import com.sms.exception.BusinessException;
import com.sms.exception.ResourceNotFoundException;
import com.sms.repository.SalaryRecordRepository;
import com.sms.repository.SalaryHistoryRepository;
import com.sms.repository.EmployeeRepository;
import com.sms.repository.DepartmentRepository;
import com.sms.repository.DesignationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service for salary management operations including creation, updates, tax calculation, slips, and approvals.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SalaryService {

    private final SalaryRecordRepository salaryRecordRepository;
    private final SalaryHistoryRepository salaryHistoryRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final TaxCalculationService taxCalculationService;
    private final AuditService auditService;

    /**
     * Create a new salary record.
     */
    public SalaryRecordResponseDto createSalaryRecord(SalaryRecordDto dto, String createdBy) {
        log.info("Creating salary record for employee: {}", dto.getEmployeeId());

        // Verify employee exists
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", dto.getEmployeeId()));

        // Calculate tax based on employee country & gross salary
        BigDecimal allowances = dto.getAllowances() != null ? dto.getAllowances() : BigDecimal.ZERO;
        BigDecimal deductions = dto.getDeductions() != null ? dto.getDeductions() : BigDecimal.ZERO;
        BigDecimal grossSalary = dto.getBaseSalary().add(allowances);

        int taxYear = dto.getEffectiveDate() != null ? dto.getEffectiveDate().getYear() : LocalDate.now().getYear();
        BigDecimal tax = taxCalculationService.calculateTax(grossSalary, employee.getCountry(), taxYear);
        BigDecimal netSalary = grossSalary.subtract(deductions).subtract(tax);

        // If status is ACTIVE, deactivate previous active salary records
        String status = dto.getStatus() != null ? dto.getStatus() : "ACTIVE";
        if ("ACTIVE".equalsIgnoreCase(status)) {
            salaryRecordRepository.findActiveSalaryByEmployeeId(dto.getEmployeeId()).ifPresent(previous -> {
                previous.setStatus("INACTIVE");
                if (dto.getEffectiveDate() != null) {
                    previous.setEndDate(dto.getEffectiveDate().minusDays(1));
                } else {
                    previous.setEndDate(LocalDate.now());
                }
                salaryRecordRepository.save(previous);
            });
        }

        // Create salary record
        SalaryRecord salaryRecord = new SalaryRecord();
        salaryRecord.setEmployeeId(dto.getEmployeeId());
        salaryRecord.setBaseSalary(dto.getBaseSalary());
        salaryRecord.setAllowances(allowances);
        salaryRecord.setDeductions(deductions);
        salaryRecord.setGrossSalary(grossSalary);
        salaryRecord.setTax(tax);
        salaryRecord.setNetSalary(netSalary);
        salaryRecord.setEffectiveDate(dto.getEffectiveDate() != null ? dto.getEffectiveDate() : LocalDate.now());
        salaryRecord.setEndDate(dto.getEndDate());
        salaryRecord.setStatus(status.toUpperCase());
        salaryRecord.setPayFrequency(dto.getPayFrequency() != null ? dto.getPayFrequency() : "MONTHLY");
        salaryRecord.setCurrency(dto.getCurrency() != null ? dto.getCurrency() : employee.getCurrency());
        salaryRecord.setComments(dto.getComments());
        salaryRecord.setCreatedBy(createdBy);
        salaryRecord.setIsActive(true);

        if ("ACTIVE".equalsIgnoreCase(status)) {
            salaryRecord.setApprovedAt(java.time.LocalDateTime.now());
        }

        SalaryRecord saved = salaryRecordRepository.save(salaryRecord);
        log.info("Salary record created with ID: {}", saved.getId());

        auditService.logAction("CREATE", "SALARY_RECORD", saved.getId(), createdBy, null, saved.toString());

        // Create initial history record
        createSalaryHistory(saved, "INITIAL", "Initial salary record created", createdBy);

        return mapToResponseDto(saved);
    }

    /**
     * Approve a salary record.
     */
    public SalaryRecordResponseDto approveSalaryRecord(Long salaryRecordId, String approvedBy) {
        log.info("Approving salary record: {}", salaryRecordId);

        SalaryRecord salaryRecord = salaryRecordRepository.findById(salaryRecordId)
                .orElseThrow(() -> new ResourceNotFoundException("SalaryRecord", "id", salaryRecordId));

        if ("ACTIVE".equals(salaryRecord.getStatus())) {
            throw new BusinessException("ALREADY_APPROVED", "Salary record is already approved and active");
        }

        // Deactivate any existing active salary for this employee
        salaryRecordRepository.findActiveSalaryByEmployeeId(salaryRecord.getEmployeeId()).ifPresent(previous -> {
            previous.setStatus("INACTIVE");
            previous.setEndDate(salaryRecord.getEffectiveDate().minusDays(1));
            salaryRecordRepository.save(previous);
        });

        // Approve the new salary record
        salaryRecord.setStatus("ACTIVE");
        salaryRecord.setApprovedAt(java.time.LocalDateTime.now());

        SalaryRecord saved = salaryRecordRepository.save(salaryRecord);
        log.info("Salary record approved: {}", salaryRecordId);

        auditService.logAction("APPROVE", "SALARY_RECORD", saved.getId(), approvedBy, null, "Approved");
        createSalaryHistory(saved, "APPROVAL", "Salary record approved", approvedBy);

        return mapToResponseDto(saved);
    }

    /**
     * Get all salary records with pagination and optional employee/status filters.
     */
    @Transactional(readOnly = true)
    public PageResponse<SalaryRecordResponseDto> getAllSalaries(int page, int size, Long employeeId, String status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SalaryRecord> salaryPage;

        if (employeeId != null) {
            salaryPage = salaryRecordRepository.findByEmployeeIdAndIsActiveTrueOrderByCreatedAtDesc(employeeId, pageable);
        } else if (status != null && !status.trim().isEmpty()) {
            salaryPage = salaryRecordRepository.findByStatusAndIsActiveTrueOrderByCreatedAtDesc(status.toUpperCase(), pageable);
        } else {
            salaryPage = salaryRecordRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable);
        }

        return buildPageResponse(salaryPage);
    }

    /**
     * Get salary record by ID.
     */
    @Transactional(readOnly = true)
    public SalaryRecordResponseDto getSalaryRecordById(Long id) {
        SalaryRecord salaryRecord = salaryRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SalaryRecord", "id", id));
        return mapToResponseDto(salaryRecord);
    }

    /**
     * Get active salary for an employee.
     */
    @Transactional(readOnly = true)
    public SalaryRecordResponseDto getActiveSalaryForEmployee(Long employeeId) {
        SalaryRecord salaryRecord = salaryRecordRepository.findActiveSalaryByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("SalaryRecord", "employeeId", employeeId));
        return mapToResponseDto(salaryRecord);
    }

    /**
     * Get all salary records for an employee.
     */
    @Transactional(readOnly = true)
    public PageResponse<SalaryRecordResponseDto> getSalaryRecordsForEmployee(Long employeeId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SalaryRecord> salaryPage = salaryRecordRepository
                .findByEmployeeIdOrderByEffectiveDateDesc(employeeId, pageable);
        return buildPageResponse(salaryPage);
    }

    /**
     * Get salary progression history for an employee.
     */
    @Transactional(readOnly = true)
    public List<SalaryHistory> getSalaryHistoryForEmployee(Long employeeId) {
        return salaryHistoryRepository.findByEmployeeIdOrderByEffectiveDateDesc(employeeId, PageRequest.of(0, 100)).getContent();
    }

    /**
     * Get pending approvals.
     */
    @Transactional(readOnly = true)
    public PageResponse<SalaryRecordResponseDto> getPendingApprovals(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SalaryRecord> salaryPage = salaryRecordRepository.findPendingApproval(pageable);
        return buildPageResponse(salaryPage);
    }

    /**
     * Update salary record.
     */
    public SalaryRecordResponseDto updateSalaryRecord(Long id, SalaryRecordDto dto, String updatedBy) {
        log.info("Updating salary record: {}", id);

        SalaryRecord salaryRecord = salaryRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SalaryRecord", "id", id));

        String oldData = salaryRecord.toString();
        Employee employee = employeeRepository.findById(salaryRecord.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", salaryRecord.getEmployeeId()));

        BigDecimal allowances = dto.getAllowances() != null ? dto.getAllowances() : BigDecimal.ZERO;
        BigDecimal deductions = dto.getDeductions() != null ? dto.getDeductions() : BigDecimal.ZERO;
        BigDecimal grossSalary = dto.getBaseSalary().add(allowances);

        int taxYear = dto.getEffectiveDate() != null ? dto.getEffectiveDate().getYear() : LocalDate.now().getYear();
        BigDecimal tax = taxCalculationService.calculateTax(grossSalary, employee.getCountry(), taxYear);
        BigDecimal netSalary = grossSalary.subtract(deductions).subtract(tax);

        salaryRecord.setBaseSalary(dto.getBaseSalary());
        salaryRecord.setAllowances(allowances);
        salaryRecord.setDeductions(deductions);
        salaryRecord.setGrossSalary(grossSalary);
        salaryRecord.setTax(tax);
        salaryRecord.setNetSalary(netSalary);
        if (dto.getEffectiveDate() != null) salaryRecord.setEffectiveDate(dto.getEffectiveDate());
        if (dto.getEndDate() != null) salaryRecord.setEndDate(dto.getEndDate());
        if (dto.getPayFrequency() != null) salaryRecord.setPayFrequency(dto.getPayFrequency());
        if (dto.getCurrency() != null) salaryRecord.setCurrency(dto.getCurrency());
        if (dto.getComments() != null) salaryRecord.setComments(dto.getComments());
        salaryRecord.setUpdatedBy(updatedBy);

        SalaryRecord saved = salaryRecordRepository.save(salaryRecord);
        log.info("Salary record updated: {}", id);

        auditService.logAction("UPDATE", "SALARY_RECORD", saved.getId(), updatedBy, oldData, saved.toString());
        createSalaryHistory(saved, "REVISED", "Salary record updated", updatedBy);

        return mapToResponseDto(saved);
    }

    /**
     * Delete / deactivate salary record.
     */
    public void deleteSalaryRecord(Long id, String deletedBy) {
        SalaryRecord salaryRecord = salaryRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SalaryRecord", "id", id));
        salaryRecord.setIsActive(false);
        salaryRecord.setStatus("ARCHIVED");
        salaryRecord.setUpdatedBy(deletedBy);
        salaryRecordRepository.save(salaryRecord);
        auditService.logAction("DELETE", "SALARY_RECORD", id, deletedBy, null, "Archived salary record");
    }

    /**
     * Real-time Tax preview calculation.
     */
    @Transactional(readOnly = true)
    public TaxCalculationResponseDto calculateTaxPreview(TaxCalculationRequestDto dto) {
        return taxCalculationService.calculateTaxWithBreakdown(
                dto.getBaseSalary(),
                dto.getAllowances(),
                dto.getDeductions(),
                dto.getCountry(),
                dto.getTaxYear() != null ? dto.getTaxYear() : LocalDate.now().getYear(),
                dto.getCurrency()
        );
    }

    /**
     * Generate structured Salary Slip DTO.
     */
    @Transactional(readOnly = true)
    public SalarySlipDto generateSalarySlip(Long salaryRecordId) {
        SalaryRecord salaryRecord = salaryRecordRepository.findById(salaryRecordId)
                .orElseThrow(() -> new ResourceNotFoundException("SalaryRecord", "id", salaryRecordId));

        Employee employee = employeeRepository.findById(salaryRecord.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", salaryRecord.getEmployeeId()));

        Department department = departmentRepository.findById(employee.getDepartmentId()).orElse(null);
        Designation designation = designationRepository.findById(employee.getDesignationId()).orElse(null);

        TaxCalculationResponseDto taxCalc = taxCalculationService.calculateTaxWithBreakdown(
                salaryRecord.getBaseSalary(),
                salaryRecord.getAllowances(),
                salaryRecord.getDeductions(),
                employee.getCountry(),
                salaryRecord.getEffectiveDate().getYear(),
                salaryRecord.getCurrency()
        );

        String monthYear = salaryRecord.getEffectiveDate().format(DateTimeFormatter.ofPattern("MMMM yyyy"));

        return SalarySlipDto.builder()
                .slipNumber("PS-" + salaryRecord.getId() + "-" + salaryRecord.getEffectiveDate().format(DateTimeFormatter.ofPattern("yyyyMM")))
                .salaryRecordId(salaryRecord.getId())
                .payPeriod(monthYear)
                .generatedDate(LocalDate.now())
                .employeeId(employee.getId())
                .employeeCode(employee.getEmployeeId())
                .employeeName(employee.getFirstName() + " " + employee.getLastName())
                .email(employee.getEmail())
                .departmentName(department != null ? department.getName() : "General")
                .designationTitle(designation != null ? designation.getTitle() : "Staff")
                .country(employee.getCountry())
                .currency(salaryRecord.getCurrency())
                .taxId(employee.getTaxId() != null ? employee.getTaxId() : "N/A")
                .bankAccount(employee.getBankAccount() != null ? employee.getBankAccount() : "Direct Deposit")
                .bankCode(employee.getBankCode() != null ? employee.getBankCode() : "N/A")
                .baseSalary(salaryRecord.getBaseSalary())
                .allowances(salaryRecord.getAllowances() != null ? salaryRecord.getAllowances() : BigDecimal.ZERO)
                .grossSalary(salaryRecord.getGrossSalary())
                .standardDeductions(salaryRecord.getDeductions() != null ? salaryRecord.getDeductions() : BigDecimal.ZERO)
                .incomeTax(salaryRecord.getTax())
                .totalDeductions((salaryRecord.getDeductions() != null ? salaryRecord.getDeductions() : BigDecimal.ZERO).add(salaryRecord.getTax()))
                .netSalary(salaryRecord.getNetSalary())
                .effectiveTaxRate(taxCalc.getEffectiveTaxRate())
                .taxBreakdown(taxCalc.getBreakdown())
                .companyName("ACME Global Corporation")
                .companyAddress("100 Innovation Parkway, Suite 500")
                .build();
    }

    /**
     * Create salary history entry.
     */
    private void createSalaryHistory(SalaryRecord salaryRecord, String changeType, String reason, String changedBy) {
        SalaryHistory history = new SalaryHistory();
        history.setEmployeeId(salaryRecord.getEmployeeId());
        history.setSalaryRecordId(salaryRecord.getId());
        history.setBaseSalary(salaryRecord.getBaseSalary());
        history.setAllowances(salaryRecord.getAllowances());
        history.setDeductions(salaryRecord.getDeductions());
        history.setGrossSalary(salaryRecord.getGrossSalary());
        history.setTax(salaryRecord.getTax());
        history.setNetSalary(salaryRecord.getNetSalary());
        history.setEffectiveDate(salaryRecord.getEffectiveDate());
        history.setChangeType(changeType);
        history.setChangeReason(reason);
        history.setCurrency(salaryRecord.getCurrency());
        history.setCreatedBy(changedBy);
        history.setIsActive(true);

        salaryHistoryRepository.save(history);
    }

    /**
     * Map SalaryRecord entity to response DTO with employee details.
     */
    private SalaryRecordResponseDto mapToResponseDto(SalaryRecord salaryRecord) {
        var response = SalaryRecordResponseDto.builder()
                .id(salaryRecord.getId())
                .employeeId(salaryRecord.getEmployeeId())
                .baseSalary(salaryRecord.getBaseSalary())
                .allowances(salaryRecord.getAllowances())
                .deductions(salaryRecord.getDeductions())
                .grossSalary(salaryRecord.getGrossSalary())
                .tax(salaryRecord.getTax())
                .netSalary(salaryRecord.getNetSalary())
                .effectiveDate(salaryRecord.getEffectiveDate())
                .endDate(salaryRecord.getEndDate())
                .status(salaryRecord.getStatus())
                .payFrequency(salaryRecord.getPayFrequency())
                .currency(salaryRecord.getCurrency())
                .approvedBy(salaryRecord.getApprovedBy())
                .approvedAt(salaryRecord.getApprovedAt())
                .comments(salaryRecord.getComments())
                .createdAt(salaryRecord.getCreatedAt())
                .build();

        // Enrich with employee name, code, and country
        employeeRepository.findById(salaryRecord.getEmployeeId()).ifPresent(emp -> {
            response.setEmployeeCode(emp.getEmployeeId());
            response.setEmployeeName(emp.getFirstName() + " " + emp.getLastName());
            response.setCountry(emp.getCountry());
        });

        return response;
    }

    /**
     * Build pagination response.
     */
    private PageResponse<SalaryRecordResponseDto> buildPageResponse(Page<SalaryRecord> page) {
        return PageResponse.<SalaryRecordResponseDto>builder()
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
