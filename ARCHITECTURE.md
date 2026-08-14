# Salary Management System - Architecture & Design Decisions

## System Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                         Client Layer (Browser)                      │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │  Angular SPA (TypeScript)                                    │  │
│  │  - Employee Module (CRUD, Search, Filtering)               │  │
│  │  - Salary Module (CRUD, History, Slips)                    │  │
│  │  - Analytics Module (Dashboard, Reports, Export)           │  │
│  │  - Shared Services (HTTP, Auth, State Mgmt)                │  │
│  └──────────────────────────────────────────────────────────────┘  │
└──────────────────┬───────────────────────────────────────────────────┘
                   │ REST API (JSON)
                   │ HTTPS/JWT Auth
┌──────────────────▼───────────────────────────────────────────────────┐
│                    Server Layer (Spring Boot)                        │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │  API Layer (REST Controllers)                                │  │
│  │  - AuthController         - AnalyticsController             │  │
│  │  - EmployeeController     - DepartmentController            │  │
│  │  - SalaryController       - DesignationController           │  │
│  └──────────────────────────────────────────────────────────────┘  │
│              ↓              (Dependency Injection)                   │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │  Service Layer (Business Logic)                              │  │
│  │  - AuthService            - TaxCalculationService           │  │
│  │  - EmployeeService        - ReportService                   │  │
│  │  - SalaryService          - AuditService                    │  │
│  │  - AnalyticsService       - ValidationService               │  │
│  └──────────────────────────────────────────────────────────────┘  │
│              ↓              (Data Access)                            │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │  Repository Layer (Data Access Objects)                      │  │
│  │  - EmployeeRepository       - TaxBracketRepository          │  │
│  │  - SalaryRepository         - AuditLogRepository            │  │
│  │  - DepartmentRepository     - SalaryHistoryRepository       │  │
│  │  - DesignationRepository                                     │  │
│  └──────────────────────────────────────────────────────────────┘  │
│              ↓              (JPA/Hibernate ORM)                      │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │  Entity Layer (JPA Entities)                                 │  │
│  │  - Employee        - SalaryRecord      - TaxBracket         │  │
│  │  - Department      - SalaryHistory     - AuditLog           │  │
│  │  - Designation                                               │  │
│  └──────────────────────────────────────────────────────────────┘  │
└──────────────────┬───────────────────────────────────────────────────┘
                   │ JDBC
                   │ SQL
┌──────────────────▼───────────────────────────────────────────────────┐
│                    Data Layer (MySQL Database)                       │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │  MySQL Database (salary_management_system)                  │  │
│  │  - employees table           - salary_history table         │  │
│  │  - departments table         - tax_brackets table           │  │
│  │  - designations table        - audit_logs table             │  │
│  │  - salary_records table                                     │  │
│  └──────────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────────┘
```

## Architectural Patterns

### 1. Layered Architecture
- **Presentation Layer**: Angular SPA (handling UI/UX)
- **API Layer**: Spring Boot REST Controllers (request handling)
- **Business Logic Layer**: Services (core functionality)
- **Data Access Layer**: Repositories (database queries)
- **Data Layer**: MySQL Database (persistent storage)

**Rationale**: Clear separation of concerns, easy to test each layer independently, scalable and maintainable.

### 2. Service-Oriented Design
Each service encapsulates business logic:
- `EmployeeService`: Employee CRUD and validation
- `SalaryService`: Salary management and history tracking
- `TaxCalculationService`: Tax calculations (complex logic)
- `AnalyticsService`: Aggregations and reporting
- `AuditService`: Audit logging for compliance

**Rationale**: Single responsibility principle, reusable services, testable business logic.

### 3. Repository Pattern
- Data access abstraction via `JPARepository` extensions
- Loose coupling between service and database layers
- Enables easy switching of database implementation

**Rationale**: Cleaner code, easier testing with mocks, database-agnostic code.

### 4. DTO (Data Transfer Objects) Pattern
- Separate entities from API contracts
- DTOs for request/response payloads
- Entities for database models

**Rationale**: API versioning flexibility, validation separation, better security (avoid exposing internal details).

### 5. JWT-Based Authentication
- Stateless authentication
- Token-based session management
- CORS-enabled for cross-origin requests

**Rationale**: Scalable, doesn't require server-side session storage, suitable for distributed systems.

---

## Design Decisions & Trade-offs

### 1. Framework: Spring Boot vs Other Java Frameworks

| Aspect | Spring Boot | Quarkus | Micronaut |
|--------|------------|---------|-----------|
| Setup Time | Fast with starters | Fast | Very Fast |
| Learning Curve | Moderate | Steeper | Moderate |
| Community/Docs | Excellent | Growing | Good |
| Production Readiness | Very High | High | High |
| **Decision** | ✅ Chosen | - | - |

**Reasoning**: 
- Mature ecosystem with extensive libraries
- Excellent Spring Data JPA support for quick repository implementation
- Large community and abundant resources
- Built-in actuators for monitoring

---

### 2. ORM: JPA/Hibernate vs Raw SQL

| Aspect | JPA/Hibernate | Raw SQL | QueryDSL |
|--------|---------------|---------|----------|
| Development Speed | Fast | Slow | Moderate |
| Type Safety | Good | None | Excellent |
| Maintainability | Easy | Hard | Moderate |
| Performance | Good | Best | Good |
| **Decision** | ✅ Chosen | - | - |

**Reasoning**:
- Reduces boilerplate SQL code
- Automatic schema management with Flyway
- Type-safe queries with Spring Data
- Easy to add transactions and lazy loading
- Trade-off: Raw SQL might be slightly faster for complex aggregations, but JPA provides sufficient performance

---

### 3. State Management: RxJS vs NgRx (Frontend)

| Aspect | RxJS Services | NgRx | Akita |
|--------|---------------|------|-------|
| Complexity | Low | High | Medium |
| Learning Curve | Moderate | Steep | Moderate |
| For MVP | ✅ Good | Overkill | Good |
| For Scale | Limited | Excellent | Good |
| **Decision** | ✅ Chosen for MVP | - | - |

**Reasoning**:
- MVP scope doesn't require complex global state
- RxJS services sufficient for CRUD operations
- Can migrate to NgRx post-MVP if needed
- Reduces initial complexity and development time

---

### 4. Database: MySQL vs PostgreSQL

| Aspect | MySQL | PostgreSQL |
|--------|-------|-----------|
| Setup | Easy | Moderate |
| Performance | Good | Excellent |
| Features | Basic | Advanced (JSONB, window functions, etc.) |
| Cost | Free | Free |
| **Decision** | ✅ Chosen (as per requirements) | - |

**Reasoning**: 
- Simple to set up and manage
- Sufficient for requirements (salary data is relational)
- ACID-compliant for financial data
- Meets assessment constraints

---

### 5. Testing: Unit + Integration vs E2E-Heavy

| Approach | Unit Tests | Integration Tests | E2E Tests |
|----------|-----------|-------------------|-----------|
| Speed | Very Fast | Fast | Slow |
| Reliability | High | High | Medium |
| Coverage | Core Logic | Components | Full Flow |
| **Decision** | ✅ 70% of effort | ✅ 20% of effort | ✅ 10% of effort |

**Reasoning**:
- Unit tests verify core logic (tax calculation, aggregations)
- Integration tests verify API contracts and database interaction
- E2E tests reserved for critical user flows
- Balances speed, reliability, and coverage

---

### 6. Authentication: JWT vs OAuth2 vs Session-Based

| Approach | JWT | OAuth2 | Session-Based |
|----------|-----|--------|---------------|
| Stateless | ✅ Yes | ✅ Yes | ❌ No |
| Scalability | Excellent | Excellent | Poor |
| Implementation | Simple | Complex | Very Simple |
| Multi-tenant | Easy | Hard | Possible |
| **Decision** | ✅ Chosen | - | - |

**Reasoning**:
- Stateless design supports horizontal scaling
- Simpler than OAuth2 for single-organization deployment
- JWT allows future microservices migration
- Easy to test and implement

---

### 7. Calculated Columns: Generated vs Computed at Service

| Approach | Generated Columns | Computed in Service |
|----------|-------------------|-------------------|
| Data Consistency | Excellent | Good |
| Query Performance | Excellent | Good |
| Flexibility | Limited | Excellent |
| Database Dependency | High | Low |
| **Decision** | ✅ Chosen for gross/net | ✅ Chosen for complex logic |

**Reasoning**:
- Simple calculations (gross = base + allowances) → Generated columns
- Complex logic (tax calculation based on brackets) → Service layer
- Ensures financial data consistency while maintaining flexibility

---

### 8. API Versioning: URL-Based vs Header-Based

| Approach | URL-Based | Header-Based |
|----------|-----------|-------------|
| Clarity | Very Clear | Hidden |
| Debugging | Easy | Hard |
| Multiple Versions | Easy | Easy |
| REST Compliance | Moderate | Better |
| **Decision** | ✅ Chosen (`/api/v1`) | - |

**Reasoning**:
- Clear visibility of API version in URLs
- Easier to maintain separate implementations if needed
- Clearer for developers using the API

---

## Security Architecture

### Authentication Flow
```
┌─────────────┐
│  Client     │ 1. POST /auth/login (username, password)
└──────┬──────┘
       │
       ▼
┌────────────────────────────────────────┐
│   Spring Security Filter Chain         │
│  - Validates credentials               │
│  - Generates JWT token                 │
└──────┬───────────────────────────────────┘
       │
       ▼ 2. Response: { token, expiresIn }
┌─────────────┐
│  Client     │
└──────┬──────┘
       │
       ▼ 3. Store token in localStorage
       
       ▼ 4. For subsequent requests: Authorization: Bearer <token>
┌────────────────────────────────────────┐
│   JWT Filter Validates Token           │
│  - Verifies signature                  │
│  - Checks expiration                   │
│  - Extracts user claims                │
└──────┬───────────────────────────────────┘
       │
       ▼ 5. Grant access to protected resource
```

### Authorization (RBAC)
```
┌──────────────┐
│  User Roles  │
├──────────────┤
│ HR_MANAGER   │ → Full access to all operations
│ HR_SPECIALIST│ → CRUD on assigned departments only
│ EMPLOYEE     │ → Read-only own salary data
└──────────────┘
```

Implemented via:
- `@PreAuthorize("hasRole('HR_MANAGER')")` annotations
- Custom authorization checks in services
- Audit logging for sensitive operations

---

## Performance Considerations

### 1. Database Query Optimization
- **Indexes**: Optimized for common filters (department_id, country, status)
- **Pagination**: Always paginated (50-500 records per page)
- **Lazy Loading**: Entity relationships loaded on-demand
- **Database Connection Pooling**: HikariCP (default Spring Boot)

### 2. Caching Strategy
- **Employee List**: Cache department/designation dropdowns (rarely change)
- **Tax Brackets**: Cache in memory (static per year)
- **Dashboard**: Cache aggregations (refresh every 5 minutes)

**Not Cached**: Individual employee/salary data (frequently updated)

### 3. API Response Optimization
- **DTO Projections**: Select only required fields
- **Compression**: GZIP compression enabled for responses
- **CDN**: Static assets (JS, CSS) served from CDN (post-MVP)

### 4. Bulk Operations
- **Batch Inserts**: Use bulk import for 10,000 employees
- **Asynchronous Processing**: Long-running imports via job queue
- **Progress Tracking**: Job status endpoint to monitor imports

---

## Scalability & Future Growth

### Current Target
- **10,000 employees**: Comfortably handled with MySQL + Spring Boot
- **Concurrent users**: ~100-200 simultaneous users
- **Data volume**: ~100 MB database size

### Future Scaling (Post-MVP)
1. **Database**: Migrate to cloud-managed database (AWS RDS, Azure MySQL)
2. **Caching**: Introduce Redis for session/cache management
3. **Microservices**: Split into service modules (Auth, Employee, Salary, Analytics)
4. **Message Queue**: Add Apache Kafka for event-driven architecture
5. **Search**: Elasticsearch for advanced employee/salary search
6. **CDN**: CloudFront/Akamai for static assets and API caching

---

## Error Handling & Logging

### Exception Hierarchy
```
RuntimeException
├── ValidationException (400 Bad Request)
├── AuthenticationException (401 Unauthorized)
├── AuthorizationException (403 Forbidden)
├── ResourceNotFoundException (404 Not Found)
├── DuplicateResourceException (409 Conflict)
└── InternalServerException (500 Internal Server Error)
```

### Logging Strategy
- **DEBUG**: Method entry/exit, variable values (only in development)
- **INFO**: Business events (salary created, employee updated)
- **WARN**: Recoverable errors (failed import, retry logic)
- **ERROR**: Unrecoverable errors, exceptions
- **Tool**: SLF4J with Logback backend

---

## Testing Strategy

### Unit Tests (Service Layer)
```
✅ TaxCalculationService
   - calculateTax(grossSalary, country) → correct tax amount
   - getTaxBrackets(country, year) → correct brackets loaded
   
✅ SalaryService
   - calculateNetSalary() → correct calculation
   - validateSalaryRecord() → validation logic
   
✅ EmployeeService
   - validateEmail(email) → uniqueness check
   - filterEmployees() → correct filtering
```

### Integration Tests (API Layer)
```
✅ EmployeeController
   - GET /employees → returns paginated list
   - POST /employees → creates with validation
   - GET /employees/{id} → returns correct employee
   
✅ SalaryController
   - POST /salaries → creates and calculates tax
   - GET /salaries → filters correctly
```

### Frontend Tests (Angular)
```
✅ EmployeeListComponent
   - renders employee table
   - pagination works
   - search/filter applies correctly
   
✅ SalaryFormComponent
   - form validation works
   - calculations display correctly
```

---

## Deployment Architecture

### Development
- Local MySQL database
- Spring Boot runs on `http://localhost:8080`
- Angular dev server on `http://localhost:4200`

### Production
- Cloud-managed MySQL (AWS RDS / Azure Database)
- Spring Boot deployed as Docker container
- Angular built and served by Nginx
- HTTPS/TLS for all communications
- Load balancer for high availability

See [DEPLOYMENT.md](DEPLOYMENT.md) for detailed deployment guide.

---

## Trade-offs Summary

| Decision | Chosen | Why | Cost |
|----------|--------|-----|------|
| Spring Boot | ✅ | Mature, extensive ecosystem | Learning curve |
| JPA/Hibernate | ✅ | Fast development, type-safe | Performance (optimizable) |
| RxJS (not NgRx) | ✅ | MVP simplicity | Future refactoring if complex |
| MySQL | ✅ | Requirements, ACID guarantees | Less advanced features |
| JWT Auth | ✅ | Stateless, scalable | Token expiration management |
| Generated Columns | ✅ | Data consistency | DB dependency |

---

**Document Version**: 1.0  
**Last Updated**: 2026-08-14  
**Next Review**: After backend scaffolding completion
