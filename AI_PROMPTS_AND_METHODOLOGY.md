# AI Prompts & Engineering Methodology Artifact

This document outlines the structured prompt engineering strategies, workflows, and instructions used alongside AI pair programming tools to design, develop, test, and deploy the **Salary Management System**.

---

## 1. System Design & Requirements Elicitation

### Prompt Strategy
- **Goal**: Formulate a comprehensive enterprise specification from a high-level candidate assessment prompt.
- **Role Definition**: Senior Software Architect & Compensation Systems Specialist.

```text
Act as a Principal Enterprise Architect. I need to design a full-stack Salary Management System for ACME Corporation capable of managing 10,000+ employee compensation records across 9 countries (US, UK, India, Germany, France, Canada, Australia, Japan, Singapore).
Generate a production-grade Requirements Document (REQUIREMENTS.md) covering:
1. Executive Summary & User Personas (HR Manager).
2. Explicit In-Scope vs. Deliberately Excluded Out-of-Scope boundaries with trade-off rationale.
3. Detailed Functional Requirements (F1: Employee CRUD, F2: Salary & Multi-country Progressive Tax Engine, F3: Analytics & Pay Equity, F4: Audit Trails & CSV Exports, F5: Stateless JWT Security).
4. Non-Functional SLA Targets (sub-500ms API responses, 2-second 10k-record dashboard load).
```

---

## 2. Database Schema & Indexing Strategy

### Prompt Strategy
- **Goal**: Model a normalized, ACID-compliant relational schema optimized for high-performance financial queries and audit compliance.

```text
Design a MySQL 8.0 / JPA database schema for the Salary Management System.
Requirements:
- 8 core entities: employees, departments, designations, salary_records, salary_history, tax_brackets, audit_logs, users.
- Accurate monetary data types: DECIMAL(12,2) and DECIMAL(15,2) for currency calculations.
- Comprehensive indexing strategy on (employee_id, email, department_id, country, status) to ensure sub-50ms query execution on 10,000+ rows.
- Complete ER diagram and normalization (3NF) documentation in DATABASE_SCHEMA.md.
```

---

## 3. Progressive Tax Calculation Engine

### Prompt Strategy
- **Goal**: Implement a pure, stateless progressive taxation engine that accurately computes tax across dynamic income brackets.

```text
Generate a Java 17 / Spring Boot 3 TaxCalculationService implementing multi-tier progressive taxation.
Requirements:
1. Support dynamic tax brackets per country and tax year.
2. Calculate taxable income across progressive tiers (e.g., 0% on tier 1, 10% on tier 2, 20% on tier 3, etc.).
3. Return both the total tax deduction and an itemized breakdown of tax paid per bracket for transparent payslip vouchers.
4. Provide comprehensive JUnit 5 unit tests covering edge cases (zero salary, minimum wage, highest tax slab boundary, unsupported country fallback).
```

---

## 4. UI/UX Design System & Dashboard Analytics

### Prompt Strategy
- **Goal**: Build an intuitive, state-of-the-art Angular 16 dashboard with glassmorphic SaaS styling and Chart.js data visualizations.

```text
Create an Angular 16 single-page application for the Salary Management System:
1. Design System: Custom SCSS design tokens, glassmorphism card styling, responsive flex/grid layouts, Plus Jakarta Sans typography, and Font Awesome iconography.
2. Executive Analytics Dashboard: Headcount by department, regional payroll distribution, salary spread ratios, and pay equity analysis.
3. Employee Directory: High-performance 10,000-record paginated table with real-time multi-filter and search.
4. Interactive Tax Simulator: Real-time interactive calculation widget allowing HR managers to test hypothetical compensation packages before committing.
```

---

## 5. Cloud Deployment & Docker Orchestration

### Prompt Strategy
- **Goal**: Automate zero-downtime deployment to AWS EC2 and Vercel with automated memory management and swap space.

```text
Create a production docker-compose.yml and automated bash deployment script (deploy-aws-ec2.sh) for AWS EC2:
1. Docker Compose running MySQL 8.0 and Spring Boot 3 with healthy dependencies and memory-limited JVM options (-Xms256m -Xmx512m).
2. Bash script that automatically allocates 2GB swap space to ensure stability on AWS t2.micro/t3.micro free-tier instances.
3. Automated polling health check loop verifying /actuator/health before concluding deployment.
4. Vercel Serverless API proxy (api/[...path].js) to bridge frontend and backend without CORS or mixed-content issues.
```

---

## 6. Human-in-the-Loop Validation & Verification

1. **Static Analysis & Type Checking**: Ran Angular TypeScript compiler and Maven compiler with zero errors.
2. **Deterministic Test Execution**: Verified all backend unit tests pass with `mvn test`.
3. **Database Integrity Verification**: Verified 10,000 seeded employees and progressive tax brackets load cleanly in MySQL.
4. **Live Network Validation**: Validated end-to-end HTTP payload flow between Vercel and AWS EC2.
