# Salary Management System - Database Schema Design

## Schema Overview

The database uses a relational model with 6 core entities:
1. **employees** - Employee master data
2. **departments** - Department master data
3. **designations** - Job title/role master data
4. **salary_records** - Current and historical salary data
5. **tax_brackets** - Country-specific tax rate configurations
6. **audit_logs** - Change tracking and audit trail

## Entity Relationship Diagram (ERD)

```
┌─────────────────────┐
│   DEPARTMENTS       │
├─────────────────────┤
│ id (PK)            │
│ name               │
│ description        │
│ created_at         │
│ updated_at         │
└─────────────────────┘
        ▲
        │ (1:N)
        │
┌─────────────────────┐         ┌──────────────────┐
│   EMPLOYEES         │         │ DESIGNATIONS     │
├─────────────────────┤         ├──────────────────┤
│ id (PK)            │◄────────│ id (PK)          │
│ name               │         │ title            │
│ email (UNIQUE)     │         │ description      │
│ department_id (FK) │         │ created_at       │
│ designation_id (FK)│         │ updated_at       │
│ hire_date          │         └──────────────────┘
│ country            │
│ currency           │
│ status             │
│ created_at         │
│ updated_at         │
└─────────────────────┘
        ▲
        │ (1:N)
        │
┌─────────────────────────┐
│   SALARY_RECORDS        │
├─────────────────────────┤
│ id (PK)                │
│ employee_id (FK)       │
│ base_salary            │
│ allowances             │
│ deductions             │
│ tax                    │
│ net_salary             │
│ currency               │
│ effective_date         │
│ status                 │
│ created_at             │
│ updated_at             │
└─────────────────────────┘
        ▲
        │ (1:N)
        │
┌──────────────────────────┐
│   SALARY_HISTORY         │
├──────────────────────────┤
│ id (PK)                 │
│ salary_record_id (FK)   │
│ change_type             │
│ old_value               │
│ new_value               │
│ changed_by              │
│ changed_at              │
└──────────────────────────┘

┌─────────────────────┐
│  TAX_BRACKETS       │
├─────────────────────┤
│ id (PK)            │
│ country            │
│ year               │
│ income_from        │
│ income_to          │
│ tax_rate           │
│ created_at         │
└─────────────────────┘

┌─────────────────────┐
│   AUDIT_LOGS        │
├─────────────────────┤
│ id (PK)            │
│ entity_type        │
│ entity_id          │
│ action             │
│ user_id            │
│ old_value          │
│ new_value          │
│ timestamp          │
│ details (JSON)     │
└─────────────────────┘
```

## Table Definitions

### 1. DEPARTMENTS
Stores organizational department master data.

```sql
CREATE TABLE departments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name)
);
```

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, Auto-increment | Unique department ID |
| name | VARCHAR(100) | NOT NULL, UNIQUE | Department name (e.g., Engineering, HR, Sales) |
| description | TEXT | Nullable | Department description |
| created_at | TIMESTAMP | Default NOW() | Creation timestamp |
| updated_at | TIMESTAMP | Default NOW() | Last update timestamp |

---

### 2. DESIGNATIONS
Stores job titles and roles.

```sql
CREATE TABLE designations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_title (title)
);
```

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, Auto-increment | Unique designation ID |
| title | VARCHAR(100) | NOT NULL, UNIQUE | Job title (e.g., Software Engineer, Manager) |
| description | TEXT | Nullable | Role description |
| created_at | TIMESTAMP | Default NOW() | Creation timestamp |
| updated_at | TIMESTAMP | Default NOW() | Last update timestamp |

---

### 3. EMPLOYEES
Stores employee master data.

```sql
CREATE TABLE employees (
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
);
```

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, Auto-increment | Unique employee ID |
| name | VARCHAR(150) | NOT NULL | Employee full name |
| email | VARCHAR(100) | NOT NULL, UNIQUE | Work email (unique identifier) |
| department_id | BIGINT | FK (departments) | Employee's department |
| designation_id | BIGINT | FK (designations) | Employee's job title/role |
| hire_date | DATE | NOT NULL | Joining date |
| country | VARCHAR(2) | NOT NULL | Country code (ISO 3166-1 alpha-2, e.g., US, IN, GB) |
| currency | VARCHAR(3) | NOT NULL, Default 'USD' | Salary currency (ISO 4217, e.g., USD, EUR, INR) |
| status | ENUM | Default 'ACTIVE' | Employment status: ACTIVE, INACTIVE, ON_LEAVE |
| created_at | TIMESTAMP | Default NOW() | Record creation time |
| updated_at | TIMESTAMP | Default NOW() | Last modification time |

**Indexes**:
- `email`: Fast lookup by email
- `department_id`: Queries by department
- `country`: Analytics by country
- `status`: Filter active/inactive employees

---

### 4. SALARY_RECORDS
Stores current and historical salary information.

```sql
CREATE TABLE salary_records (
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
    UNIQUE KEY unique_active_salary (employee_id, status) -- Only one ACTIVE salary per employee
);
```

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, Auto-increment | Unique salary record ID |
| employee_id | BIGINT | FK (employees) | Employee reference |
| base_salary | DECIMAL(12,2) | NOT NULL | Base monthly salary |
| allowances | DECIMAL(12,2) | Default 0 | Monthly allowances (HRA, DA, etc.) |
| deductions | DECIMAL(12,2) | Default 0 | Monthly deductions (insurance, loans, etc.) |
| gross_salary | DECIMAL(12,2) | GENERATED | Calculated: base_salary + allowances |
| tax | DECIMAL(12,2) | Default 0 | Calculated tax based on country/bracket |
| net_salary | DECIMAL(12,2) | GENERATED | Calculated: gross - deductions - tax |
| currency | VARCHAR(3) | NOT NULL, Default 'USD' | Salary currency |
| effective_date | DATE | NOT NULL | Date from which this salary applies |
| status | ENUM | Default 'ACTIVE' | DRAFT (pending), ACTIVE (current), ARCHIVED (history) |
| created_at | TIMESTAMP | Default NOW() | Record creation time |
| updated_at | TIMESTAMP | Default NOW() | Last modification time |

**Calculated Columns** (Using MySQL generated columns):
- `gross_salary`: Base + Allowances
- `net_salary`: Gross - Deductions - Tax

**Indexes**:
- `employee_id`: Fast lookup by employee
- `effective_date`: Queries within date ranges
- `status`: Filter active/draft/archived
- `unique_active_salary`: Ensures only one ACTIVE salary per employee

---

### 5. SALARY_HISTORY
Tracks all changes to salary records for audit trail.

```sql
CREATE TABLE salary_history (
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
);
```

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, Auto-increment | Unique history record ID |
| salary_record_id | BIGINT | FK (salary_records) | Associated salary record |
| change_type | VARCHAR(50) | NOT NULL | Type: CREATE, UPDATE, DELETE, ARCHIVE |
| old_value | DECIMAL(12,2) | Nullable | Previous value (NULL for CREATE) |
| new_value | DECIMAL(12,2) | Nullable | New value |
| field_name | VARCHAR(100) | Nullable | Which field changed (e.g., 'base_salary', 'tax') |
| changed_by | VARCHAR(100) | NOT NULL | Username who made the change |
| changed_at | TIMESTAMP | Default NOW() | When change occurred |

**Indexes**:
- `salary_record_id`: Fast lookup of all changes for a salary record
- `changed_at`: Audit trail by date range

---

### 6. TAX_BRACKETS
Configurable tax brackets per country and year.

```sql
CREATE TABLE tax_brackets (
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
);
```

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, Auto-increment | Unique bracket ID |
| country | VARCHAR(2) | NOT NULL | Country code (e.g., US, IN, GB) |
| tax_year | INT | NOT NULL | Tax year (e.g., 2024, 2025) |
| income_from | DECIMAL(12,2) | NOT NULL | Income range start |
| income_to | DECIMAL(12,2) | NOT NULL | Income range end |
| tax_rate | DECIMAL(5,3) | NOT NULL | Tax rate as decimal (e.g., 0.20 = 20%) |
| created_at | TIMESTAMP | Default NOW() | Creation timestamp |

**Unique Constraint**: No duplicate brackets for same country, year, and income range

**Example Data** (US 2024):
| Country | Year | Income From | Income To | Tax Rate |
|---------|------|-------------|-----------|----------|
| US | 2024 | 0 | 11000 | 0.10 |
| US | 2024 | 11000 | 44725 | 0.12 |
| US | 2024 | 44725 | 95375 | 0.22 |
| US | 2024 | 95375 | 182100 | 0.24 |

---

### 7. AUDIT_LOGS
General audit trail for all critical operations.

```sql
CREATE TABLE audit_logs (
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
);
```

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, Auto-increment | Unique audit log ID |
| entity_type | VARCHAR(50) | NOT NULL | Type of entity (EMPLOYEE, SALARY, DEPARTMENT) |
| entity_id | BIGINT | NOT NULL | ID of the entity |
| action | VARCHAR(50) | NOT NULL | Action performed (CREATE, UPDATE, DELETE, EXPORT) |
| user_id | VARCHAR(100) | Nullable | User who performed the action |
| details | JSON | Nullable | Additional context (e.g., old/new values) |
| timestamp | TIMESTAMP | Default NOW() | When action occurred |

---

## Indexing Strategy

| Table | Indexes | Purpose |
|-------|---------|---------|
| employees | email, department_id, country, status | Fast lookups, filtering, analytics |
| salary_records | employee_id, effective_date, status | Salary history, active salary lookup |
| salary_history | salary_record_id, changed_at | Audit trail queries |
| tax_brackets | country, tax_year | Quick tax bracket lookups |
| audit_logs | entity_type, entity_id, timestamp | Audit trail filtering |

## Key Constraints

1. **Referential Integrity**:
   - Employees → Departments (RESTRICT: prevent department deletion if employees exist)
   - Employees → Designations (RESTRICT: prevent designation deletion if employees exist)
   - Salary_Records → Employees (RESTRICT: prevent employee deletion if salary records exist)
   - Salary_History → Salary_Records (CASCADE: delete history when salary record deleted)

2. **Uniqueness**:
   - Employee email must be unique
   - Department name must be unique
   - Designation title must be unique
   - Only ONE ACTIVE salary record per employee (composite unique constraint)

3. **Data Validation** (to be enforced at application level):
   - Salary values must be non-negative
   - Tax rate must be between 0 and 1
   - Income brackets must not overlap within same country/year
   - Hire date should not be in future

## Migration Scripts

### Initial Schema Creation
See `sql/01_initial_schema.sql`

### Sample Data (Tax Brackets)
See `sql/02_seed_tax_brackets.sql`

## Design Decisions

1. **Salary History**: Separate table instead of JSON column for better queryability and audit compliance
2. **Generated Columns**: gross_salary and net_salary computed to prevent calculation errors
3. **Status Column**: Tracks salary record lifecycle (DRAFT → ACTIVE → ARCHIVED) instead of soft deletes
4. **Tax Calculation**: Applied at salary record level, not at query time (for consistency and audit)
5. **Unique Active Salary**: Constraint ensures only one active salary per employee at any time
6. **Decimal Precision**: DECIMAL(12,2) for financial data (no floating-point rounding errors)
7. **ISO Standards**: Country codes (ISO 3166-1), Currency codes (ISO 4217) for international compliance

---

**Document Version**: 1.0  
**Last Updated**: 2026-08-14
