package com.sms.service;

import com.sms.dto.*;
import com.sms.entity.*;
import com.sms.exception.BusinessException;
import com.sms.exception.ResourceNotFoundException;
import com.sms.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SalaryService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SalaryService Unit Tests")
public class SalaryServiceTest {

    @Mock
    private SalaryRecordRepository salaryRecordRepository;

    @Mock
    private SalaryHistoryRepository salaryHistoryRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private DesignationRepository designationRepository;

    @Mock
    private TaxCalculationService taxCalculationService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private SalaryService salaryService;

    private Employee mockEmployee;
    private SalaryRecord mockSalaryRecord;
    private SalaryRecordDto mockSalaryDto;

    @BeforeEach
    void setUp() {
        mockEmployee = new Employee();
        mockEmployee.setId(1L);
        mockEmployee.setEmployeeId("EMP-001");
        mockEmployee.setFirstName("Jane");
        mockEmployee.setLastName("Smith");
        mockEmployee.setEmail("jane.smith@acme.com");
        mockEmployee.setCountry("USA");
        mockEmployee.setCurrency("USD");
        mockEmployee.setDepartmentId(1L);
        mockEmployee.setDesignationId(2L);
        mockEmployee.setIsActive(true);

        mockSalaryRecord = new SalaryRecord();
        mockSalaryRecord.setId(10L);
        mockSalaryRecord.setEmployeeId(1L);
        mockSalaryRecord.setBaseSalary(new BigDecimal("100000"));
        mockSalaryRecord.setAllowances(new BigDecimal("15000"));
        mockSalaryRecord.setDeductions(new BigDecimal("5000"));
        mockSalaryRecord.setGrossSalary(new BigDecimal("115000"));
        mockSalaryRecord.setTax(new BigDecimal("23000"));
        mockSalaryRecord.setNetSalary(new BigDecimal("87000"));
        mockSalaryRecord.setEffectiveDate(LocalDate.of(2024, 1, 1));
        mockSalaryRecord.setStatus("ACTIVE");
        mockSalaryRecord.setPayFrequency("MONTHLY");
        mockSalaryRecord.setCurrency("USD");
        mockSalaryRecord.setIsActive(true);

        mockSalaryDto = new SalaryRecordDto();
        mockSalaryDto.setEmployeeId(1L);
        mockSalaryDto.setBaseSalary(new BigDecimal("100000"));
        mockSalaryDto.setAllowances(new BigDecimal("15000"));
        mockSalaryDto.setDeductions(new BigDecimal("5000"));
        mockSalaryDto.setEffectiveDate(LocalDate.of(2024, 1, 1));
        mockSalaryDto.setPayFrequency("MONTHLY");
        mockSalaryDto.setCurrency("USD");
        mockSalaryDto.setStatus("ACTIVE");
    }

    @Test
    @DisplayName("Should create salary record and compute progressive tax and net pay")
    void testCreateSalaryRecord_Success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
        when(taxCalculationService.calculateTax(eq(new BigDecimal("115000")), eq("USA"), any(Integer.class)))
                .thenReturn(new BigDecimal("23000"));
        when(salaryRecordRepository.save(any(SalaryRecord.class))).thenReturn(mockSalaryRecord);

        SalaryRecordResponseDto result = salaryService.createSalaryRecord(mockSalaryDto, "hr_manager");

        assertNotNull(result);
        assertEquals(new BigDecimal("100000"), result.getBaseSalary());
        assertEquals(new BigDecimal("115000"), result.getGrossSalary());
        assertEquals(new BigDecimal("23000"), result.getTax());
        assertEquals(new BigDecimal("87000"), result.getNetSalary());
        verify(salaryRecordRepository, times(1)).save(any(SalaryRecord.class));
    }

    @Test
    @DisplayName("Should approve salary record and record history")
    void testApproveSalaryRecord() {
        mockSalaryRecord.setStatus("INACTIVE");
        when(salaryRecordRepository.findById(10L)).thenReturn(Optional.of(mockSalaryRecord));
        when(salaryRecordRepository.save(any(SalaryRecord.class))).thenReturn(mockSalaryRecord);

        SalaryRecordResponseDto result = salaryService.approveSalaryRecord(10L, "hr_director");

        assertNotNull(result);
        assertEquals("ACTIVE", mockSalaryRecord.getStatus());
        verify(salaryHistoryRepository, times(1)).save(any(SalaryHistory.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when trying to approve an already active salary")
    void testApproveSalaryRecord_AlreadyActive() {
        mockSalaryRecord.setStatus("ACTIVE");
        when(salaryRecordRepository.findById(10L)).thenReturn(Optional.of(mockSalaryRecord));

        assertThrows(BusinessException.class, () -> {
            salaryService.approveSalaryRecord(10L, "hr_director");
        });
    }

    @Test
    @DisplayName("Should generate complete salary slip")
    void testGenerateSalarySlip() {
        Department dept = new Department();
        dept.setId(1L);
        dept.setName("Engineering");

        Designation desig = new Designation();
        desig.setId(2L);
        desig.setName("Lead Architect");

        when(salaryRecordRepository.findById(10L)).thenReturn(Optional.of(mockSalaryRecord));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(dept));
        when(designationRepository.findById(2L)).thenReturn(Optional.of(desig));

        TaxCalculationResponseDto taxCalc = TaxCalculationResponseDto.builder()
                .effectiveTaxRate(new BigDecimal("20.00"))
                .breakdown(List.of())
                .build();
        when(taxCalculationService.calculateTaxWithBreakdown(any(), any(), any(), any(), any(), any()))
                .thenReturn(taxCalc);

        SalarySlipDto slip = salaryService.generateSalarySlip(10L);

        assertNotNull(slip);
        assertEquals(1L, slip.getEmployeeId());
        assertEquals("Engineering", slip.getDepartmentName());
        assertEquals("Lead Architect", slip.getDesignationTitle());
        assertEquals(new BigDecimal("87000"), slip.getNetSalary());
    }
}
