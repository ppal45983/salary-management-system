package com.sms.service;

import com.sms.dto.EmployeeDto;
import com.sms.dto.EmployeeResponseDto;
import com.sms.dto.PageResponse;
import com.sms.entity.Employee;
import com.sms.exception.BusinessException;
import com.sms.exception.ResourceNotFoundException;
import com.sms.repository.EmployeeRepository;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EmployeeService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeService Unit Tests")
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee mockEmployee;
    private EmployeeDto mockEmployeeDto;

    @BeforeEach
    void setUp() {
        mockEmployee = new Employee();
        mockEmployee.setId(1L);
        mockEmployee.setEmployeeId("EMP-001");
        mockEmployee.setFirstName("John");
        mockEmployee.setLastName("Doe");
        mockEmployee.setEmail("john.doe@acme.com");
        mockEmployee.setDepartmentId(1L);
        mockEmployee.setDesignationId(2L);
        mockEmployee.setCountry("USA");
        mockEmployee.setCurrency("USD");
        mockEmployee.setStatus("ACTIVE");
        mockEmployee.setHireDate(LocalDate.of(2023, 1, 15));
        mockEmployee.setIsActive(true);

        mockEmployeeDto = new EmployeeDto();
        mockEmployeeDto.setEmployeeId("EMP-001");
        mockEmployeeDto.setFirstName("John");
        mockEmployeeDto.setLastName("Doe");
        mockEmployeeDto.setEmail("john.doe@acme.com");
        mockEmployeeDto.setDepartmentId(1L);
        mockEmployeeDto.setDesignationId(2L);
        mockEmployeeDto.setCountry("USA");
        mockEmployeeDto.setCurrency("USD");
        mockEmployeeDto.setStatus("ACTIVE");
        mockEmployeeDto.setHireDate(LocalDate.of(2023, 1, 15));
    }

    @Test
    @DisplayName("Should create employee successfully when data is valid")
    void testCreateEmployee_Success() {
        when(employeeRepository.existsByEmployeeIdAndIsActiveTrue("EMP-001")).thenReturn(false);
        when(employeeRepository.existsByEmailAndIsActiveTrue("john.doe@acme.com")).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(mockEmployee);

        EmployeeResponseDto result = employeeService.createEmployee(mockEmployeeDto, "admin");

        assertNotNull(result);
        assertEquals("EMP-001", result.getEmployeeId());
        assertEquals("John", result.getFirstName());
        assertEquals("john.doe@acme.com", result.getEmail());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when duplicate employee ID exists")
    void testCreateEmployee_DuplicateEmployeeId() {
        when(employeeRepository.existsByEmployeeIdAndIsActiveTrue("EMP-001")).thenReturn(true);

        assertThrows(BusinessException.class, () -> {
            employeeService.createEmployee(mockEmployeeDto, "admin");
        });
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when duplicate email exists")
    void testCreateEmployee_DuplicateEmail() {
        when(employeeRepository.existsByEmployeeIdAndIsActiveTrue("EMP-001")).thenReturn(false);
        when(employeeRepository.existsByEmailAndIsActiveTrue("john.doe@acme.com")).thenReturn(true);

        assertThrows(BusinessException.class, () -> {
            employeeService.createEmployee(mockEmployeeDto, "admin");
        });
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should retrieve employee by ID")
    void testGetEmployeeById_Found() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));

        EmployeeResponseDto result = employeeService.getEmployeeById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when employee ID not found")
    void testGetEmployeeById_NotFound() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.getEmployeeById(99L);
        });
    }

    @Test
    @DisplayName("Should return paginated list of employees")
    void testGetAllEmployees() {
        Page<Employee> page = new PageImpl<>(List.of(mockEmployee));
        when(employeeRepository.findByIsActiveTrue(any(Pageable.class))).thenReturn(page);

        PageResponse<EmployeeResponseDto> response = employeeService.getAllEmployees(0, 10, null);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals("EMP-001", response.getContent().get(0).getEmployeeId());
    }

    @Test
    @DisplayName("Should deactivate employee successfully")
    void testDeactivateEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(mockEmployee);

        assertDoesNotThrow(() -> {
            employeeService.deactivateEmployee(1L, "admin");
        });

        assertEquals("INACTIVE", mockEmployee.getStatus());
        assertFalse(mockEmployee.getIsActive());
        verify(employeeRepository, times(1)).save(mockEmployee);
    }
}
