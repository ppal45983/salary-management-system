-- ============================================================================
-- Salary Management System - 10,000 Employee Seeding Script
-- Target Database: MySQL 8.0+ / Relational DB
-- Description: Generates 10,000 realistic employees across 9 countries with
--              departments, designations, salary records, and tax calculations.
-- ============================================================================

DELIMITER $$

DROP PROCEDURE IF EXISTS Seed10kEmployees$$

CREATE PROCEDURE Seed10kEmployees()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE dept_count INT;
    DECLARE desig_count INT;
    DECLARE rand_dept_id BIGINT;
    DECLARE rand_desig_id BIGINT;
    DECLARE rand_country VARCHAR(100);
    DECLARE rand_currency VARCHAR(10);
    DECLARE rand_base DECIMAL(12,2);
    DECLARE rand_allowance DECIMAL(12,2);
    DECLARE rand_deduction DECIMAL(12,2);
    DECLARE rand_gross DECIMAL(12,2);
    DECLARE rand_tax DECIMAL(12,2);
    DECLARE rand_net DECIMAL(12,2);
    DECLARE new_emp_id BIGINT;

    -- Country & currency array mappings
    DROP TEMPORARY TABLE IF EXISTS TempCountries;
    CREATE TEMPORARY TABLE TempCountries (
        id INT AUTO_INCREMENT PRIMARY KEY,
        country_name VARCHAR(100),
        currency_code VARCHAR(10),
        tax_rate_approx DECIMAL(5,2)
    );

    INSERT INTO TempCountries (country_name, currency_code, tax_rate_approx) VALUES
    ('United States', 'USD', 22.0),
    ('United Kingdom', 'GBP', 20.0),
    ('India', 'INR', 15.0),
    ('Germany', 'EUR', 25.0),
    ('France', 'EUR', 24.0),
    ('Canada', 'CAD', 20.0),
    ('Australia', 'AUD', 22.5),
    ('Japan', 'JPY', 18.0),
    ('Singapore', 'SGD', 12.0);

    -- Disable autocommit for fast batch insert
    SET autocommit = 0;

    WHILE i <= 10000 DO
        -- Random selection
        SELECT country_name, currency_code, tax_rate_approx 
        INTO rand_country, rand_currency, rand_tax
        FROM TempCountries ORDER BY RAND() LIMIT 1;

        SELECT id INTO rand_dept_id FROM departments ORDER BY RAND() LIMIT 1;
        SELECT id, min_salary, max_salary INTO rand_desig_id, rand_base, rand_gross FROM designations ORDER BY RAND() LIMIT 1;

        IF rand_base IS NULL THEN SET rand_base = 65000.00; END IF;
        IF rand_gross IS NULL THEN SET rand_gross = 120000.00; END IF;

        SET rand_base = rand_base + (RAND() * (rand_gross - rand_base));
        SET rand_base = ROUND(rand_base / 1000) * 1000;
        SET rand_allowance = ROUND(rand_base * 0.10);
        SET rand_deduction = ROUND(rand_base * 0.03);
        SET rand_gross = rand_base + rand_allowance;
        SET rand_tax = ROUND(rand_gross * 0.20);
        SET rand_net = rand_gross - rand_deduction - rand_tax;

        -- Insert Employee
        INSERT INTO employees (
            employee_id, first_name, last_name, email, phone, date_of_birth, gender,
            department_id, designation_id, hire_date, employment_type, country, currency,
            tax_id, bank_account, bank_code, status, address, city, is_active, created_at, created_by
        ) VALUES (
            CONCAT('EMP-', LPAD(i, 5, '0')),
            ELT(1 + FLOOR(RAND() * 10), 'James', 'Mary', 'Robert', 'Patricia', 'John', 'Jennifer', 'Michael', 'Linda', 'David', 'Elizabeth'),
            ELT(1 + FLOOR(RAND() * 10), 'Smith', 'Johnson', 'Williams', 'Brown', 'Jones', 'Garcia', 'Miller', 'Davis', 'Rodriguez', 'Martinez'),
            CONCAT('employee.', i, '@acme.com'),
            CONCAT('+1-555-', LPAD(FLOOR(RAND()*10000), 4, '0')),
            DATE_SUB(CURDATE(), INTERVAL (22 + FLOOR(RAND() * 30)) YEAR),
            IF(RAND() > 0.5, 'Male', 'Female'),
            rand_dept_id,
            rand_desig_id,
            DATE_SUB(CURDATE(), INTERVAL (FLOOR(RAND() * 1800)) DAY),
            'FULL_TIME',
            rand_country,
            rand_currency,
            CONCAT('TAX-', LPAD(i, 8, '0')),
            CONCAT('ACCT-', LPAD(i * 37, 10, '0')),
            'ACME-BANK',
            IF(RAND() > 0.05, 'ACTIVE', 'ON_LEAVE'),
            '100 Corporate Blvd',
            'Metropolis',
            1,
            NOW(),
            'SQL_SEEDER'
        );

        SET new_emp_id = LAST_INSERT_ID();

        -- Insert matching Salary Record
        INSERT INTO salary_records (
            employee_id, base_salary, allowances, deductions, gross_salary, tax, net_salary,
            effective_date, status, pay_frequency, currency, approved_by, approved_at, is_active, created_at, created_by
        ) VALUES (
            new_emp_id,
            rand_base,
            rand_allowance,
            rand_deduction,
            rand_gross,
            rand_tax,
            rand_net,
            DATE_SUB(CURDATE(), INTERVAL (FLOOR(RAND() * 365)) DAY),
            'ACTIVE',
            'MONTHLY',
            rand_currency,
            1,
            NOW(),
            1,
            NOW(),
            'SQL_SEEDER'
        );

        -- Commit in chunks of 500
        IF MOD(i, 500) = 0 THEN
            COMMIT;
        END IF;

        SET i = i + 1;
    END WHILE;

    COMMIT;
    SET autocommit = 1;
    DROP TEMPORARY TABLE IF EXISTS TempCountries;
END$$

DELIMITER ;

-- Execute procedure:
-- CALL Seed10kEmployees();
