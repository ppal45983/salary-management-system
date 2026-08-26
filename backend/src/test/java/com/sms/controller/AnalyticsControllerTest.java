package com.sms.controller;

import com.sms.dto.DashboardMetricsDto;
import com.sms.dto.DepartmentDistributionDto;
import com.sms.dto.PayEquityDto;
import com.sms.service.AnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AnalyticsService analyticsService;

    @InjectMocks
    private AnalyticsController analyticsController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(analyticsController).build();
    }

    @Test
    @DisplayName("GET /analytics/dashboard - Should return dashboard metrics")
    void testGetDashboardMetrics() throws Exception {
        DashboardMetricsDto metrics = DashboardMetricsDto.builder()
                .totalEmployees(10000L)
                .activeEmployees(9500L)
                .inactiveEmployees(500L)
                .totalMonthlyPayroll(new BigDecimal("75000000.00"))
                .payrollByCurrency(new HashMap<>())
                .headcountByDepartment(new HashMap<>())
                .headcountByCountry(new HashMap<>())
                .build();

        when(analyticsService.getDashboardMetrics()).thenReturn(metrics);

        mockMvc.perform(get("/analytics/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalEmployees").value(10000));

        verify(analyticsService, times(1)).getDashboardMetrics();
    }

    @Test
    @DisplayName("GET /analytics/distribution - Should return department salary distributions")
    void testGetDepartmentDistributions() throws Exception {
        DepartmentDistributionDto dto = DepartmentDistributionDto.builder()
                .departmentId(1L)
                .departmentName("Engineering")
                .employeeCount(2500)
                .minSalary(new BigDecimal("60000.00"))
                .maxSalary(new BigDecimal("220000.00"))
                .meanSalary(new BigDecimal("125000.00"))
                .medianSalary(new BigDecimal("120000.00"))
                .build();

        when(analyticsService.getDepartmentDistributions()).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/analytics/distribution"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].departmentName").value("Engineering"));

        verify(analyticsService, times(1)).getDepartmentDistributions();
    }
}
