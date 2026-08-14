package com.sms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entity maintaining comprehensive audit logs for compliance and security.
 * Records all important system activities and data changes.
 */
@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AuditLog extends BaseEntity {

    @Column(name = "action", nullable = false, length = 100)
    private String action;

    @Column(name = "entity_type", nullable = false, length = 100)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_name", length = 100)
    private String userName;

    @Column(name = "old_value", columnDefinition = "LONGTEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "LONGTEXT")
    private String newValue;

    @Column(name = "change_reason", columnDefinition = "TEXT")
    private String changeReason;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // SUCCESS, FAILURE, PARTIAL

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "timestamp", nullable = false)
    private java.time.LocalDateTime timestamp;

    @Column(name = "duration_ms")
    private Long durationMs;
}
