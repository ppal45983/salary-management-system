package com.sms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entity representing tax brackets for different countries and years.
 * Used for calculating income tax on employee salaries.
 */
@Entity
@Table(name = "tax_brackets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TaxBracket extends BaseEntity {

    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @Column(name = "tax_year", nullable = false)
    private Integer taxYear;

    @Column(name = "income_from", nullable = false, precision = 15, scale = 2)
    private java.math.BigDecimal incomeFrom;

    @Column(name = "income_to", precision = 15, scale = 2)
    private java.math.BigDecimal incomeTo;

    @Column(name = "tax_rate", nullable = false, precision = 5, scale = 2)
    private java.math.BigDecimal taxRate; // Percentage (0-100)

    @Column(name = "effective_from")
    private java.time.LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private java.time.LocalDate effectiveTo;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "currency", length = 10)
    private String currency;
}
