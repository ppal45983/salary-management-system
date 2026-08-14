package com.sms.repository;

import com.sms.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * Repository interface for AuditLog entity.
 * Provides data access methods for audit trail and compliance tracking.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Find all audit logs for a specific entity.
     */
    Page<AuditLog> findByEntityTypeAndEntityIdOrderByTimestampDesc(
            String entityType, Long entityId, Pageable pageable);

    /**
     * Find all audit logs for a specific user.
     */
    Page<AuditLog> findByUserIdOrderByTimestampDesc(Long userId, Pageable pageable);

    /**
     * Find audit logs by action.
     */
    Page<AuditLog> findByActionOrderByTimestampDesc(String action, Pageable pageable);

    /**
     * Find audit logs by action and entity type.
     */
    Page<AuditLog> findByActionAndEntityTypeOrderByTimestampDesc(
            String action, String entityType, Pageable pageable);

    /**
     * Find audit logs within a time range.
     */
    @Query("SELECT al FROM AuditLog al WHERE al.timestamp BETWEEN :startTime AND :endTime " +
           "ORDER BY al.timestamp DESC")
    Page<AuditLog> findAuditLogsByDateRange(@Param("startTime") LocalDateTime startTime, 
                                           @Param("endTime") LocalDateTime endTime, 
                                           Pageable pageable);

    /**
     * Find failed operations.
     */
    @Query("SELECT al FROM AuditLog al WHERE al.status != 'SUCCESS' ORDER BY al.timestamp DESC")
    Page<AuditLog> findFailedOperations(Pageable pageable);

    /**
     * Find audit logs for a user in a time range.
     */
    @Query("SELECT al FROM AuditLog al WHERE al.userId = :userId " +
           "AND al.timestamp BETWEEN :startTime AND :endTime " +
           "ORDER BY al.timestamp DESC")
    Page<AuditLog> findUserActivityBetween(@Param("userId") Long userId, 
                                          @Param("startTime") LocalDateTime startTime, 
                                          @Param("endTime") LocalDateTime endTime, 
                                          Pageable pageable);

    /**
     * Find sensitive operations (creates, updates, deletes).
     */
    @Query("SELECT al FROM AuditLog al WHERE al.action IN ('CREATE', 'UPDATE', 'DELETE') " +
           "AND al.timestamp BETWEEN :startTime AND :endTime " +
           "ORDER BY al.timestamp DESC")
    Page<AuditLog> findSensitiveOperations(@Param("startTime") LocalDateTime startTime, 
                                          @Param("endTime") LocalDateTime endTime, 
                                          Pageable pageable);

    /**
     * Count audit logs by status.
     */
    Long countByStatus(String status);
}
