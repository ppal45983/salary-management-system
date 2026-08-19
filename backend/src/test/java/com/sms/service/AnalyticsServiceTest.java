package com.sms.service;

import com.sms.dto.DashboardMetricsDto;
import com.sms.dto.DepartmentDistributionDto;
import com.sms.dto.PayEquityDto;
import com.sms.entity.Department;
import com.sms.entity.Designation;
import com.sms.entity.Employee;
import com.sms.entity.SalaryRecord;
import com.sms.repository.DepartmentRepository;
import com.sms.repository.DesignationRepository;
import com.sms.repository.EmployeeRepository;
import com.sms.repository.SalaryRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AnalyticsService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AnalyticsService Unit Tests")
public class AnalyticsServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private SalaryRecordRepository salaryRecordRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private DesignationRepository designationRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    private Employee employee1;
    private Employee employee2;
    private SalaryRecord salary1;
    private SalaryRecord salary2;
    private Department dept1;
    private Designation desig1;

    @BeforeEach
    void setUp() {
        dept1 = new Department();
        dept1.setId(1L);
        dept1.setName("Engineering");
        dept1.setIsActive(true);

        desig1 = new Designation();
        desig1.setId(1L);
        desig1.setName("Senior Software Engineer");
        desig1.setIsActive(true);

        employee1 = new Employee();
        employee1.setId(1L);
        employee1.setDepartmentId(1L);
        employee1.setDesignationId(1L);
        employee1.setCountry("USA");
        employee1.setStatus("ACTIVE");
        employee1.setIsActive(true);

        employee2 = new Employee();
        employee2.setId(2L);
        employee2.setDepartmentId(1L);
        employee2.setDesignationId(1L);
        employee2.setCountry("UK");
        employee2.setStatus("ACTIVE");
        employee2.setIsActive(true);

        salary1 = new SalaryRecord();
        salary1.setId(1L);
        salary1.setEmployeeId(1L);
        salary1.setGrossSalary(new BigDecimal("100000"));
        salary1.setTax(new BigDecimal("20000"));
        salary1.setNetSalary(new BigDecimal("80000"));
        salary1.setCurrency("USD");
        salary1.setStatus("ACTIVE");
        salary1.setIsActive(true);

        salary2 = new SalaryRecord();
        salary2.setId(2L);
        salary2.setEmployeeId(2L);
        salary2.setGrossSalary(new BigDecimal("120000"));
        salary2.setTax(new BigDecimal("24000"));
        salary2.setNetSalary(new BigDecimal("96000"));
        salary2.setCurrency("USD");
        salary2.setStatus("ACTIVE");
        salary2.setIsActive(true);
    }

    @Test
    @DisplayName("Should calculate dashboard metrics accurately")
    void testGetDashboardMetrics() {
        when(employeeRepository.findAll()).thenReturn(List.of(employee1, employee2));
        when(salaryRecordRepository.findAll()).thenReturn(List.of(salary1, salary2));
        when(departmentRepository.count()).thenReturn(1L);
        when(departmentRepository.findAll()).thenReturn(List.of(dept1));
        when(designationRepository.count()).thenReturn(1L);

        DashboardMetricsDto metrics = analyticsService.getDashboardMetrics();

        assertNotNull(metrics);
        assertEquals(2, metrics.getTotalEmployees());
        assertEquals(2, metrics.getActiveEmployees());
        assertEquals(new BigDecimal("220000"), metrics.getTotalMonthlyPayrollByCurrency().get("USD"));
        assertEquals(1, metrics.getHeadcountByDepartment().size());
        assertEquals("Engineering", metrics.getHeadcountByDepartment().get(0).getDepartmentName());
    }

    @Test
    @DisplayName("Should compute department salary distribution (Min, Max, Avg, Median)")
    void testGetDepartmentDistributions() {
        when(departmentRepository.findAll()).thenReturn(List.of(dept1));
        when(employeeRepository.findAll()).thenReturn(List.of(employee1, employee2));
        when(salaryRecordRepository.findAll()).thenReturn(List.of(salary1, salary2));

        List<DepartmentDistributionDto> distributions = analyticsService.getDepartmentDistributions();

        assertNotNull(distributions);
        assertEquals(1, distributions.size());
        DepartmentDistributionDto engDist = distributions.get(0);
        assertEquals("Engineering", engDist.getDepartmentName());
        assertEquals(new BigDecimal("100000"), engDist.getMinSalary());
        assertEquals(new BigDecimal("120000"), engDist.getMaxSalary());
        assertEquals(new BigDecimal("110000.00"), engDist.getAverageSalary());
    }

    @Test
    @DisplayName("Should compute pay equity analysis by designation")
    void testGetPayEquityAnalysis() {
        when(designationRepository.findAll()).thenReturn(List.of(desig1));
        when(employeeRepository.findAll()).thenReturn(List.of(employee1, employee2));
        when(salaryRecordRepository.findAll()).thenReturn(List.of(salary1, salary2));

        List<PayEquityDto> payEquityList = analyticsService.getPayEquityAnalysis();

        assertNotNull(payEquityList);
        assertEquals(1, payEquityList.size());
        PayEquityDto pe = payEquityList.get(0);
        assertEquals("Senior Software Engineer", pe.getDesignationTitle());
        assertEquals(2, pe.getEmployeeCount());
        assertEquals(new BigDecimal("1.20"), pe.getSalarySpreadRatio());
    }
}
