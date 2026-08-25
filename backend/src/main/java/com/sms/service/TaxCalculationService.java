package com.sms.service;

import com.sms.entity.TaxBracket;
import com.sms.exception.BusinessException;
import com.sms.repository.TaxBracketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Service for tax calculation operations.
 * Responsible for computing income tax based on tax brackets and applicable rates.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaxCalculationService {

    private final TaxBracketRepository taxBracketRepository;

    /**
     * Calculate tax for a given gross salary in a specific country.
     *
     * @param grossSalary The gross salary amount
     * @param country The country for which to calculate tax
     * @param taxYear The tax year
     * @return Calculated tax amount
     * @throws BusinessException if tax bracket not found or validation fails
     */
    public BigDecimal calculateTax(BigDecimal grossSalary, String country, Integer taxYear) {
        if (grossSalary == null || grossSalary.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("INVALID_SALARY", "Gross salary cannot be null or negative");
        }

        if (country == null || country.trim().isEmpty()) {
            throw new BusinessException("INVALID_COUNTRY", "Country cannot be null or empty");
        }

        final int effectiveTaxYear = (taxYear != null) ? taxYear : LocalDate.now().getYear();

        log.debug("Calculating tax for salary: {} in {}, tax year: {}", grossSalary, country, effectiveTaxYear);

        TaxBracket applicableBracket = taxBracketRepository
                .findApplicableTaxBracket(country, effectiveTaxYear, grossSalary)
                .orElseThrow(() -> new BusinessException("TAX_BRACKET_NOT_FOUND",
                        "No tax bracket found for " + country + " in " + effectiveTaxYear + 
                        " for salary: " + grossSalary));

        BigDecimal taxRate = applicableBracket.getTaxRate();
        BigDecimal tax = grossSalary.multiply(taxRate).divide(new BigDecimal("100"));

        log.debug("Tax calculated: {} (rate: {}%)", tax, taxRate);
        return tax.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Calculate tax with effective date validation.
     *
     * @param grossSalary The gross salary amount
     * @param country The country for which to calculate tax
     * @param effectiveDate The date for which tax should be calculated
     * @return Calculated tax amount
     */
    public BigDecimal calculateTaxAsOfDate(BigDecimal grossSalary, String country, LocalDate effectiveDate) {
        if (effectiveDate == null) {
            effectiveDate = LocalDate.now();
        }

        Integer taxYear = effectiveDate.getYear();
        return calculateTax(grossSalary, country, taxYear);
    }

    /**
     * Get applicable tax rate for given parameters.
     *
     * @param grossSalary The gross salary amount
     * @param country The country
     * @param taxYear The tax year
     * @return Tax rate as percentage (e.g., 10.5)
     */
    public BigDecimal getTaxRate(BigDecimal grossSalary, String country, Integer taxYear) {
        final int effectiveTaxYear = (taxYear != null) ? taxYear : LocalDate.now().getYear();
        TaxBracket bracket = taxBracketRepository
                .findApplicableTaxBracket(country, effectiveTaxYear, grossSalary)
                .orElseThrow(() -> new BusinessException("TAX_BRACKET_NOT_FOUND",
                        "No tax bracket found for calculation"));
        return bracket.getTaxRate();
    }

    /**
     * Get all tax brackets for a country in a specific year.
     *
     * @param country The country
     * @param taxYear The tax year
     * @return List of tax brackets
     */
    public List<TaxBracket> getTaxBracketsForCountry(String country, Integer taxYear) {
        final int effectiveTaxYear = (taxYear != null) ? taxYear : LocalDate.now().getYear();
        return taxBracketRepository.findByCountryAndTaxYearAndIsActiveTrueOrderByIncomeFrom(country, effectiveTaxYear);
    }

    /**
     * Validate that tax brackets exist for a country and year.
     */
    public void validateTaxBracketsExist(String country, Integer taxYear) {
        final int effectiveTaxYear = (taxYear != null) ? taxYear : LocalDate.now().getYear();
        boolean exists = taxBracketRepository.existsByCountryAndTaxYearAndIsActiveTrue(country, effectiveTaxYear);
        if (!exists) {
            throw new BusinessException("TAX_BRACKETS_NOT_CONFIGURED",
                    "Tax brackets not configured for " + country + " in " + effectiveTaxYear);
        }
    }

    /**
     * Calculate tax with full breakdown across brackets for real-time preview and slips.
     */
    public com.sms.dto.TaxCalculationResponseDto calculateTaxWithBreakdown(
            BigDecimal baseSalary, BigDecimal allowances, BigDecimal deductions, String country, Integer taxYear, String currency) {
        
        if (baseSalary == null || baseSalary.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("INVALID_SALARY", "Base salary cannot be null or negative");
        }
        if (country == null || country.trim().isEmpty()) {
            throw new BusinessException("INVALID_COUNTRY", "Country cannot be null or empty");
        }
        final int effectiveTaxYear = (taxYear != null) ? taxYear : LocalDate.now().getYear();

        BigDecimal safeAllowances = allowances != null ? allowances : BigDecimal.ZERO;
        BigDecimal safeDeductions = deductions != null ? deductions : BigDecimal.ZERO;
        BigDecimal grossSalary = baseSalary.add(safeAllowances);

        List<TaxBracket> brackets = getTaxBracketsForCountry(country, effectiveTaxYear);
        java.util.List<com.sms.dto.TaxCalculationResponseDto.TaxBracketBreakdownDto> breakdownList = new java.util.ArrayList<>();
        BigDecimal totalTax = BigDecimal.ZERO;

        if (brackets.isEmpty()) {
            // Fallback to single bracket lookup if findByCountry list is empty
            TaxBracket applicable = taxBracketRepository.findApplicableTaxBracket(country, effectiveTaxYear, grossSalary)
                    .orElseThrow(() -> new BusinessException("TAX_BRACKET_NOT_FOUND",
                            "No tax bracket found for " + country + " in " + effectiveTaxYear));
            BigDecimal taxRate = applicable.getTaxRate();
            totalTax = grossSalary.multiply(taxRate).divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
            breakdownList.add(com.sms.dto.TaxCalculationResponseDto.TaxBracketBreakdownDto.builder()
                    .bracketFrom(applicable.getIncomeFrom())
                    .bracketTo(applicable.getIncomeTo())
                    .rate(applicable.getTaxRate())
                    .taxableAmountInBracket(grossSalary)
                    .taxForBracket(totalTax)
                    .build());
        } else {
            // Progressive tax computation across brackets
            for (TaxBracket bracket : brackets) {
                if (grossSalary.compareTo(bracket.getIncomeFrom()) > 0) {
                    BigDecimal taxableInThisBracket;
                    if (bracket.getIncomeTo() != null && grossSalary.compareTo(bracket.getIncomeTo()) > 0) {
                        taxableInThisBracket = bracket.getIncomeTo().subtract(bracket.getIncomeFrom());
                    } else {
                        taxableInThisBracket = grossSalary.subtract(bracket.getIncomeFrom());
                    }
                    BigDecimal bracketTax = taxableInThisBracket.multiply(bracket.getTaxRate())
                            .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
                    totalTax = totalTax.add(bracketTax);

                    breakdownList.add(com.sms.dto.TaxCalculationResponseDto.TaxBracketBreakdownDto.builder()
                            .bracketFrom(bracket.getIncomeFrom())
                            .bracketTo(bracket.getIncomeTo())
                            .rate(bracket.getTaxRate())
                            .taxableAmountInBracket(taxableInThisBracket)
                            .taxForBracket(bracketTax)
                            .build());
                }
            }
        }

        totalTax = totalTax.setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal netSalary = grossSalary.subtract(safeDeductions).subtract(totalTax).setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal effectiveRate = grossSalary.compareTo(BigDecimal.ZERO) > 0 
                ? totalTax.multiply(new BigDecimal("100")).divide(grossSalary, 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return com.sms.dto.TaxCalculationResponseDto.builder()
                .baseSalary(baseSalary)
                .allowances(safeAllowances)
                .deductions(safeDeductions)
                .grossSalary(grossSalary)
                .totalTax(totalTax)
                .netSalary(netSalary)
                .effectiveTaxRate(effectiveRate)
                .country(country)
                .taxYear(effectiveTaxYear)
                .currency(currency)
                .breakdown(breakdownList)
                .build();
    }
}
