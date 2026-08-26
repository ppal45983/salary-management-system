# Submission Artifacts Index & Engineering Notes

This directory contains the complete collection of engineering artifacts, architectural diagrams, design specifications, trade-off analyses, and AI prompt workflows created for the **Salary Management System**.

---

## 📑 Artifacts Directory

| Artifact | File Link | Description & Key Contents |
| :--- | :--- | :--- |
| **1. Requirements Document** | [`REQUIREMENTS.md`](REQUIREMENTS.md) | Executive summary, user personas, in-scope vs out-of-scope matrix, functional specifications (F1–F5), non-functional SLA metrics. |
| **2. Architecture & Design** | [`ARCHITECTURE.md`](ARCHITECTURE.md) | High-level system architecture, layered component diagrams, data flow models, security architecture, and error handling strategies. |
| **3. Database Schema & ERD** | [`DATABASE_SCHEMA.md`](DATABASE_SCHEMA.md) | Complete MySQL relational model, 8 JPA entities, normalization (3NF), 12+ performance indexes, and audit log schema. |
| **4. REST API Specification** | [`API_DESIGN.md`](API_DESIGN.md) | Full contract specifications for 23 REST API endpoints with request/response JSON schemas, validation rules, and error codes. |
| **5. Trade-offs & Decisions** | [`TRADE_OFFS_AND_DESIGN_DECISIONS.md`](TRADE_OFFS_AND_DESIGN_DECISIONS.md) | Rationale for monolithic vs microservices, SQL vs NoSQL, stateless JWT vs session cache, and 10k-record performance optimizations. |
| **6. AI Prompts & Methodology** | [`AI_PROMPTS_AND_METHODOLOGY.md`](AI_PROMPTS_AND_METHODOLOGY.md) | Prompts, strategies, and workflows used with AI tools for system architecture, tax calculation engine, and automated deployments. |
| **7. Cloud Deployment Guide** | [`AWS_DEPLOYMENT_GUIDE.md`](AWS_DEPLOYMENT_GUIDE.md) | AWS EC2 provisioning, CloudFormation template, Docker Compose configuration, and automated swap memory allocation. |
| **8. Phase Execution Summary** | [`PHASE_A_SUMMARY.md`](PHASE_A_SUMMARY.md) | Iterative phase breakdown, milestone deliverables, and test coverage summaries. |

---

## 🎯 Quick Verification Reference

- **Live Application (Vercel)**: `https://salary-management-system-793w3p9r5-ppal45983s-projects.vercel.app/dashboard`
- **Backend API (AWS EC2)**: `http://13.204.76.101:8080/api/v1`
- **Demo Credentials**: `hr_manager` / `admin123`
