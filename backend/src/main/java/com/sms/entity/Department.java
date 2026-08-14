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
 * Entity representing a department in the organization.
 * Departments are master data used to organize employees.
 */
@Entity
@Table(name = "departments", uniqueConstraints = {
    @UniqueConstraint(name = "uk_department_name", columnNames = "name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Department extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "department_code", nullable = false, length = 20, unique = true)
    private String departmentCode;

    @Column(name = "manager_id")
    private Long managerId;

    @Column(name = "budget", precision = 15, scale = 2)
    private java.math.BigDecimal budget;

    @Column(name = "location", length = 200)
    private String location;
}
