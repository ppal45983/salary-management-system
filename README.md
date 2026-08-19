# ACME Salary Management & Compensation Platform

[![Angular](https://img.shields.io/badge/Angular-16.2.0-DD0031?logo=angular)](https://angular.io/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.3-6DB33F?logo=springboot)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql)](https://www.mysql.com/)
[![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk)](https://openjdk.org/)
[![JWT](https://img.shields.io/badge/Security-JWT%20HS256-000000?logo=jsonwebtokens)](https://jwt.io/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?logo=docker)](https://www.docker.com/)

An enterprise-grade, centralized web application built for **ACME Organization** to manage compensation, calculate multi-country progressive taxes, track salary history, and answer organizational payroll analytics questions for **10,000 employees** across 9 countries.

---

## 📑 Project Framing & Assessment Compliance

Built in accordance with the assessment specification ([Salary-Management-Assessment-Candidates.pdf](Salary-Management-Assessment-Candidates.pdf)):
- **User Persona**: HR Manager
- **Problem Statement**: Replace manual, error-prone Excel sheets with an automated, audited, multi-currency salary management system.
- **Dataset Scale**: Architected, indexed, and seeded for **10,000 employees**.
- **Deliberate Scopes & Trade-offs**: Documented in [REQUIREMENTS.md](REQUIREMENTS.md) and [ARCHITECTURE.md](ARCHITECTURE.md).

---

## 🏗️ Architecture & Technology Stack

```
                                 [ Angular 16 Frontend (SPA) ]
                               (TypeScript • Chart.js • SCSS)
                                             │
                                     (HTTP / REST / JWT)
                                             ▼
                             [ Spring Boot 3.1.3 Backend API ]
                       (Spring Security • Data JPA • REST Controllers)
                                             │
                        ┌────────────────────┼────────────────────┐
                        ▼                    ▼                    ▼
                [ Employee Service ]  [ Salary Service ]  [ Analytics Service ]
                        │                    │                    │
                        └────────────────────┼────────────────────┘
                                             ▼
                                  [ MySQL 8.0 Database ]
                               (Indexed for 10k+ Records)
```

### Backend:
- **Framework**: Java 17 + Spring Boot 3.1.3
- **Security**: Spring Security 6 + JJWT (Stateless Bearer Tokens, BCrypt)
- **Database / ORM**: MySQL 8.0 / Hibernate JPA with query optimization & pagination
- **Documentation**: Swagger OpenAPI 3.0 (`/api/v1/swagger-ui.html`)
- **Testing**: JUnit 5 + Mockito

### Frontend:
- **Framework**: Angular 16 (TypeScript)
- **Styling**: Vanilla SCSS Design System with modern glassmorphism, responsive grid, and custom tokens
- **Visualizations**: Chart.js for executive analytics & distributions
- **Typography & Icons**: Google Fonts (Plus Jakarta Sans, Outfit) + Font Awesome 6
- **Testing**: Jasmine + Karma

---

## 🚀 Key Features

1. **Executive Compensation Dashboard**:
   - High-level KPIs (Total Headcount, Active Payroll, Pending Approvals).
   - Monthly & Annual Payroll by Currency (USD, GBP, EUR, INR, CAD, AUD, JPY, SGD).
   - Department Headcount Bar Chart & Regional Split Doughnut Chart.
   - Annualized salary distribution ranges (Min, Average, Max).
   - Pay Equity analysis & spread ratio per designation.

2. **Employee Directory (10,000 Employees)**:
   - High-performance paginated table with under 500ms response time.
   - Search by name, email, employee ID.
   - Filters by Department, Country, and Employment Status.
   - Complete Employee profile modal with compensation progression history.
   - Export employee directory to CSV.

3. **Salary Management & Approvals**:
   - Multi-currency salary record creation with automatic tax calculations.
   - Salary status lifecycle (`DRAFT` → `INACTIVE` / Pending Approval → `ACTIVE` → `ARCHIVED`).
   - Historical salary progression audit trail.
   - One-click CSV export of salary records.

4. **Multi-Country Progressive Tax Engine & Simulator**:
   - Progressive tax slab calculations across 9 countries (US, UK, India, Germany, France, Canada, Australia, Japan, Singapore).
   - Interactive live salary simulator with bracket-by-bracket breakdown.

5. **Official Salary Slip Generation**:
   - Formatted printable payslip voucher with company branding (`ACME Global Corporation`).
   - Earnings, pre-tax deductions, progressive tax slabs applied, and net pay.
   - Print & PDF download layout.

6. **Master Data Explorer**:
   - Inspect 9 Departments (with budgets & employee counts).
   - Inspect 14 Designations (with salary bands & job levels).
   - Inspect Country Tax Brackets.

---

## 📂 Repository Structure

```
Salary-Management-System/
├── backend/
│   ├── src/main/java/com/sms/
│   │   ├── config/              # SecurityConfig, SwaggerConfig
│   │   ├── controller/          # REST Controllers (Auth, Employee, Salary, Analytics, Masters)
│   │   ├── dto/                 # API Contracts & Request/Response DTOs
│   │   ├── entity/              # 8 JPA Entities (Employee, SalaryRecord, TaxBracket, etc.)
│   │   ├── exception/           # Global Exception Handler & Business Exceptions
│   │   ├── repository/          # Spring Data Repositories with custom queries
│   │   ├── security/            # JWT Token Provider & Auth Filter
│   │   ├── service/             # Business Logic, Taxes, Analytics, CSV Export
│   │   └── util/                # DataSeederRunner (10k employee population)
│   ├── src/test/java/com/sms/   # Comprehensive Unit & Integration Tests
│   ├── sql/                     # Schema DDL, Seed Scripts & 10k Generator
│   └── pom.xml                  # Maven Configuration
├── src/                         # Angular 16 Frontend
│   ├── app/
│   │   ├── components/          # Dashboard, Employees, Salaries, Tax Calculator, Masters, Login
│   │   ├── core/                # Models, Services, Interceptors, Guards, Mock Data
│   │   └── app.module.ts        # App Module & Routing
│   ├── styles.scss              # Global Design System & Variables
│   └── index.html               # Entry HTML
├── Dockerfile                   # Multi-stage production container build
├── docker-compose.yml           # One-command full-stack container orchestration
├── REQUIREMENTS.md              # One-page Requirements Document
├── DATABASE_SCHEMA.md           # ER Diagrams, Indexing & Normalization
├── API_DESIGN.md                # 23 REST API Endpoint Specifications
├── ARCHITECTURE.md              # Layered Architecture & Trade-off Analysis
└── README.md                    # Project Documentation
```

---

## ⚡ Quick Start & Running Locally

### Option 1: Running with Docker (Recommended)
```bash
docker-compose up --build
```
- Frontend UI: `http://localhost`
- Backend REST API: `http://localhost:8080/api/v1`
- Swagger API Docs: `http://localhost:8080/api/v1/swagger-ui.html`

---

### Option 2: Running Frontend & Backend Manually

#### 1. Start Angular Frontend:
```bash
npm install
npm start
```
Navigate to `http://localhost:4200/`.

#### 2. Start Spring Boot Backend:
```bash
cd backend
mvn spring-boot:run
```

#### 3. Default Demo Credentials:
- **Username**: `hr_manager@acme.com` (or `hr_manager`)
- **Password**: `admin123`
*(A 1-click **"Auto-Fill Demo Credentials"** button is also built into the login screen)*.

---

## 🧪 Running Automated Tests

### Backend Tests:
```bash
cd backend
mvn test
```
Runs unit & integration tests covering `TaxCalculationServiceTest`, `SalaryServiceTest`, `EmployeeServiceTest`, `AnalyticsServiceTest`, `AuthenticationServiceTest`, and `JwtTokenProviderTest`.

### Frontend Tests:
```bash
npm test -- --watch=false --browsers=ChromeHeadless
```

---

## 📊 Development Phases Summary

- ✅ **Phase A**: Requirements framing, Schema design, REST API specifications, Architecture trade-offs.
- ✅ **Phase B**: Core Spring Boot infrastructure, JPA entities, Repositories, JWT security, Tax calculation.
- ✅ **Phase C**: REST APIs & Services (Employees, Salaries, Analytics, Masters, CSV Exports).
- ✅ **Phase D**: Backend test suite (Services & Security).
- ✅ **Phase E**: Full Angular 16 Web Application with rich SaaS UI and live tax simulation.
- ✅ **Phase F**: Frontend unit test suite.
- ✅ **Phase G**: 10,000 Employee realistic dataset seeding engine.
- ✅ **Phase H & I**: Containerization, Docker orchestration, and system verification.
