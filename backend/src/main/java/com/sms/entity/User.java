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
 * Entity representing a system user with authentication credentials.
 * Users are associated with employees and have roles for access control.
 */
@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_email", columnNames = "email"),
    @UniqueConstraint(name = "uk_user_username", columnNames = "username")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    @Column(name = "username", nullable = false, length = 100, unique = true)
    private String username;

    @Column(name = "email", nullable = false, length = 255, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "role", nullable = false, length = 50)
    private String role; // ADMIN, HR_MANAGER, EMPLOYEE

    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "last_login")
    private java.time.LocalDateTime lastLogin;

    @Column(name = "account_locked", nullable = false)
    private Boolean accountLocked = false;

    @Column(name = "failed_login_attempts", nullable = false)
    private Integer failedLoginAttempts = 0;

    @Column(name = "password_expires_at")
    private java.time.LocalDateTime passwordExpiresAt;

    @Column(name = "two_factor_enabled", nullable = false)
    private Boolean twoFactorEnabled = false;
}
