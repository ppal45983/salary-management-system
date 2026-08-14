package com.sms.repository;

import com.sms.entity.SalaryHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for SalaryHistory entity.
 * Provides data access methods for salary history tracking and audit trail.
 */
@Repository
public interface SalaryHistoryRepository extends JpaRepository<SalaryHistory, Long> {

    /**
     * Find all salary history records for an employee.
     */
    Page<SalaryHistory> findByEmployeeIdOrderByEffectiveDateDesc(Long employeeId, Pageable pageable);

    /**
     * Find salary history records by change type.
     */
    Page<SalaryHistory> findByEmployeeIdAndChangeTypeOrderByEffectiveDateDesc(
            Long employeeId, String changeType, Pageable pageable);

    /**
     * Find salary history records within a date range.
     */
    @Query("SELECT sh FROM SalaryHistory sh WHERE sh.employeeId = :employeeId " +
           "AND sh.effectiveDate BETWEEN :startDate AND :endDate " +
           "ORDER BY sh.effectiveDate DESC")
    Page<SalaryHistory> findByDateRange(@Param("employeeId") Long employeeId, 
                                       @Param("startDate") LocalDate startDate, 
                                       @Param("endDate") LocalDate endDate, 
                                       Pageable pageable);

    /**
     * Find salary increments for an employee.
     */
    @Query("SELECT sh FROM SalaryHistory sh WHERE sh.employeeId = :employeeId " +
           "AND sh.changeType IN ('INCREMENT', 'PROMOTION') " +
           "AND sh.effectiveDate >= :fromDate " +
           "ORDER BY sh.effectiveDate DESC")
    List<SalaryHistory> findIncrementsSince(@Param("employeeId") Long employeeId, 
                                           @Param("fromDate") LocalDate fromDate);

    /**
     * Find all salary adjustments.
     */
    @Query("SELECT sh FROM SalaryHistory sh WHERE sh.changeType = 'ADJUSTMENT' " +
           "AND sh.isActive = true ORDER BY sh.createdAt DESC")
    Page<SalaryHistory> findAllAdjustments(Pageable pageable);

    /**
     * Find salary reviews for a specific period.
     */
    @Query("SELECT sh FROM SalaryHistory sh WHERE sh.changeType = 'REVIEW' " +
           "AND sh.effectiveDate BETWEEN :startDate AND :endDate " +
           "ORDER BY sh.effectiveDate DESC")
    Page<SalaryHistory> findSalaryReviews(@Param("startDate") LocalDate startDate, 
                                         @Param("endDate") LocalDate endDate, 
                                         Pageable pageable);

    /**
     * Get total salary increase for an employee.
     */
    @Query("SELECT COALESCE(SUM(sh.salaryIncreaseAmount), 0) FROM SalaryHistory sh " +
           "WHERE sh.employeeId = :employeeId AND sh.effectiveDate >= :fromDate")
    java.math.BigDecimal getTotalSalaryIncrease(@Param("employeeId") Long employeeId, 
                                               @Param("fromDate") LocalDate fromDate);
}
