package com.sms.service;

import com.sms.entity.TaxBracket;
import com.sms.exception.BusinessException;
import com.sms.repository.TaxBracketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TaxCalculationService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TaxCalculationService Tests")
public class TaxCalculationServiceTest {

    @Mock
    private TaxBracketRepository taxBracketRepository;

    @InjectMocks
    private TaxCalculationService taxCalculationService;

    private TaxBracket mockTaxBracket;

    @BeforeEach
    void setUp() {
        mockTaxBracket = new TaxBracket();
        mockTaxBracket.setId(1L);
        mockTaxBracket.setCountry("USA");
        mockTaxBracket.setTaxYear(2024);
        mockTaxBracket.setIncomeFrom(BigDecimal.ZERO);
        mockTaxBracket.setIncomeTo(new BigDecimal("100000"));
        mockTaxBracket.setTaxRate(new BigDecimal("10.5"));
        mockTaxBracket.setIsActive(true);
    }

    @Test
    @DisplayName("Should calculate tax correctly for valid salary")
    void testCalculateTax_ValidSalary() {
        // Arrange
        BigDecimal grossSalary = new BigDecimal("50000");
        when(taxBracketRepository.findApplicableTaxBracket("USA", 2024, grossSalary))
                .thenReturn(Optional.of(mockTaxBracket));

        // Act
        BigDecimal tax = taxCalculationService.calculateTax(grossSalary, "USA", 2024);

        // Assert
        BigDecimal expectedTax = new BigDecimal("5250.00");
        assertEquals(expectedTax, tax);
        verify(taxBracketRepository, times(1)).findApplicableTaxBracket("USA", 2024, grossSalary);
    }

    @Test
    @DisplayName("Should throw exception for null salary")
    void testCalculateTax_NullSalary() {
        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            taxCalculationService.calculateTax(null, "USA", 2024);
        });
    }

    @Test
    @DisplayName("Should throw exception for negative salary")
    void testCalculateTax_NegativeSalary() {
        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            taxCalculationService.calculateTax(new BigDecimal("-10000"), "USA", 2024);
        });
    }

    @Test
    @DisplayName("Should throw exception for null country")
    void testCalculateTax_NullCountry() {
        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            taxCalculationService.calculateTax(new BigDecimal("50000"), null, 2024);
        });
    }

    @Test
    @DisplayName("Should throw exception when tax bracket not found")
    void testCalculateTax_TaxBracketNotFound() {
        // Arrange
        when(taxBracketRepository.findApplicableTaxBracket("INVALID", 2024, new BigDecimal("50000")))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            taxCalculationService.calculateTax(new BigDecimal("50000"), "INVALID", 2024);
        });
    }

    @Test
    @DisplayName("Should calculate tax as of date correctly")
    void testCalculateTaxAsOfDate() {
        // Arrange
        BigDecimal grossSalary = new BigDecimal("50000");
        LocalDate date = LocalDate.of(2024, 6, 15);
        when(taxBracketRepository.findApplicableTaxBracket("USA", 2024, grossSalary))
                .thenReturn(Optional.of(mockTaxBracket));

        // Act
        BigDecimal tax = taxCalculationService.calculateTaxAsOfDate(grossSalary, "USA", date);

        // Assert
        assertEquals(new BigDecimal("5250.00"), tax);
    }

    @Test
    @DisplayName("Should get tax rate correctly")
    void testGetTaxRate() {
        // Arrange
        BigDecimal grossSalary = new BigDecimal("50000");
        when(taxBracketRepository.findApplicableTaxBracket("USA", 2024, grossSalary))
                .thenReturn(Optional.of(mockTaxBracket));

        // Act
        BigDecimal taxRate = taxCalculationService.getTaxRate(grossSalary, "USA", 2024);

        // Assert
        assertEquals(new BigDecimal("10.5"), taxRate);
    }

    @Test
    @DisplayName("Should validate tax brackets exist")
    void testValidateTaxBracketsExist() {
        // Arrange
        when(taxBracketRepository.existsByCountryAndTaxYearAndIsActiveTrue("USA", 2024))
                .thenReturn(true);

        // Act & Assert
        assertDoesNotThrow(() -> {
            taxCalculationService.validateTaxBracketsExist("USA", 2024);
        });
    }

    @Test
    @DisplayName("Should throw exception when tax brackets not configured")
    void testValidateTaxBracketsExist_NotConfigured() {
        // Arrange
        when(taxBracketRepository.existsByCountryAndTaxYearAndIsActiveTrue("NONEXISTENT", 2024))
                .thenReturn(false);

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            taxCalculationService.validateTaxBracketsExist("NONEXISTENT", 2024);
        });
    }
}
