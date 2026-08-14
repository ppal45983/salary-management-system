package com.sms.repository;

import com.sms.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Department entity.
 * Provides data access methods for department management.
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    /**
     * Find a department by its name.
     */
    Optional<Department> findByName(String name);

    /**
     * Find a department by its code.
     */
    Optional<Department> findByDepartmentCode(String departmentCode);

    /**
     * Find all active departments.
     */
    Page<Department> findByIsActiveTrue(Pageable pageable);

    /**
     * Search departments by name or description.
     */
    @Query("SELECT d FROM Department d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(d.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND d.isActive = true")
    Page<Department> searchDepartments(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Check if department name exists.
     */
    boolean existsByNameAndIsActiveTrue(String name);
}
