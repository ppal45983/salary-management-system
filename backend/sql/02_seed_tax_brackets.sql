-- Salary Management System - Seed Tax Brackets
-- Version: 1.0
-- This script seeds tax brackets for different countries (2024)

-- ============================================
-- US TAX BRACKETS 2024 (Single Filer)
-- ============================================
INSERT INTO tax_brackets (country, tax_year, income_from, income_to, tax_rate) VALUES
('US', 2024, 0, 11000, 0.10),
('US', 2024, 11000, 44725, 0.12),
('US', 2024, 44725, 95375, 0.22),
('US', 2024, 95375, 182100, 0.24),
('US', 2024, 182100, 231250, 0.32),
('US', 2024, 231250, 578125, 0.35),
('US', 2024, 578125, 999999999, 0.37);

-- ============================================
-- UK TAX BRACKETS 2024-25
-- ============================================
INSERT INTO tax_brackets (country, tax_year, income_from, income_to, tax_rate) VALUES
('GB', 2024, 0, 12570, 0.00),
('GB', 2024, 12570, 50270, 0.20),
('GB', 2024, 50270, 125140, 0.40),
('GB', 2024, 125140, 999999999, 0.45);

-- ============================================
-- INDIA TAX BRACKETS 2024-25
-- ============================================
INSERT INTO tax_brackets (country, tax_year, income_from, income_to, tax_rate) VALUES
('IN', 2024, 0, 300000, 0.00),
('IN', 2024, 300000, 750000, 0.05),
('IN', 2024, 750000, 1500000, 0.20),
('IN', 2024, 1500000, 999999999, 0.30);

-- ============================================
-- CANADA TAX BRACKETS 2024
-- ============================================
INSERT INTO tax_brackets (country, tax_year, income_from, income_to, tax_rate) VALUES
('CA', 2024, 0, 55867, 0.15),
('CA', 2024, 55867, 111733, 0.205),
('CA', 2024, 111733, 173205, 0.26),
('CA', 2024, 173205, 246752, 0.29),
('CA', 2024, 246752, 999999999, 0.33);

-- ============================================
-- AUSTRALIA TAX BRACKETS 2024-25
-- ============================================
INSERT INTO tax_brackets (country, tax_year, income_from, income_to, tax_rate) VALUES
('AU', 2024, 0, 18200, 0.00),
('AU', 2024, 18200, 45000, 0.19),
('AU', 2024, 45000, 120000, 0.325),
('AU', 2024, 120000, 180000, 0.37),
('AU', 2024, 180000, 999999999, 0.45);

-- ============================================
-- GERMANY TAX BRACKETS 2024
-- ============================================
INSERT INTO tax_brackets (country, tax_year, income_from, income_to, tax_rate) VALUES
('DE', 2024, 0, 11604, 0.00),
('DE', 2024, 11604, 47348, 0.19),
('DE', 2024, 47348, 102699, 0.42),
('DE', 2024, 102699, 999999999, 0.45);

-- ============================================
-- FRANCE TAX BRACKETS 2024
-- ============================================
INSERT INTO tax_brackets (country, tax_year, income_from, income_to, tax_rate) VALUES
('FR', 2024, 0, 11294, 0.00),
('FR', 2024, 11294, 28797, 0.11),
('FR', 2024, 28797, 82341, 0.30),
('FR', 2024, 82341, 177106, 0.41),
('FR', 2024, 177106, 999999999, 0.45);

-- ============================================
-- JAPAN TAX BRACKETS 2024
-- ============================================
INSERT INTO tax_brackets (country, tax_year, income_from, income_to, tax_rate) VALUES
('JP', 2024, 0, 1949000, 0.05),
('JP', 2024, 1949000, 3299000, 0.10),
('JP', 2024, 3299000, 6994000, 0.20),
('JP', 2024, 6994000, 9240000, 0.23),
('JP', 2024, 9240000, 17100000, 0.33),
('JP', 2024, 17100000, 999999999, 0.45);

-- ============================================
-- SINGAPORE TAX BRACKETS 2024
-- ============================================
INSERT INTO tax_brackets (country, tax_year, income_from, income_to, tax_rate) VALUES
('SG', 2024, 0, 20000, 0.00),
('SG', 2024, 20000, 30000, 0.02),
('SG', 2024, 30000, 40000, 0.035),
('SG', 2024, 40000, 80000, 0.07),
('SG', 2024, 80000, 120000, 0.11),
('SG', 2024, 120000, 160000, 0.15),
('SG', 2024, 160000, 200000, 0.18),
('SG', 2024, 200000, 320000, 0.19),
('SG', 2024, 320000, 999999999, 0.22);

-- ============================================
-- SEED DEPARTMENTS
-- ============================================
INSERT INTO departments (name, description) VALUES
('Engineering', 'Software development and technical teams'),
('Sales', 'Sales and business development'),
('Marketing', 'Marketing and communications'),
('HR', 'Human Resources'),
('Finance', 'Finance and accounting'),
('Operations', 'Operations and logistics'),
('Product', 'Product management and design'),
('Legal', 'Legal and compliance'),
('Data Science', 'Analytics and machine learning');

-- ============================================
-- SEED DESIGNATIONS
-- ============================================
INSERT INTO designations (title, description) VALUES
('Software Engineer', 'Full-stack software development'),
('Senior Software Engineer', 'Senior software engineer with 5+ years experience'),
('Engineering Manager', 'Manages engineering team'),
('Sales Representative', 'Sales and client management'),
('Sales Manager', 'Manages sales team'),
('Marketing Specialist', 'Marketing campaigns and strategy'),
('HR Manager', 'Human resources management'),
('Finance Manager', 'Financial planning and analysis'),
('Operations Manager', 'Operations management'),
('Product Manager', 'Product development and strategy'),
('Data Scientist', 'Data analysis and machine learning'),
('Legal Counsel', 'Legal advisory and compliance'),
('Junior Engineer', 'Entry-level software engineer'),
('Intern', 'Internship program');

-- ============================================
-- SEED DEFAULT ADMIN USER (password: admin123)
-- ============================================
INSERT INTO users (username, email, password, role, enabled) VALUES
('admin', 'admin@acme.com', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy2QFTM', 'HR_MANAGER', TRUE),
('hr_manager', 'hr.manager@acme.com', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy2QFTM', 'HR_MANAGER', TRUE);

-- Note: Passwords are bcrypt hashed. Password: admin123
-- To generate a new password hash, use an online bcrypt tool or Java BCrypt library

-- ============================================
-- SEED DATA COMPLETE
-- ============================================
