# Architectural Trade-offs & Design Decisions Artifact

This document details the critical architectural decisions, evaluated alternatives, trade-offs, and performance optimizations implemented in the **Salary Management System**.

---

## 1. Architectural Style: Modular Monolith vs. Microservices

| Attribute | Modular Monolith (Chosen) | Microservices (Alternative) |
| :--- | :--- | :--- |
| **Rationale** | Optimal for single-domain compensation management, unified ACID transactions, and streamlined deployment on AWS `t2.micro` / `t3.micro`. | Introduces distributed transaction overhead (Saga pattern), network latency, and high memory footprint exceeding free-tier limits. |
| **Trade-off** | All services run in a single process; horizontal scaling scales the entire container. | Sub-services can scale independently, but complexity and hosting cost increase significantly. |

---

## 2. Database Selection: MySQL 8 Relational vs. Document NoSQL (MongoDB)

| Attribute | MySQL 8.0 Relational (Chosen) | MongoDB NoSQL (Alternative) |
| :--- | :--- | :--- |
| **Rationale** | Financial compensation data demands strict ACID transactions, foreign-key referential integrity, and exact `DECIMAL` numeric precision. | Flexible schema without strict relational integrity increases risk of payroll calculation inconsistencies and orphaned records. |
| **Trade-off** | Schema migrations require deliberate planning (Flyway/Hibernate). | Easy schema changes, but lack of multi-entity ACID guarantees and joins makes aggregate financial reporting complex. |

---

## 3. Security Architecture: Stateless JWT vs. Server-Side Sessions (Redis)

| Attribute | Stateless JWT (Chosen) | Server-Side Redis Sessions (Alternative) |
| :--- | :--- | :--- |
| **Rationale** | Completely stateless authentication with zero server-side session memory overhead; seamless scalability across distributed containers. | Requires running and maintaining a Redis cache container, consuming precious memory on 1GB RAM instances. |
| **Trade-off** | Tokens cannot be revoked immediately before expiration (mitigated by short TTL and refresh flow). | Instant session revocation at the cost of infrastructure overhead. |

---

## 4. Performance Optimizations for 10,000+ Records

### A. Relational Indexing Strategy
- **Composite Index `(employee_id, status)`**: Accelerates active salary lookups to $O(1)$.
- **Covering Index `(department_id, country)`**: Enables instantaneous aggregation for department distribution analytics without full table scans.
- **Unique Indexes `(username, email)`**: Enforces zero-duplicate constraints at the database engine level.

### B. JVM Memory Management on AWS Free Tier
- **JVM Constraints**: `-Xms256m -Xmx512m -XX:+UseG1GC` prevents out-of-memory errors on 1GB RAM instances.
- **Linux Swap Allocation**: 2GB swapfile configured on EC2 prevents kernel OOM-killer during simultaneous Maven compilation and Docker image generation.

### C. Frontend Pagination & Virtualization
- Server-side Spring Data JPA pagination (`Pageable`) streams only 10 to 50 records per HTTP roundtrip, keeping client-side memory under 20MB even with 10,000 total database records.
