import { Injectable } from '@angular/core';
import {
  Department, Designation, Employee, SalaryRecord,
  TaxBracket, DashboardMetrics, DepartmentDistribution,
  PayEquity, CountryInfo, PageResponse, SalarySlip,
  TaxCalculationRequest, TaxCalculationResponse
} from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class MockDataService {

  public departments: Department[] = [
    { id: 1, name: 'Engineering', departmentCode: 'ENG', description: 'Software engineering, DevOps, and cloud architecture', budget: 15000000, location: 'Global', employeeCount: 3200 },
    { id: 2, name: 'Human Resources', departmentCode: 'HR', description: 'Talent acquisition, total rewards, and people operations', budget: 3000000, location: 'Global', employeeCount: 450 },
    { id: 3, name: 'Finance', departmentCode: 'FIN', description: 'Financial planning, accounting, payroll, and compliance', budget: 4500000, location: 'Global', employeeCount: 650 },
    { id: 4, name: 'Sales', departmentCode: 'SLS', description: 'Global enterprise and commercial customer acquisition', budget: 12000000, location: 'Global', employeeCount: 2100 },
    { id: 5, name: 'Marketing', departmentCode: 'MKT', description: 'Product marketing, brand development, and lead generation', budget: 6000000, location: 'Global', employeeCount: 850 },
    { id: 6, name: 'Product Management', departmentCode: 'PRD', description: 'Product strategy, roadmap delivery, and UX research', budget: 5000000, location: 'Global', employeeCount: 750 },
    { id: 7, name: 'Legal & Compliance', departmentCode: 'LGL', description: 'Corporate governance, contracts, and employment law', budget: 2500000, location: 'Global', employeeCount: 200 },
    { id: 8, name: 'Customer Success', departmentCode: 'CS', description: 'Client onboarding, technical support, and account renewals', budget: 4000000, location: 'Global', employeeCount: 1100 },
    { id: 9, name: 'Operations', departmentCode: 'OPS', description: 'Global infrastructure, IT services, and facilities', budget: 3500000, location: 'Global', employeeCount: 700 }
  ];

  public designations: Designation[] = [
    { id: 1, title: 'Associate Software Engineer', level: 'L1', minSalary: 45000, maxSalary: 75000, departmentName: 'Engineering', employeeCount: 600 },
    { id: 2, title: 'Software Engineer', level: 'L2', minSalary: 70000, maxSalary: 115000, departmentName: 'Engineering', employeeCount: 1200 },
    { id: 3, title: 'Senior Software Engineer', level: 'L3', minSalary: 110000, maxSalary: 165000, departmentName: 'Engineering', employeeCount: 950 },
    { id: 4, title: 'Staff Software Engineer', level: 'L4', minSalary: 150000, maxSalary: 220000, departmentName: 'Engineering', employeeCount: 350 },
    { id: 5, title: 'Engineering Manager', level: 'M1', minSalary: 140000, maxSalary: 210000, departmentName: 'Engineering', employeeCount: 100 },
    { id: 6, title: 'HR Specialist', level: 'L2', minSalary: 50000, maxSalary: 80000, departmentName: 'Human Resources', employeeCount: 300 },
    { id: 7, title: 'Senior HR Manager', level: 'M1', minSalary: 90000, maxSalary: 140000, departmentName: 'Human Resources', employeeCount: 150 },
    { id: 8, title: 'Financial Analyst', level: 'L2', minSalary: 60000, maxSalary: 95000, departmentName: 'Finance', employeeCount: 450 },
    { id: 9, title: 'Finance Director', level: 'D1', minSalary: 130000, maxSalary: 200000, departmentName: 'Finance', employeeCount: 200 },
    { id: 10, title: 'Account Executive', level: 'L2', minSalary: 65000, maxSalary: 130000, departmentName: 'Sales', employeeCount: 1600 },
    { id: 11, title: 'Sales Director', level: 'D1', minSalary: 150000, maxSalary: 250000, departmentName: 'Sales', employeeCount: 500 },
    { id: 12, title: 'Senior Product Manager', level: 'L3', minSalary: 110000, maxSalary: 170000, departmentName: 'Product Management', employeeCount: 750 },
    { id: 13, title: 'Customer Success Manager', level: 'L2', minSalary: 60000, maxSalary: 95000, departmentName: 'Customer Success', employeeCount: 1100 },
    { id: 14, title: 'VP of Global Engineering', level: 'VP', minSalary: 220000, maxSalary: 350000, departmentName: 'Engineering', employeeCount: 50 }
  ];

  public countries: CountryInfo[] = [
    { code: 'US', name: 'United States', currency: 'USD', symbol: '$' },
    { code: 'GB', name: 'United Kingdom', currency: 'GBP', symbol: '£' },
    { code: 'IN', name: 'India', currency: 'INR', symbol: '₹' },
    { code: 'DE', name: 'Germany', currency: 'EUR', symbol: '€' },
    { code: 'FR', name: 'France', currency: 'EUR', symbol: '€' },
    { code: 'CA', name: 'Canada', currency: 'CAD', symbol: '$' },
    { code: 'AU', name: 'Australia', currency: 'AUD', symbol: '$' },
    { code: 'JP', name: 'Japan', currency: 'JPY', symbol: '¥' },
    { code: 'SG', name: 'Singapore', currency: 'SGD', symbol: '$' }
  ];

  public taxBrackets: TaxBracket[] = [
    { id: 1, country: 'United States', taxYear: 2024, incomeFrom: 0, incomeTo: 11600, taxRate: 10.0, currency: 'USD', description: 'Bracket 1 (10%)' },
    { id: 2, country: 'United States', taxYear: 2024, incomeFrom: 11600, incomeTo: 47150, taxRate: 12.0, currency: 'USD', description: 'Bracket 2 (12%)' },
    { id: 3, country: 'United States', taxYear: 2024, incomeFrom: 47150, incomeTo: 100525, taxRate: 22.0, currency: 'USD', description: 'Bracket 3 (22%)' },
    { id: 4, country: 'United States', taxYear: 2024, incomeFrom: 100525, incomeTo: 191950, taxRate: 24.0, currency: 'USD', description: 'Bracket 4 (24%)' },
    { id: 5, country: 'United States', taxYear: 2024, incomeFrom: 191950, incomeTo: 999999999, taxRate: 32.0, currency: 'USD', description: 'Bracket 5 (32%)' },
    { id: 6, country: 'United Kingdom', taxYear: 2024, incomeFrom: 0, incomeTo: 12570, taxRate: 0.0, currency: 'GBP', description: 'Personal Allowance (0%)' },
    { id: 7, country: 'United Kingdom', taxYear: 2024, incomeFrom: 12570, incomeTo: 50270, taxRate: 20.0, currency: 'GBP', description: 'Basic Rate (20%)' },
    { id: 8, country: 'United Kingdom', taxYear: 2024, incomeFrom: 50270, incomeTo: 125140, taxRate: 40.0, currency: 'GBP', description: 'Higher Rate (40%)' },
    { id: 9, country: 'United Kingdom', taxYear: 2024, incomeFrom: 125140, incomeTo: 999999999, taxRate: 45.0, currency: 'GBP', description: 'Additional Rate (45%)' },
    { id: 10, country: 'India', taxYear: 2024, incomeFrom: 0, incomeTo: 300000, taxRate: 0.0, currency: 'INR', description: 'Exempt Bracket (0%)' },
    { id: 11, country: 'India', taxYear: 2024, incomeFrom: 300000, incomeTo: 600000, taxRate: 5.0, currency: 'INR', description: 'Slab 1 (5%)' },
    { id: 12, country: 'India', taxYear: 2024, incomeFrom: 600000, incomeTo: 900000, taxRate: 10.0, currency: 'INR', description: 'Slab 2 (10%)' },
    { id: 13, country: 'India', taxYear: 2024, incomeFrom: 900000, incomeTo: 1200000, taxRate: 15.0, currency: 'INR', description: 'Slab 3 (15%)' },
    { id: 14, country: 'India', taxYear: 2024, incomeFrom: 1200000, incomeTo: 1500000, taxRate: 20.0, currency: 'INR', description: 'Slab 4 (20%)' },
    { id: 15, country: 'India', taxYear: 2024, incomeFrom: 1500000, incomeTo: 999999999, taxRate: 30.0, currency: 'INR', description: 'Slab 5 (30%)' }
  ];

  public employees: Employee[] = [];
  public salaries: SalaryRecord[] = [];

  constructor() {
    this.generateMockDataset();
  }

  private generateMockDataset() {
    const firstNames = ['Alexander', 'Sophia', 'Liam', 'Olivia', 'Noah', 'Emma', 'Ethan', 'Ava', 'Mason', 'Isabella', 'William', 'Mia', 'James', 'Harper', 'Benjamin', 'Evelyn', 'Aarav', 'Priya', 'Rahul', 'Ananya', 'Rohan', 'Sneha', 'Wei', 'Yuki', 'Kenji', 'Sakura', 'Lukas', 'Hannah', 'Camille', 'Antoine'];
    const lastNames = ['Smith', 'Johnson', 'Williams', 'Brown', 'Jones', 'Garcia', 'Miller', 'Davis', 'Rodriguez', 'Martinez', 'Sharma', 'Patel', 'Verma', 'Reddy', 'Tanaka', 'Sato', 'Suzuki', 'Mueller', 'Schmidt', 'Dubois', 'Lambert', 'Wilson', 'Anderson', 'Taylor', 'Moore'];

    for (let i = 1; i <= 1000; i++) {
      const fn = firstNames[i % firstNames.length];
      const ln = lastNames[(i * 3) % lastNames.length];
      const countryObj = this.countries[i % this.countries.length];
      const dept = this.departments[i % this.departments.length];
      const desig = this.designations[i % this.designations.length];

      const emp: Employee = {
        id: i,
        employeeId: `EMP-${String(i).padStart(5, '0')}`,
        firstName: fn,
        lastName: ln,
        email: `${fn.toLowerCase()}.${ln.toLowerCase()}.${i}@acme.com`,
        phone: `+1-555-${String(1000 + (i % 9000))}`,
        departmentId: dept.id,
        departmentName: dept.name,
        designationId: desig.id,
        designationName: desig.title,
        hireDate: `202${(i % 5) + 1}-0${(i % 9) + 1}-15`,
        employmentType: 'FULL_TIME',
        country: countryObj.name,
        currency: countryObj.currency,
        status: i % 25 === 0 ? 'ON_LEAVE' : 'ACTIVE',
        city: 'Global Center',
        createdAt: '2024-01-01'
      };
      this.employees.push(emp);

      const base = (desig.minSalary || 60000) + ((i * 1234) % ((desig.maxSalary || 120000) - (desig.minSalary || 60000)));
      const allowances = Math.round(base * 0.12);
      const deductions = Math.round(base * 0.04);
      const gross = base + allowances;
      const taxRate = countryObj.name === 'United States' ? 0.22 : countryObj.name === 'United Kingdom' ? 0.20 : countryObj.name === 'India' ? 0.15 : 0.24;
      const tax = Math.round(gross * taxRate);
      const net = gross - deductions - tax;

      const sal: SalaryRecord = {
        id: i,
        employeeId: i,
        employeeCode: emp.employeeId,
        employeeName: `${fn} ${ln}`,
        baseSalary: base,
        allowances,
        deductions,
        grossSalary: gross,
        tax,
        netSalary: net,
        effectiveDate: emp.hireDate,
        status: i % 40 === 0 ? 'INACTIVE' : 'ACTIVE',
        payFrequency: 'MONTHLY',
        currency: emp.currency,
        country: emp.country,
        approvedBy: 1,
        approvedAt: '2024-01-01T00:00:00'
      };
      this.salaries.push(sal);
    }
  }

  getDashboardMetrics(): DashboardMetrics {
    const total = 10000;
    const active = 9750;
    const inactive = 250;

    return {
      totalEmployees: total,
      activeEmployees: active,
      inactiveEmployees: inactive,
      departmentsCount: this.departments.length,
      designationsCount: this.designations.length,
      pendingSalaryApprovals: 14,
      totalMonthlyPayrollByCurrency: {
        USD: 14850000,
        GBP: 3620000,
        EUR: 4910000,
        INR: 78500000,
        CAD: 2150000,
        AUD: 1980000,
        JPY: 145000000,
        SGD: 1250000
      },
      totalAnnualPayrollByCurrency: {
        USD: 178200000,
        GBP: 43440000,
        EUR: 58920000,
        INR: 942000000
      },
      totalTaxCollectedByCurrency: {
        USD: 3267000,
        GBP: 724000,
        EUR: 1178400,
        INR: 11775000
      },
      headcountByDepartment: this.departments.map(d => ({
        departmentId: d.id,
        departmentName: d.name,
        count: d.employeeCount || 1000,
        percentage: Math.round(((d.employeeCount || 1000) / total) * 1000) / 10
      })),
      headcountByCountry: this.countries.map((c, idx) => ({
        country: c.name,
        currency: c.currency,
        count: idx === 0 ? 3200 : idx === 1 ? 1800 : idx === 2 ? 2200 : idx === 3 ? 900 : 400,
        percentage: idx === 0 ? 32.0 : idx === 1 ? 18.0 : idx === 2 ? 22.0 : idx === 3 ? 9.0 : 4.0
      })),
      headcountByStatus: [
        { status: 'ACTIVE', count: 9750 },
        { status: 'ON_LEAVE', count: 250 }
      ]
    };
  }

  getDepartmentDistributions(): DepartmentDistribution[] {
    return this.departments.map((d, i) => {
      const avg = 85000 + (i * 7500);
      return {
        departmentId: d.id,
        departmentName: d.name,
        currency: 'USD',
        employeeCount: d.employeeCount || 1000,
        minSalary: Math.round(avg * 0.55),
        maxSalary: Math.round(avg * 1.85),
        averageSalary: avg,
        medianSalary: Math.round(avg * 0.98),
        p25Salary: Math.round(avg * 0.78),
        p75Salary: Math.round(avg * 1.25),
        totalGrossSalary: Math.round(avg * (d.employeeCount || 1000)),
        totalTax: Math.round(avg * (d.employeeCount || 1000) * 0.22),
        totalNetSalary: Math.round(avg * (d.employeeCount || 1000) * 0.74)
      };
    });
  }

  getPayEquityAnalysis(): PayEquity[] {
    return this.designations.map(d => {
      const min = d.minSalary || 60000;
      const max = d.maxSalary || 120000;
      const avg = Math.round((min + max) / 2);
      return {
        designationId: d.id,
        designationTitle: d.title,
        currency: 'USD',
        employeeCount: d.employeeCount || 250,
        minSalary: min,
        maxSalary: max,
        averageSalary: avg,
        medianSalary: Math.round(avg * 0.96),
        salarySpreadRatio: Math.round((max / min) * 100) / 100
      };
    });
  }

  calculateTaxPreview(req: TaxCalculationRequest): TaxCalculationResponse {
    const base = req.baseSalary || 0;
    const allow = req.allowances || 0;
    const ded = req.deductions || 0;
    const gross = base + allow;

    const brackets = this.taxBrackets.filter(b => b.country === req.country || b.country === 'United States');
    const breakdown: any[] = [];
    let totalTax = 0;

    for (const b of brackets) {
      if (gross > b.incomeFrom) {
        const taxable = b.incomeTo && gross > b.incomeTo ? (b.incomeTo - b.incomeFrom) : (gross - b.incomeFrom);
        const bTax = Math.round((taxable * b.taxRate) / 100);
        totalTax += bTax;
        breakdown.push({
          bracketFrom: b.incomeFrom,
          bracketTo: b.incomeTo,
          rate: b.taxRate,
          taxableAmountInBracket: taxable,
          taxForBracket: bTax
        });
      }
    }

    const net = gross - ded - totalTax;
    const effectiveRate = gross > 0 ? Math.round((totalTax / gross) * 10000) / 100 : 0;

    return {
      baseSalary: base,
      allowances: allow,
      deductions: ded,
      grossSalary: gross,
      totalTax,
      netSalary: net,
      effectiveTaxRate: effectiveRate,
      country: req.country,
      taxYear: req.taxYear || 2024,
      currency: req.currency || 'USD',
      breakdown
    };
  }

  generateSalarySlip(salId: number): SalarySlip {
    const sal = this.salaries.find(s => s.id === salId) || this.salaries[0];
    const emp = this.employees.find(e => e.id === sal.employeeId) || this.employees[0];
    const taxPreview = this.calculateTaxPreview({ baseSalary: sal.baseSalary, allowances: sal.allowances, deductions: sal.deductions, country: sal.country || 'United States' });

    return {
      slipNumber: `PS-${sal.id}-202408`,
      salaryRecordId: sal.id,
      payPeriod: 'August 2024',
      generatedDate: '2024-08-20',
      employeeId: emp.id,
      employeeCode: emp.employeeId,
      employeeName: `${emp.firstName} ${emp.lastName}`,
      email: emp.email,
      departmentName: emp.departmentName || 'Engineering',
      designationTitle: emp.designationName || 'Senior Software Engineer',
      country: emp.country,
      currency: sal.currency,
      taxId: emp.taxId || 'TX-94829104',
      bankAccount: emp.bankAccount || 'ACCT-948201849',
      bankCode: 'ACME-CORP-BANK',
      baseSalary: sal.baseSalary,
      allowances: sal.allowances,
      grossSalary: sal.grossSalary,
      standardDeductions: sal.deductions,
      incomeTax: sal.tax,
      totalDeductions: sal.deductions + sal.tax,
      netSalary: sal.netSalary,
      effectiveTaxRate: taxPreview.effectiveTaxRate,
      taxBreakdown: taxPreview.breakdown,
      companyName: 'ACME Global Corporation',
      companyAddress: '100 Innovation Parkway, Tech City'
    };
  }
}
