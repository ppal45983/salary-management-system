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
 * Entity representing a job designation/title in the organization.
 * Designations define job roles and their levels (e.g., Software Engineer, Manager, etc.).
 */
@Entity
@Table(name = "designations", uniqueConstraints = {
    @UniqueConstraint(name = "uk_designation_name", columnNames = "name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Designation extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "level", nullable = false, length = 50)
    private String level;

    @Column(name = "grade", length = 10)
    private String grade;

    @Column(name = "min_salary", precision = 12, scale = 2)
    private java.math.BigDecimal minSalary;

    @Column(name = "max_salary", precision = 12, scale = 2)
    private java.math.BigDecimal maxSalary;

    @Column(name = "department_id")
    private Long departmentId;

    @Column(name = "reports_to_designation_id")
    private Long reportsToDesignationId;

    public String getTitle() {
        return name;
    }

    public void setTitle(String title) {
        this.name = title;
    }
}
