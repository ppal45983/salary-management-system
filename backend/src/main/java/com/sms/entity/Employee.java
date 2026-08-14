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
 * Entity representing an employee in the organization.
 * Core entity storing employee personal and employment information.
 */
@Entity
@Table(name = "employees", uniqueConstraints = {
    @UniqueConstraint(name = "uk_employee_email", columnNames = "email"),
    @UniqueConstraint(name = "uk_employee_employee_id", columnNames = "employee_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Employee extends BaseEntity {

    @Column(name = "employee_id", nullable = false, length = 20, unique = true)
    private String employeeId;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "email", nullable = false, length = 255, unique = true)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "date_of_birth")
    private java.time.LocalDate dateOfBirth;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "department_id", nullable = false)
    private Long departmentId;

    @Column(name = "designation_id", nullable = false)
    private Long designationId;

    @Column(name = "hire_date", nullable = false)
    private java.time.LocalDate hireDate;

    @Column(name = "employment_type", nullable = false, length = 50)
    private String employmentType; // FULL_TIME, PART_TIME, CONTRACT, INTERN

    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency; // USD, GBP, INR, etc.

    @Column(name = "tax_id", length = 50)
    private String taxId;

    @Column(name = "bank_account", length = 50)
    private String bankAccount;

    @Column(name = "bank_code", length = 20)
    private String bankCode;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // ACTIVE, ON_LEAVE, SUSPENDED, TERMINATED

    @Column(name = "termination_date")
    private java.time.LocalDate terminationDate;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "manager_id")
    private Long managerId;
}
