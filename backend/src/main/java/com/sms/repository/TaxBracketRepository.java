package com.sms.repository;

import com.sms.entity.TaxBracket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for TaxBracket entity.
 * Provides data access methods for tax bracket management and calculation.
 */
@Repository
public interface TaxBracketRepository extends JpaRepository<TaxBracket, Long> {

    /**
     * Find tax bracket for a specific income amount in a country for a given year.
     */
    @Query("SELECT t FROM TaxBracket t WHERE t.country = :country AND t.taxYear = :taxYear " +
           "AND t.incomeFrom <= :income AND t.incomeTo >= :income " +
           "AND t.isActive = true " +
           "ORDER BY t.incomeFrom")
    Optional<TaxBracket> findApplicableTaxBracket(@Param("country") String country, 
                                                    @Param("taxYear") Integer taxYear, 
                                                    @Param("income") BigDecimal income);

    /**
     * Find all tax brackets for a country and year.
     */
    List<TaxBracket> findByCountryAndTaxYearAndIsActiveTrueOrderByIncomeFrom(String country, Integer taxYear);

    /**
     * Find all active tax brackets for a country.
     */
    List<TaxBracket> findByCountryAndIsActiveTrueOrderByTaxYearDescIncomeFrom(String country);

    /**
     * Find tax brackets effective on a specific date.
     */
    @Query("SELECT t FROM TaxBracket t WHERE t.country = :country AND t.isActive = true " +
           "AND t.effectiveFrom <= :date AND (t.effectiveTo IS NULL OR t.effectiveTo >= :date) " +
           "ORDER BY t.taxYear DESC, t.incomeFrom")
    List<TaxBracket> findEffectiveTaxBrackets(@Param("country") String country, @Param("date") LocalDate date);

    /**
     * Find all countries with tax data.
     */
    @Query("SELECT DISTINCT t.country FROM TaxBracket t WHERE t.isActive = true ORDER BY t.country")
    List<String> findAllCountries();

    /**
     * Check if tax brackets exist for a country and year.
     */
    boolean existsByCountryAndTaxYearAndIsActiveTrue(String country, Integer taxYear);
}
