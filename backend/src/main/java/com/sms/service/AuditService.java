package com.sms.service;

import com.sms.entity.AuditLog;
import com.sms.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * Service for audit logging and compliance tracking.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Log an audit action.
     */
    public void logAction(String action, String entityType, Long entityId, 
                         String userId, String oldValue, String newValue) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setAction(action);
            auditLog.setEntityType(entityType);
            auditLog.setEntityId(entityId);
            auditLog.setUserId(getUserIdAsLong(userId));
            auditLog.setUserName(userId);
            auditLog.setOldValue(oldValue);
            auditLog.setNewValue(newValue);
            auditLog.setStatus("SUCCESS");
            auditLog.setTimestamp(LocalDateTime.now());
            auditLog.setIsActive(true);

            // Try to get IP address
            try {
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                        .getRequestAttributes()).getRequest();
                auditLog.setIpAddress(getClientIpAddress(request));
                auditLog.setUserAgent(request.getHeader("User-Agent"));
            } catch (Exception e) {
                log.debug("Could not retrieve request details for audit logging");
            }

            auditLogRepository.save(auditLog);
            log.debug("Audit log created: {} on {} for user {}", action, entityType, userId);
        } catch (Exception e) {
            log.error("Error logging audit action: {}", e.getMessage(), e);
        }
    }

    /**
     * Log a failed operation.
     */
    public void logFailedAction(String action, String entityType, Long entityId, 
                               String userId, String errorMessage) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setAction(action);
            auditLog.setEntityType(entityType);
            auditLog.setEntityId(entityId);
            auditLog.setUserId(getUserIdAsLong(userId));
            auditLog.setUserName(userId);
            auditLog.setStatus("FAILURE");
            auditLog.setErrorMessage(errorMessage);
            auditLog.setTimestamp(LocalDateTime.now());
            auditLog.setIsActive(true);

            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Error logging failed audit action: {}", e.getMessage());
        }
    }

    /**
     * Get client IP address from request.
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0];
        }
        return request.getRemoteAddr();
    }

    /**
     * Convert username to Long ID (simplified).
     */
    private Long getUserIdAsLong(String username) {
        try {
            return Long.parseLong(username);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
