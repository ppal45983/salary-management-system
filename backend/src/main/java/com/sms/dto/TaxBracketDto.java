package com.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxBracketDto {
    private Long id;
    private String country;
    private Integer taxYear;
    private BigDecimal incomeFrom;
    private BigDecimal incomeTo;
    private BigDecimal taxRate;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private String description;
    private String currency;
}
