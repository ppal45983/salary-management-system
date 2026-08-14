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

        if (taxYear == null) {
            taxYear = LocalDate.now().getYear();
        }

        log.debug("Calculating tax for salary: {} in {}, tax year: {}", grossSalary, country, taxYear);

        TaxBracket applicableBracket = taxBracketRepository
                .findApplicableTaxBracket(country, taxYear, grossSalary)
                .orElseThrow(() -> new BusinessException("TAX_BRACKET_NOT_FOUND",
                        "No tax bracket found for " + country + " in " + taxYear + 
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
        TaxBracket bracket = taxBracketRepository
                .findApplicableTaxBracket(country, taxYear, grossSalary)
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
        return taxBracketRepository.findByCountryAndTaxYearAndIsActiveTrueOrderByIncomeFrom(country, taxYear);
    }

    /**
     * Validate that tax brackets exist for a country and year.
     *
     * @param country The country
     * @param taxYear The tax year
     * @throws BusinessException if no tax brackets found
     */
    public void validateTaxBracketsExist(String country, Integer taxYear) {
        boolean exists = taxBracketRepository.existsByCountryAndTaxYearAndIsActiveTrue(country, taxYear);
        if (!exists) {
            throw new BusinessException("TAX_BRACKETS_NOT_CONFIGURED",
                    "Tax brackets not configured for " + country + " in " + taxYear);
        }
    }
}
