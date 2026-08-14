package com.sms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entity maintaining historical records of all salary changes for an employee.
 * Provides audit trail for salary adjustments, promotions, and salary reviews.
 */
@Entity
@Table(name = "salary_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SalaryHistory extends BaseEntity {

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "salary_record_id")
    private Long salaryRecordId;

    @Column(name = "base_salary", nullable = false, precision = 12, scale = 2)
    private java.math.BigDecimal baseSalary;

    @Column(name = "allowances", precision = 12, scale = 2)
    private java.math.BigDecimal allowances;

    @Column(name = "deductions", precision = 12, scale = 2)
    private java.math.BigDecimal deductions;

    @Column(name = "gross_salary", precision = 12, scale = 2)
    private java.math.BigDecimal grossSalary;

    @Column(name = "tax", precision = 12, scale = 2)
    private java.math.BigDecimal tax;

    @Column(name = "net_salary", precision = 12, scale = 2)
    private java.math.BigDecimal netSalary;

    @Column(name = "effective_date", nullable = false)
    private java.time.LocalDate effectiveDate;

    @Column(name = "change_type", nullable = false, length = 50)
    private String changeType; // INITIAL, INCREMENT, PROMOTION, ADJUSTMENT, REVIEW

    @Column(name = "change_reason", columnDefinition = "TEXT")
    private String changeReason;

    @Column(name = "changed_by", nullable = false)
    private Long changedBy;

    @Column(name = "previous_base_salary", precision = 12, scale = 2)
    private java.math.BigDecimal previousBaseSalary;

    @Column(name = "salary_increase_amount", precision = 12, scale = 2)
    private java.math.BigDecimal salaryIncreaseAmount;

    @Column(name = "salary_increase_percentage", precision = 5, scale = 2)
    private java.math.BigDecimal salaryIncreasePercentage;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
