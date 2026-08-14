-- Salary Management System - Initial Schema
-- Version: 1.0
-- This script creates all necessary tables for the salary management system

-- ============================================
-- 1. CREATE DEPARTMENTS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS departments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 2. CREATE DESIGNATIONS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS designations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_title (title)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 3. CREATE EMPLOYEES TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS employees (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    department_id BIGINT NOT NULL,
    designation_id BIGINT NOT NULL,
    hire_date DATE NOT NULL,
    country VARCHAR(2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    status ENUM('ACTIVE', 'INACTIVE', 'ON_LEAVE') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE RESTRICT,
    FOREIGN KEY (designation_id) REFERENCES designations(id) ON DELETE RESTRICT,
    INDEX idx_email (email),
    INDEX idx_department_id (department_id),
    INDEX idx_country (country),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 4. CREATE SALARY_RECORDS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS salary_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    base_salary DECIMAL(12, 2) NOT NULL,
    allowances DECIMAL(12, 2) DEFAULT 0,
    deductions DECIMAL(12, 2) DEFAULT 0,
    gross_salary DECIMAL(12, 2) GENERATED ALWAYS AS (base_salary + allowances) STORED,
    tax DECIMAL(12, 2) DEFAULT 0,
    net_salary DECIMAL(12, 2) GENERATED ALWAYS AS (base_salary + allowances - deductions - tax) STORED,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    effective_date DATE NOT NULL,
    status ENUM('DRAFT', 'ACTIVE', 'ARCHIVED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE RESTRICT,
    INDEX idx_employee_id (employee_id),
    INDEX idx_effective_date (effective_date),
    INDEX idx_status (status),
    UNIQUE KEY unique_active_salary (employee_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 5. CREATE SALARY_HISTORY TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS salary_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    salary_record_id BIGINT NOT NULL,
    change_type VARCHAR(50) NOT NULL,
    old_value DECIMAL(12, 2),
    new_value DECIMAL(12, 2),
    field_name VARCHAR(100),
    changed_by VARCHAR(100) NOT NULL,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (salary_record_id) REFERENCES salary_records(id) ON DELETE CASCADE,
    INDEX idx_salary_record_id (salary_record_id),
    INDEX idx_changed_at (changed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 6. CREATE TAX_BRACKETS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS tax_brackets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    country VARCHAR(2) NOT NULL,
    tax_year INT NOT NULL,
    income_from DECIMAL(12, 2) NOT NULL,
    income_to DECIMAL(12, 2) NOT NULL,
    tax_rate DECIMAL(5, 3) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_bracket (country, tax_year, income_from, income_to),
    INDEX idx_country (country),
    INDEX idx_tax_year (tax_year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 7. CREATE AUDIT_LOGS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    user_id VARCHAR(100),
    details JSON,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_entity_type (entity_type),
    INDEX idx_entity_id (entity_id),
    INDEX idx_timestamp (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 8. CREATE USERS TABLE (for authentication)
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'HR_MANAGER',
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- INDEXES FOR PERFORMANCE
-- ============================================
-- Already created with table definitions, but additional indexes can be added here
ALTER TABLE salary_records ADD INDEX idx_employee_status (employee_id, status);
ALTER TABLE employees ADD INDEX idx_department_country (department_id, country);

-- ============================================
-- DATABASE COMPLETED
-- ============================================
-- Run the next migration script to seed initial data
