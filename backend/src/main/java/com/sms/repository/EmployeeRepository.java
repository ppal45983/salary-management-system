package com.sms.repository;

import com.sms.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Employee entity.
 * Provides data access methods for employee management.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Find an employee by employee ID.
     */
    Optional<Employee> findByEmployeeId(String employeeId);

    /**
     * Find an employee by email.
     */
    Optional<Employee> findByEmail(String email);

    /**
     * Find all active employees.
     */
    Page<Employee> findByIsActiveTrue(Pageable pageable);

    /**
     * Find all employees in a department.
     */
    Page<Employee> findByDepartmentIdAndIsActiveTrue(Long departmentId, Pageable pageable);

    /**
     * Find all employees by status.
     */
    Page<Employee> findByStatusAndIsActiveTrue(String status, Pageable pageable);

    /**
     * Find all employees by country.
     */
    Page<Employee> findByCountryAndIsActiveTrue(String country, Pageable pageable);

    /**
     * Find all employees by employment type.
     */
    Page<Employee> findByEmploymentTypeAndIsActiveTrue(String employmentType, Pageable pageable);

    /**
     * Search employees by name, email, or employee ID.
     */
    @Query("SELECT e FROM Employee e WHERE (LOWER(e.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(e.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR e.employeeId LIKE CONCAT('%', :searchTerm, '%')) AND e.isActive = true")
    Page<Employee> searchEmployees(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find employees by designation.
     */
    Page<Employee> findByDesignationIdAndIsActiveTrue(Long designationId, Pageable pageable);

    /**
     * Find direct reports for a manager.
     */
    Page<Employee> findByManagerIdAndIsActiveTrue(Long managerId, Pageable pageable);

    /**
     * Count employees in a department.
     */
    Long countByDepartmentIdAndIsActiveTrue(Long departmentId);

    /**
     * Check if employee email exists.
     */
    boolean existsByEmailAndIsActiveTrue(String email);

    /**
     * Check if employee ID exists.
     */
    boolean existsByEmployeeIdAndIsActiveTrue(String employeeId);
}
