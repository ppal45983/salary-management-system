/**
 * Automated Live API & CRUD Verification Test Suite
 * Tests all endpoints on the live AWS EC2 backend.
 */
const BASE_URL = process.env.API_URL || 'http://localhost:8080/api/v1';

let authToken = null;
let createdEmployeeId = null;
const results = [];

async function test(name, fn) {
  const start = Date.now();
  try {
    await fn();
    const duration = Date.now() - start;
    results.push({ name, status: 'PASS', duration: `${duration}ms` });
    console.log(` \x1b[32m✔ PASS\x1b[0m [${duration}ms] ${name}`);
  } catch (err) {
    const duration = Date.now() - start;
    results.push({ name, status: 'FAIL', duration: `${duration}ms`, error: err.message });
    console.log(` \x1b[31m✖ FAIL\x1b[0m [${duration}ms] ${name} - Error: ${err.message}`);
  }
}

async function request(path, options = {}) {
  const url = `${BASE_URL}${path}`;
  const headers = {
    'Content-Type': 'application/json',
    ...(authToken ? { 'Authorization': `Bearer ${authToken}` } : {}),
    ...(options.headers || {})
  };

  const response = await fetch(url, {
    ...options,
    headers
  });

  const contentType = response.headers.get('content-type') || '';
  let body = null;
  if (contentType.includes('application/json')) {
    body = await response.json();
  } else {
    body = await response.text();
  }

  if (!response.ok) {
    throw new Error(`HTTP ${response.status}: ${JSON.stringify(body)}`);
  }

  return { status: response.status, data: body };
}

async function runAllTests() {
  console.log('\n============================================================');
  console.log(` 🚀 STARTING FULL-STACK API & CRUD VERIFICATION SUITE`);
  console.log(` Target Backend: ${BASE_URL}`);
  console.log('============================================================\n');

  // --- SECTION 1: PUBLIC HEALTH & DOCS ---
  console.log('📌 [1/4] Public Health & Documentation Endpoints:');
  
  await test('GET /actuator/health - Database & Service Health Check', async () => {
    const res = await request('/actuator/health');
    if (res.data.status !== 'UP') throw new Error(`Health status: ${res.data.status}`);
  });

  await test('GET /auth/health - Authentication Service Health', async () => {
    const res = await request('/auth/health');
    if (!res.data.success) throw new Error('Auth service health check failed');
  });

  await test('GET /v3/api-docs - OpenAPI 3.0 Specifications JSON', async () => {
    const res = await request('/v3/api-docs');
    if (!res.data.openapi && !res.data.info) throw new Error('Invalid OpenAPI spec');
  });

  // --- SECTION 2: AUTHENTICATION ---
  console.log('\n📌 [2/4] Security & JWT Authentication:');

  await test('POST /auth/login - HR Manager Login & JWT Generation', async () => {
    const res = await request('/auth/login', {
      method: 'POST',
      body: JSON.stringify({
        usernameOrEmail: 'hr_manager',
        password: 'admin123'
      })
    });
    authToken = res.data.data.accessToken;
    if (!authToken) throw new Error('No access token returned');
  });

  // --- SECTION 3: EMPLOYEE & SALARY CRUD ---
  console.log('\n📌 [3/4] Employee & Salary CRUD Operations:');

  await test('POST /employees - [CRUD: CREATE] Add New Employee', async () => {
    const uniqueEmail = `test.employee.${Date.now()}@acme.com`;
    const res = await request('/employees', {
      method: 'POST',
      body: JSON.stringify({
        firstName: 'Pramod',
        lastName: 'Pal',
        email: uniqueEmail,
        phone: '+918299494481',
        departmentId: 1,
        designationId: 1,
        hireDate: '2026-08-26',
        country: 'India',
        currency: 'INR',
        status: 'ACTIVE'
      })
    });
    createdEmployeeId = res.data.data.id;
    if (!createdEmployeeId) throw new Error('Failed to obtain new employee ID');
  });

  await test('GET /employees - [CRUD: READ] Paginated Employee Directory (10k scale)', async () => {
    const res = await request('/employees?page=0&size=10');
    if (!res.data.data.content || res.data.data.content.length === 0) {
      throw new Error('No employees returned in directory');
    }
  });

  await test('GET /employees/{id} - [CRUD: READ] Single Employee Profile', async () => {
    if (!createdEmployeeId) throw new Error('No employee ID available');
    const res = await request(`/employees/${createdEmployeeId}`);
    if (res.data.data.firstName !== 'Pramod') throw new Error('Employee name mismatch');
  });

  await test('PUT /employees/{id} - [CRUD: UPDATE] Update Employee Info', async () => {
    if (!createdEmployeeId) throw new Error('No employee ID available');
    const res = await request(`/employees/${createdEmployeeId}`, {
      method: 'PUT',
      body: JSON.stringify({
        firstName: 'Pramod',
        lastName: 'Pal Senior',
        phone: '+919999999999',
        departmentId: 1,
        designationId: 2,
        status: 'ACTIVE'
      })
    });
    if (res.data.data.lastName !== 'Pal Senior') throw new Error('Failed to update employee last name');
  });

  await test('POST /salaries - [CRUD: CREATE] Create Salary with Progressive Taxes', async () => {
    if (!createdEmployeeId) throw new Error('No employee ID available');
    const res = await request('/salaries', {
      method: 'POST',
      body: JSON.stringify({
        employeeId: createdEmployeeId,
        baseSalary: 120000.00,
        allowances: 15000.00,
        deductions: 5000.00,
        currency: 'INR',
        effectiveDate: '2026-08-26',
        status: 'ACTIVE',
        paymentMethod: 'BANK_TRANSFER'
      })
    });
    if (!res.data.data.netSalary) throw new Error('Failed to calculate net salary');
  });

  await test('GET /salaries/employee/{id} - [CRUD: READ] Fetch Employee Active Salary', async () => {
    if (!createdEmployeeId) throw new Error('No employee ID available');
    const res = await request(`/salaries/employee/${createdEmployeeId}`);
    if (!res.data.data.grossSalary) throw new Error('Failed to retrieve salary breakdown');
  });

  await test('DELETE /employees/{id} - [CRUD: DELETE] Deactivate Employee Record', async () => {
    if (!createdEmployeeId) throw new Error('No employee ID available');
    const res = await request(`/employees/${createdEmployeeId}`, {
      method: 'DELETE'
    });
    if (!res.data.success) throw new Error('Failed to deactivate employee');
  });

  // --- SECTION 4: ANALYTICS & MASTER DATA ---
  console.log('\n📌 [4/4] Analytics, Master Data & Reports:');

  await test('GET /analytics/dashboard - Executive KPIs & Headcounts', async () => {
    const res = await request('/analytics/dashboard');
    if (res.data.data.totalEmployees === undefined) throw new Error('Dashboard metrics missing totalEmployees');
  });

  await test('GET /analytics/distribution - Salary Distribution by Department', async () => {
    const res = await request('/analytics/distribution');
    if (!Array.isArray(res.data.data)) throw new Error('Distribution must be an array');
  });

  await test('GET /analytics/pay-equity - Pay Equity Spread by Designation', async () => {
    const res = await request('/analytics/pay-equity');
    if (!Array.isArray(res.data.data)) throw new Error('Pay equity must be an array');
  });

  await test('GET /masters/departments - Master Departments Lookup', async () => {
    const res = await request('/masters/departments');
    if (res.data.data.length === 0) throw new Error('No departments found');
  });

  await test('GET /masters/designations - Master Designations Lookup', async () => {
    const res = await request('/masters/designations');
    if (res.data.data.length === 0) throw new Error('No designations found');
  });

  // Summary
  console.log('\n============================================================');
  console.log(' 📊 TEST EXECUTION SUMMARY');
  console.log('============================================================');
  const passed = results.filter(r => r.status === 'PASS').length;
  const failed = results.filter(r => r.status === 'FAIL').length;
  console.log(` Total Test Cases Executed : ${results.length}`);
  console.log(` \x1b[32mPassed\x1b[0m                     : ${passed}`);
  console.log(` \x1b[31mFailed\x1b[0m                     : ${failed}`);
  console.log(` Success Rate              : ${((passed / results.length) * 100).toFixed(1)}%`);
  console.log('============================================================\n');
}

runAllTests().catch(console.error);
