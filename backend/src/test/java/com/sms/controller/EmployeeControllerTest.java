package com.sms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sms.dto.EmployeeDto;
import com.sms.dto.EmployeeResponseDto;
import com.sms.dto.PageResponse;
import com.sms.service.CsvExportService;
import com.sms.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private CsvExportService csvExportService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private EmployeeController employeeController;

    private ObjectMapper objectMapper;
    private EmployeeDto testDto;
    private EmployeeResponseDto testResponseDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testDto = EmployeeDto.builder()
                .firstName("Pramod")
                .lastName("Pal")
                .email("pramod.pal@acme.com")
                .phone("+918299494481")
                .departmentId(1L)
                .designationId(1L)
                .hireDate(LocalDate.now())
                .country("India")
                .currency("INR")
                .status("ACTIVE")
                .build();

        testResponseDto = EmployeeResponseDto.builder()
                .id(1L)
                .employeeId("EMP-00001")
                .firstName("Pramod")
                .lastName("Pal")
                .email("pramod.pal@acme.com")
                .departmentId(1L)
                .departmentName("Engineering")
                .designationId(1L)
                .designationTitle("Software Engineer")
                .country("India")
                .currency("INR")
                .status("ACTIVE")
                .build();
    }

    @Test
    @DisplayName("POST /employees - Should successfully create employee (CRUD: Create)")
    void testCreateEmployee() throws Exception {
        when(employeeService.createEmployee(any(EmployeeDto.class), anyString())).thenReturn(testResponseDto);

        mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDto))
                .principal(() -> "hr_manager"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.employeeId").value("EMP-00001"))
                .andExpect(jsonPath("$.data.email").value("pramod.pal@acme.com"));

        verify(employeeService, times(1)).createEmployee(any(EmployeeDto.class), anyString());
    }

    @Test
    @DisplayName("GET /employees/{id} - Should retrieve single employee by ID (CRUD: Read)")
    void testGetEmployeeById() throws Exception {
        when(employeeService.getEmployeeById(1L)).thenReturn(testResponseDto);

        mockMvc.perform(get("/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.firstName").value("Pramod"));

        verify(employeeService, times(1)).getEmployeeById(1L);
    }

    @Test
    @DisplayName("GET /employees - Should return paginated employee list (CRUD: Read)")
    void testGetEmployeesPaginated() throws Exception {
        PageResponse<EmployeeResponseDto> pageResponse = PageResponse.<EmployeeResponseDto>builder()
                .content(Collections.singletonList(testResponseDto))
                .pageNumber(0)
                .pageSize(10)
                .totalElements(1L)
                .totalPages(1)
                .hasNext(false)
                .hasPrevious(false)
                .build();

        when(employeeService.getEmployees(anyInt(), anyInt(), any(), any(), any(), any()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/employees?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].email").value("pramod.pal@acme.com"));

        verify(employeeService, times(1)).getEmployees(anyInt(), anyInt(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("PUT /employees/{id} - Should update employee details (CRUD: Update)")
    void testUpdateEmployee() throws Exception {
        when(employeeService.updateEmployee(eq(1L), any(EmployeeDto.class), anyString()))
                .thenReturn(testResponseDto);

        mockMvc.perform(put("/employees/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDto))
                .principal(() -> "hr_manager"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.firstName").value("Pramod"));

        verify(employeeService, times(1)).updateEmployee(eq(1L), any(EmployeeDto.class), anyString());
    }

    @Test
    @DisplayName("DELETE /employees/{id} - Should deactivate employee (CRUD: Delete)")
    void testDeleteEmployee() throws Exception {
        doNothing().when(employeeService).deactivateEmployee(eq(1L), anyString());

        mockMvc.perform(delete("/employees/1")
                .principal(() -> "hr_manager"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Employee deactivated successfully"));

        verify(employeeService, times(1)).deactivateEmployee(eq(1L), anyString());
    }
}
