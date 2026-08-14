package com.sms.repository;

import com.sms.entity.SalaryRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Repository interface for SalaryRecord entity.
 * Provides data access methods for salary management and retrieval.
 */
@Repository
public interface SalaryRecordRepository extends JpaRepository<SalaryRecord, Long> {

    /**
     * Find the active salary record for an employee.
     */
    @Query("SELECT s FROM SalaryRecord s WHERE s.employeeId = :employeeId AND s.status = 'ACTIVE'")
    Optional<SalaryRecord> findActiveSalaryByEmployeeId(@Param("employeeId") Long employeeId);

    /**
     * Find all salary records for an employee.
     */
    Page<SalaryRecord> findByEmployeeIdOrderByEffectiveDateDesc(Long employeeId, Pageable pageable);

    /**
     * Find all active salary records.
     */
    Page<SalaryRecord> findByStatusAndIsActiveTrueOrderByEffectiveDate(String status, Pageable pageable);

    /**
     * Find salary records effective on a specific date.
     */
    @Query("SELECT s FROM SalaryRecord s WHERE s.employeeId = :employeeId " +
           "AND s.effectiveDate <= :date AND (s.endDate IS NULL OR s.endDate >= :date)")
    Optional<SalaryRecord> findSalaryAtDate(@Param("employeeId") Long employeeId, @Param("date") LocalDate date);

    /**
     * Find salary records that need approval.
     */
    @Query("SELECT s FROM SalaryRecord s WHERE s.status = 'INACTIVE' AND s.approvedAt IS NULL " +
           "AND s.isActive = true ORDER BY s.createdAt")
    Page<SalaryRecord> findPendingApproval(Pageable pageable);

    /**
     * Find salary records by pay frequency.
     */
    Page<SalaryRecord> findByPayFrequencyAndStatusAndIsActiveTrueOrderByEffectiveDate(
            String payFrequency, String status, Pageable pageable);

    /**
     * Count active salary records.
     */
    Long countByStatusAndIsActiveTrue(String status);

    /**
     * Find salary records for payroll processing.
     */
    @Query("SELECT s FROM SalaryRecord s WHERE s.status = 'ACTIVE' AND s.isActive = true " +
           "AND s.payFrequency = :payFrequency ORDER BY s.employeeId")
    Page<SalaryRecord> findForPayroll(@Param("payFrequency") String payFrequency, Pageable pageable);
}
