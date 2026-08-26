package com.sms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.dto.DepartmentDto;
import com.sms.dto.DesignationDto;
import com.sms.service.MasterDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MasterDataControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MasterDataService masterDataService;

    @InjectMocks
    private MasterDataController masterDataController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(masterDataController).build();
    }

    @Test
    @DisplayName("GET /masters/departments - Should return active departments list")
    void testGetAllDepartments() throws Exception {
        DepartmentDto dept = DepartmentDto.builder()
                .id(1L)
                .name("Engineering")
                .code("ENG")
                .employeeCount(1500)
                .build();

        when(masterDataService.getAllDepartments()).thenReturn(Collections.singletonList(dept));

        mockMvc.perform(get("/masters/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("Engineering"));

        verify(masterDataService, times(1)).getAllDepartments();
    }

    @Test
    @DisplayName("GET /masters/designations - Should return active designations list")
    void testGetAllDesignations() throws Exception {
        DesignationDto desig = DesignationDto.builder()
                .id(1L)
                .name("Senior Software Engineer")
                .level("Senior")
                .build();

        when(masterDataService.getAllDesignations()).thenReturn(Collections.singletonList(desig));

        mockMvc.perform(get("/masters/designations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("Senior Software Engineer"));

        verify(masterDataService, times(1)).getAllDesignations();
    }
}
