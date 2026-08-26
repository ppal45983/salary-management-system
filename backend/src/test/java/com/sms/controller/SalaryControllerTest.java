package com.sms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sms.dto.SalaryRecordDto;
import com.sms.dto.SalaryRecordResponseDto;
import com.sms.service.CsvExportService;
import com.sms.service.SalaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SalaryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SalaryService salaryService;

    @Mock
    private CsvExportService csvExportService;

    @InjectMocks
    private SalaryController salaryController;

    private ObjectMapper objectMapper;
    private SalaryRecordDto testDto;
    private SalaryRecordResponseDto testResponseDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(salaryController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testDto = SalaryRecordDto.builder()
                .employeeId(1L)
                .baseSalary(new BigDecimal("100000.00"))
                .allowances(new BigDecimal("15000.00"))
                .deductions(new BigDecimal("5000.00"))
                .effectiveDate(LocalDate.now())
                .paymentMethod("BANK_TRANSFER")
                .currency("USD")
                .status("ACTIVE")
                .build();

        testResponseDto = SalaryRecordResponseDto.builder()
                .id(1L)
                .employeeId(1L)
                .employeeName("Pramod Pal")
                .baseSalary(new BigDecimal("100000.00"))
                .allowances(new BigDecimal("15000.00"))
                .deductions(new BigDecimal("5000.00"))
                .grossSalary(new BigDecimal("115000.00"))
                .tax(new BigDecimal("21000.00"))
                .netSalary(new BigDecimal("89000.00"))
                .currency("USD")
                .status("ACTIVE")
                .build();
    }

    @Test
    @DisplayName("POST /salaries - Should create salary record with progressive tax calculation (CRUD: Create)")
    void testCreateSalaryRecord() throws Exception {
        when(salaryService.createSalaryRecord(any(SalaryRecordDto.class), anyString()))
                .thenReturn(testResponseDto);

        mockMvc.perform(post("/salaries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDto))
                .principal(() -> "hr_manager"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.grossSalary").value(115000.00))
                .andExpect(jsonPath("$.data.netSalary").value(89000.00));

        verify(salaryService, times(1)).createSalaryRecord(any(SalaryRecordDto.class), anyString());
    }

    @Test
    @DisplayName("GET /salaries/employee/{empId} - Should retrieve salary for employee (CRUD: Read)")
    void testGetActiveSalaryByEmployeeId() throws Exception {
        when(salaryService.getActiveSalaryByEmployeeId(1L)).thenReturn(testResponseDto);

        mockMvc.perform(get("/salaries/employee/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.netSalary").value(89000.00));

        verify(salaryService, times(1)).getActiveSalaryByEmployeeId(1L);
    }
}
