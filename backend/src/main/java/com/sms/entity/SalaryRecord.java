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
 * Entity representing the current salary record for an employee.
 * Contains salary components (base, allowances, deductions) and calculated fields (gross, tax, net).
 * Maintains the active salary record for each employee.
 */
@Entity
@Table(name = "salary_records", uniqueConstraints = {
    @UniqueConstraint(name = "uk_salary_employee_active", columnNames = {"employee_id", "status"}) // Only one ACTIVE record per employee
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SalaryRecord extends BaseEntity {

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "base_salary", nullable = false, precision = 12, scale = 2)
    private java.math.BigDecimal baseSalary;

    @Column(name = "allowances", precision = 12, scale = 2)
    private java.math.BigDecimal allowances;

    @Column(name = "deductions", precision = 12, scale = 2)
    private java.math.BigDecimal deductions;

    @Column(name = "gross_salary", nullable = false, precision = 12, scale = 2)
    private java.math.BigDecimal grossSalary;

    @Column(name = "tax", nullable = false, precision = 12, scale = 2)
    private java.math.BigDecimal tax;

    @Column(name = "net_salary", nullable = false, precision = 12, scale = 2)
    private java.math.BigDecimal netSalary;

    @Column(name = "effective_date", nullable = false)
    private java.time.LocalDate effectiveDate;

    @Column(name = "end_date")
    private java.time.LocalDate endDate;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // ACTIVE, INACTIVE, ARCHIVED

    @Column(name = "pay_frequency", nullable = false, length = 50)
    private String payFrequency; // MONTHLY, QUARTERLY, ANNUALLY

    @Column(name = "currency", nullable = false, length = 10)
    private String currency;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_at")
    private java.time.LocalDateTime approvedAt;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;
}
