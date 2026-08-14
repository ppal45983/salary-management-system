package com.sms.repository;

import com.sms.entity.Designation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Designation entity.
 * Provides data access methods for job designation management.
 */
@Repository
public interface DesignationRepository extends JpaRepository<Designation, Long> {

    /**
     * Find a designation by its name.
     */
    Optional<Designation> findByName(String name);

    /**
     * Find all designations for a specific department.
     */
    Page<Designation> findByDepartmentIdAndIsActiveTrue(Long departmentId, Pageable pageable);

    /**
     * Find all active designations.
     */
    Page<Designation> findByIsActiveTrue(Pageable pageable);

    /**
     * Search designations by name, level, or grade.
     */
    @Query("SELECT d FROM Designation d WHERE (LOWER(d.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(d.level) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(d.grade) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND d.isActive = true")
    Page<Designation> searchDesignations(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find all designations in a specific level.
     */
    Page<Designation> findByLevelAndIsActiveTrue(String level, Pageable pageable);

    /**
     * Check if designation name exists.
     */
    boolean existsByNameAndIsActiveTrue(String name);
}
