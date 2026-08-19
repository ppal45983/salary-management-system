package com.sms.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for real-time tax calculation preview.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxCalculationRequestDto {

    @NotNull(message = "Base salary is required")
    @DecimalMin(value = "0.01", message = "Base salary must be greater than 0")
    private BigDecimal baseSalary;

    private BigDecimal allowances;
    private BigDecimal deductions;

    @NotBlank(message = "Country code is required")
    private String country;

    private Integer taxYear;
    private String currency;
}
