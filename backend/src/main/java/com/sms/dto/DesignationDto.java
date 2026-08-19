package com.sms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DesignationDto {
    private Long id;

    @NotBlank(message = "Designation title is required")
    private String title;

    private String description;
    private String level;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private String departmentName;
    private long employeeCount;
}
