package com.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response DTO for tax calculation result.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxCalculationResponseDto {

    private BigDecimal baseSalary;
    private BigDecimal allowances;
    private BigDecimal deductions;
    private BigDecimal grossSalary;
    private BigDecimal totalTax;
    private BigDecimal netSalary;
    private BigDecimal effectiveTaxRate;
    private String country;
    private Integer taxYear;
    private String currency;
    private String regime;
    private List<TaxBracketBreakdownDto> breakdown;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaxBracketBreakdownDto {
        private BigDecimal bracketFrom;
        private BigDecimal bracketTo;
        private BigDecimal rate;
        private BigDecimal taxableAmountInBracket;
        private BigDecimal taxForBracket;
    }
}
