# Salary Management System - API Design Specification

## Base Configuration

- **Base URL**: `http://localhost:8080/api/v1`
- **Authentication**: JWT Bearer Token
- **Content-Type**: `application/json`
- **Response Format**: JSON
- **Versioning**: URL-based (`/api/v1`)

## Authentication

All endpoints (except Login) require a JWT Bearer token in the Authorization header:

```http
Authorization: Bearer <jwt_token>
```

### Login Endpoint
```http
POST /auth/login
Content-Type: application/json

{
  "username": "hr_manager@acme.com",
  "password": "password123"
}

Response (200 OK):
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 86400,
  "user": {
    "id": "user_123",
    "username": "hr_manager@acme.com",
    "role": "HR_MANAGER"
  }
}
```

---

## 1. EMPLOYEE MANAGEMENT

### 1.1 Get All Employees (with Pagination & Filtering)
```http
GET /employees?page=1&pageSize=50&department=Engineering&country=US&status=ACTIVE&search=john
Authorization: Bearer <token>

Response (200 OK):
{
  "data": [
    {
      "id": 1,
      "name": "John Doe",
      "email": "john.doe@acme.com",
      "department": {
        "id": 1,
        "name": "Engineering"
      },
      "designation": {
        "id": 1,
        "title": "Senior Software Engineer"
      },
      "hireDate": "2020-05-15",
      "country": "US",
      "currency": "USD",
      "status": "ACTIVE",
      "createdAt": "2024-01-01T10:00:00Z",
      "updatedAt": "2024-08-14T15:30:00Z"
    }
  ],
  "pagination": {
    "currentPage": 1,
    "pageSize": 50,
    "totalRecords": 10000,
    "totalPages": 200
  }
}
```

**Query Parameters**:
- `page` (optional, default: 1): Page number
- `pageSize` (optional, default: 50, max: 500): Records per page
- `department` (optional): Filter by department name or ID
- `country` (optional): Filter by country code
- `status` (optional): Filter by status (ACTIVE, INACTIVE, ON_LEAVE)
- `search` (optional): Search by name or email

**Response Codes**:
- `200 OK`: Successfully retrieved employees
- `400 Bad Request`: Invalid query parameters
- `401 Unauthorized`: Missing/invalid token
- `403 Forbidden`: Insufficient permissions

---

### 1.2 Get Employee by ID
```http
GET /employees/{id}
Authorization: Bearer <token>

Response (200 OK):
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@acme.com",
  "department": {
    "id": 1,
    "name": "Engineering"
  },
  "designation": {
    "id": 1,
    "title": "Senior Software Engineer"
  },
  "hireDate": "2020-05-15",
  "country": "US",
  "currency": "USD",
  "status": "ACTIVE",
  "createdAt": "2024-01-01T10:00:00Z",
  "updatedAt": "2024-08-14T15:30:00Z"
}
```

**Response Codes**:
- `200 OK`: Employee found
- `401 Unauthorized`: Missing/invalid token
- `404 Not Found`: Employee not found

---

### 1.3 Create Employee
```http
POST /employees
Content-Type: application/json
Authorization: Bearer <token>

{
  "name": "Jane Smith",
  "email": "jane.smith@acme.com",
  "departmentId": 2,
  "designationId": 3,
  "hireDate": "2024-08-14",
  "country": "GB",
  "currency": "GBP",
  "status": "ACTIVE"
}

Response (201 Created):
{
  "id": 10001,
  "name": "Jane Smith",
  "email": "jane.smith@acme.com",
  "department": {
    "id": 2,
    "name": "HR"
  },
  "designation": {
    "id": 3,
    "title": "HR Manager"
  },
  "hireDate": "2024-08-14",
  "country": "GB",
  "currency": "GBP",
  "status": "ACTIVE",
  "createdAt": "2024-08-14T16:00:00Z",
  "updatedAt": "2024-08-14T16:00:00Z"
}
```

**Validation**:
- `name`: Required, max 150 characters
- `email`: Required, valid email format, must be unique
- `departmentId`: Required, must exist in departments table
- `designationId`: Required, must exist in designations table
- `hireDate`: Required, valid date, not in future
- `country`: Required, valid ISO 3166-1 alpha-2 code
- `currency`: Required, valid ISO 4217 code

**Response Codes**:
- `201 Created`: Employee created successfully
- `400 Bad Request`: Validation error
- `409 Conflict`: Email already exists
- `401 Unauthorized`: Missing/invalid token
- `403 Forbidden`: Insufficient permissions

---

### 1.4 Update Employee
```http
PUT /employees/{id}
Content-Type: application/json
Authorization: Bearer <token>

{
  "name": "Jane Smith",
  "departmentId": 2,
  "designationId": 4,
  "status": "ACTIVE"
}

Response (200 OK):
{
  "id": 10001,
  "name": "Jane Smith",
  "email": "jane.smith@acme.com",
  "department": {
    "id": 2,
    "name": "HR"
  },
  "designation": {
    "id": 4,
    "title": "Senior HR Manager"
  },
  "hireDate": "2024-08-14",
  "country": "GB",
  "currency": "GBP",
  "status": "ACTIVE",
  "createdAt": "2024-08-14T16:00:00Z",
  "updatedAt": "2024-08-14T16:30:00Z"
}
```

**Note**: Email and hireDate cannot be updated

**Response Codes**:
- `200 OK`: Employee updated
- `400 Bad Request`: Validation error
- `401 Unauthorized`: Missing/invalid token
- `404 Not Found`: Employee not found
- `403 Forbidden`: Insufficient permissions

---

### 1.5 Delete Employee
```http
DELETE /employees/{id}
Authorization: Bearer <token>

Response (204 No Content)
```

**Constraints**: Cannot delete if employee has active salary records

**Response Codes**:
- `204 No Content`: Employee deleted
- `400 Bad Request`: Employee has active salary records
- `401 Unauthorized`: Missing/invalid token
- `404 Not Found`: Employee not found
- `403 Forbidden`: Insufficient permissions

---

### 1.6 Bulk Import Employees
```http
POST /employees/bulk-import
Content-Type: multipart/form-data
Authorization: Bearer <token>

Form Data:
- file: CSV file (max 10 MB, max 5000 records)

CSV Format:
name,email,departmentId,designationId,hireDate,country,currency,status
John Doe,john.doe@acme.com,1,1,2020-05-15,US,USD,ACTIVE
Jane Smith,jane.smith@acme.com,2,2,2021-03-20,GB,GBP,ACTIVE

Response (202 Accepted):
{
  "jobId": "import_job_12345",
  "status": "PROCESSING",
  "totalRecords": 2,
  "processedRecords": 0,
  "failedRecords": 0
}
```

**Response Codes**:
- `202 Accepted`: Import job started
- `400 Bad Request`: Invalid file format or data
- `401 Unauthorized`: Missing/invalid token
- `403 Forbidden`: Insufficient permissions

---

## 2. SALARY MANAGEMENT

### 2.1 Get All Salary Records
```http
GET /salaries?page=1&pageSize=50&employeeId=1&status=ACTIVE&departmentId=1
Authorization: Bearer <token>

Response (200 OK):
{
  "data": [
    {
      "id": 1,
      "employee": {
        "id": 1,
        "name": "John Doe",
        "email": "john.doe@acme.com"
      },
      "baseSalary": 100000,
      "allowances": 15000,
      "deductions": 5000,
      "grossSalary": 115000,
      "tax": 23000,
      "netSalary": 87000,
      "currency": "USD",
      "effectiveDate": "2024-01-01",
      "status": "ACTIVE",
      "createdAt": "2024-01-01T10:00:00Z",
      "updatedAt": "2024-08-14T15:30:00Z"
    }
  ],
  "pagination": {
    "currentPage": 1,
    "pageSize": 50,
    "totalRecords": 10000,
    "totalPages": 200
  }
}
```

**Query Parameters**:
- `page` (optional, default: 1): Page number
- `pageSize` (optional, default: 50): Records per page
- `employeeId` (optional): Filter by employee
- `status` (optional): Filter by status (DRAFT, ACTIVE, ARCHIVED)
- `departmentId` (optional): Filter by department (via employee)
- `effectiveDateFrom` (optional): Filter by effective date range start
- `effectiveDateTo` (optional): Filter by effective date range end

---

### 2.2 Get Salary Record by ID
```http
GET /salaries/{id}
Authorization: Bearer <token>

Response (200 OK):
{
  "id": 1,
  "employee": {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@acme.com",
    "country": "US"
  },
  "baseSalary": 100000,
  "allowances": 15000,
  "deductions": 5000,
  "grossSalary": 115000,
  "tax": 23000,
  "netSalary": 87000,
  "currency": "USD",
  "effectiveDate": "2024-01-01",
  "status": "ACTIVE",
  "createdAt": "2024-01-01T10:00:00Z",
  "updatedAt": "2024-08-14T15:30:00Z"
}
```

---

### 2.3 Create Salary Record
```http
POST /salaries
Content-Type: application/json
Authorization: Bearer <token>

{
  "employeeId": 1,
  "baseSalary": 100000,
  "allowances": 15000,
  "deductions": 5000,
  "currency": "USD",
  "effectiveDate": "2024-09-01",
  "status": "DRAFT"
}

Response (201 Created):
{
  "id": 10001,
  "employee": {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@acme.com",
    "country": "US"
  },
  "baseSalary": 100000,
  "allowances": 15000,
  "deductions": 5000,
  "grossSalary": 115000,
  "tax": 23000,
  "netSalary": 87000,
  "currency": "USD",
  "effectiveDate": "2024-09-01",
  "status": "DRAFT",
  "createdAt": "2024-08-14T16:00:00Z",
  "updatedAt": "2024-08-14T16:00:00Z"
}
```

**Note**: System automatically calculates:
- `grossSalary` = baseSalary + allowances
- `tax` = calculated based on country and gross salary using tax brackets
- `netSalary` = grossSalary - deductions - tax

**Validation**:
- `employeeId`: Required, must exist
- `baseSalary`: Required, must be non-negative
- `allowances`: Required, must be non-negative
- `deductions`: Required, must be non-negative
- `currency`: Required, valid ISO 4217 code
- `effectiveDate`: Required, valid date
- `status`: Optional, default 'DRAFT' (DRAFT, ACTIVE, ARCHIVED)

**Response Codes**:
- `201 Created`: Salary record created
- `400 Bad Request`: Validation error
- `401 Unauthorized`: Missing/invalid token
- `404 Not Found`: Employee not found

---

### 2.4 Update Salary Record
```http
PUT /salaries/{id}
Content-Type: application/json
Authorization: Bearer <token>

{
  "baseSalary": 105000,
  "allowances": 16000,
  "deductions": 5500
}

Response (200 OK):
{
  "id": 10001,
  "employee": {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@acme.com",
    "country": "US"
  },
  "baseSalary": 105000,
  "allowances": 16000,
  "deductions": 5500,
  "grossSalary": 121000,
  "tax": 24200,
  "netSalary": 91300,
  "currency": "USD",
  "effectiveDate": "2024-09-01",
  "status": "DRAFT",
  "createdAt": "2024-08-14T16:00:00Z",
  "updatedAt": "2024-08-14T16:30:00Z"
}
```

**Note**: Update is only allowed for DRAFT status. ACTIVE records can only be archived and replaced.

---

### 2.5 Activate Salary Record (Change status from DRAFT to ACTIVE)
```http
PATCH /salaries/{id}/activate
Authorization: Bearer <token>

Response (200 OK):
{
  "id": 10001,
  "status": "ACTIVE",
  ...
}
```

---

### 2.6 Archive Salary Record
```http
PATCH /salaries/{id}/archive
Authorization: Bearer <token>

Response (200 OK):
{
  "id": 10001,
  "status": "ARCHIVED",
  ...
}
```

---

### 2.7 Get Salary History for an Employee
```http
GET /salaries/history/employee/{employeeId}
Authorization: Bearer <token>

Response (200 OK):
{
  "data": [
    {
      "id": 1,
      "salaryRecordId": 10001,
      "changeType": "UPDATE",
      "fieldName": "baseSalary",
      "oldValue": 100000,
      "newValue": 105000,
      "changedBy": "hr_manager@acme.com",
      "changedAt": "2024-08-14T16:30:00Z"
    }
  ]
}
```

---

### 2.8 Get Salary Slip (PDF/Document)
```http
GET /salaries/{id}/slip
Authorization: Bearer <token>

Response (200 OK):
Content-Type: application/pdf
(PDF document with salary breakdown)
```

---

### 2.9 Bulk Import Salary Records
```http
POST /salaries/bulk-import
Content-Type: multipart/form-data
Authorization: Bearer <token>

Form Data:
- file: CSV file

CSV Format:
employeeId,baseSalary,allowances,deductions,effectiveDate,status
1,100000,15000,5000,2024-09-01,DRAFT
2,95000,14000,4500,2024-09-01,DRAFT
```

---

## 3. ANALYTICS & REPORTING

### 3.1 Get Dashboard Summary
```http
GET /analytics/dashboard?departmentId=1&country=US&designationId=1
Authorization: Bearer <token>

Response (200 OK):
{
  "summary": {
    "totalEmployees": 10000,
    "totalPayroll": 1250000000,
    "averageSalary": 125000,
    "medianSalary": 118000
  },
  "byDepartment": [
    {
      "departmentId": 1,
      "departmentName": "Engineering",
      "employeeCount": 3000,
      "averageSalary": 135000,
      "minSalary": 80000,
      "maxSalary": 250000,
      "totalPayroll": 405000000
    }
  ],
  "byCurrency": [
    {
      "currency": "USD",
      "totalPayroll": 750000000,
      "employeeCount": 6000
    }
  ],
  "byDesignation": [
    {
      "designationId": 1,
      "designationTitle": "Software Engineer",
      "employeeCount": 1500,
      "averageSalary": 125000,
      "minSalary": 85000,
      "maxSalary": 180000
    }
  ]
}
```

**Query Parameters**:
- `departmentId` (optional): Filter by department
- `country` (optional): Filter by country
- `designationId` (optional): Filter by designation
- `effectiveDateFrom` (optional): Filter by date range
- `effectiveDateTo` (optional): Filter by date range

---

### 3.2 Get Salary Distribution Report
```http
GET /analytics/salary-distribution?departmentId=1&groupBy=range
Authorization: Bearer <token>

Response (200 OK):
{
  "ranges": [
    {
      "range": "0-50000",
      "count": 500,
      "percentage": 5.0
    },
    {
      "range": "50000-100000",
      "count": 2000,
      "percentage": 20.0
    }
  ]
}
```

---

### 3.3 Export Salary Report to CSV
```http
GET /analytics/export/salary-report?format=csv&departmentId=1
Authorization: Bearer <token>

Response (200 OK):
Content-Type: text/csv
(CSV file with salary data)
```

---

## 4. DEPARTMENTS & DESIGNATIONS

### 4.1 Get All Departments
```http
GET /departments
Authorization: Bearer <token>

Response (200 OK):
{
  "data": [
    {
      "id": 1,
      "name": "Engineering",
      "description": "Software development team",
      "createdAt": "2024-01-01T10:00:00Z",
      "updatedAt": "2024-01-01T10:00:00Z"
    }
  ]
}
```

---

### 4.2 Create Department
```http
POST /departments
Content-Type: application/json
Authorization: Bearer <token>

{
  "name": "Data Science",
  "description": "Analytics and ML team"
}

Response (201 Created):
{
  "id": 10,
  "name": "Data Science",
  "description": "Analytics and ML team",
  "createdAt": "2024-08-14T16:00:00Z",
  "updatedAt": "2024-08-14T16:00:00Z"
}
```

---

### 4.3 Get All Designations
```http
GET /designations
Authorization: Bearer <token>

Response (200 OK):
{
  "data": [
    {
      "id": 1,
      "title": "Software Engineer",
      "description": "Full-stack software development",
      "createdAt": "2024-01-01T10:00:00Z"
    }
  ]
}
```

---

### 4.4 Create Designation
```http
POST /designations
Content-Type: application/json
Authorization: Bearer <token>

{
  "title": "Senior Data Scientist",
  "description": "Lead ML model development"
}

Response (201 Created):
{
  "id": 15,
  "title": "Senior Data Scientist",
  "description": "Lead ML model development",
  "createdAt": "2024-08-14T16:00:00Z"
}
```

---

## Error Response Format

All error responses follow this format:

```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid request parameters",
    "details": [
      {
        "field": "email",
        "message": "Email already exists"
      }
    ],
    "timestamp": "2024-08-14T16:00:00Z",
    "requestId": "req_123456"
  }
}
```

**Common Error Codes**:
- `VALIDATION_ERROR`: Input validation failed
- `AUTHENTICATION_ERROR`: Authentication failed
- `AUTHORIZATION_ERROR`: Insufficient permissions
- `RESOURCE_NOT_FOUND`: Resource doesn't exist
- `DUPLICATE_RESOURCE`: Resource already exists
- `INTERNAL_SERVER_ERROR`: Server error

---

**Document Version**: 1.0  
**Last Updated**: 2026-08-14
