// ============================================================================
// Core TypeScript Interfaces & Models for Salary Management System
// ============================================================================

export interface ApiResponse<T> {
  status: number;
  message: string;
  data: T;
  timestamp: string;
}

export interface PageResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  hasNext: boolean;
  hasPrevious: boolean;
}

export interface User {
  id?: number;
  username: string;
  email?: string;
  role: string;
}

export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  refreshToken?: string;
  user?: User;
}

export interface Employee {
  id: number;
  employeeId: string;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  dateOfBirth?: string;
  gender?: string;
  departmentId: number;
  departmentName?: string;
  designationId: number;
  designationName?: string;
  hireDate: string;
  employmentType?: string;
  country: string;
  currency: string;
  taxId?: string;
  bankAccount?: string;
  bankCode?: string;
  status: string;
  address?: string;
  city?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface SalaryRecord {
  id: number;
  employeeId: number;
  employeeCode?: string;
  employeeName?: string;
  baseSalary: number;
  allowances: number;
  deductions: number;
  grossSalary: number;
  tax: number;
  netSalary: number;
  effectiveDate: string;
  endDate?: string;
  status: 'ACTIVE' | 'INACTIVE' | 'DRAFT' | 'ARCHIVED';
  payFrequency: string;
  currency: string;
  country?: string;
  approvedBy?: number;
  approvedAt?: string;
  comments?: string;
  createdAt?: string;
}

export interface SalaryHistory {
  id: number;
  employeeId: number;
  salaryRecordId: number;
  baseSalary: number;
  allowances: number;
  deductions: number;
  grossSalary: number;
  tax: number;
  netSalary: number;
  effectiveDate: string;
  changeType: string;
  changeReason?: string;
  currency: string;
  createdAt?: string;
}

export interface TaxCalculationRequest {
  baseSalary: number;
  allowances?: number;
  deductions?: number;
  country: string;
  taxYear?: number;
  currency?: string;
  regime?: 'NEW' | 'OLD';
}

export interface TaxBracketBreakdown {
  bracketFrom: number;
  bracketTo: number;
  rate: number;
  taxableAmountInBracket: number;
  taxForBracket: number;
}

export interface RegimeComparison {
  newRegimeTax: number;
  oldRegimeTax: number;
  difference: number;
  recommendation: 'NEW' | 'OLD';
  savingsMessage: string;
}

export interface TaxCalculationResponse {
  baseSalary: number;
  allowances: number;
  deductions: number;
  grossSalary: number;
  totalTax: number;
  netSalary: number;
  effectiveTaxRate: number;
  country: string;
  taxYear: number;
  currency: string;
  regime?: 'NEW' | 'OLD';
  comparison?: RegimeComparison;
  breakdown: TaxBracketBreakdown[];
}

export interface SalarySlip {
  slipNumber: string;
  salaryRecordId: number;
  payPeriod: string;
  generatedDate: string;
  employeeId: number;
  employeeCode: string;
  employeeName: string;
  email: string;
  departmentName: string;
  designationTitle: string;
  country: string;
  currency: string;
  taxId: string;
  bankAccount: string;
  bankCode: string;
  baseSalary: number;
  allowances: number;
  grossSalary: number;
  standardDeductions: number;
  incomeTax: number;
  totalDeductions: number;
  netSalary: number;
  effectiveTaxRate: number;
  taxBreakdown: TaxBracketBreakdown[];
  companyName: string;
  companyAddress: string;
}

export interface HeadcountByDepartment {
  departmentId: number;
  departmentName: string;
  count: number;
  percentage: number;
}

export interface HeadcountByCountry {
  country: string;
  currency: string;
  count: number;
  percentage: number;
}

export interface DashboardMetrics {
  totalEmployees: number;
  activeEmployees: number;
  inactiveEmployees: number;
  departmentsCount: number;
  designationsCount: number;
  pendingSalaryApprovals: number;
  totalMonthlyPayrollByCurrency: { [key: string]: number };
  totalAnnualPayrollByCurrency: { [key: string]: number };
  totalTaxCollectedByCurrency: { [key: string]: number };
  headcountByDepartment: HeadcountByDepartment[];
  headcountByCountry: HeadcountByCountry[];
  headcountByStatus: { status: string; count: number }[];
}

export interface DepartmentDistribution {
  departmentId: number;
  departmentName: string;
  currency: string;
  employeeCount: number;
  minSalary: number;
  maxSalary: number;
  averageSalary: number;
  medianSalary: number;
  p25Salary: number;
  p75Salary: number;
  totalGrossSalary: number;
  totalTax: number;
  totalNetSalary: number;
}

export interface PayEquity {
  designationId: number;
  designationTitle: string;
  currency: string;
  employeeCount: number;
  minSalary: number;
  maxSalary: number;
  averageSalary: number;
  medianSalary: number;
  salarySpreadRatio: number;
}

export interface Department {
  id: number;
  name: string;
  description?: string;
  departmentCode: string;
  budget?: number;
  location?: string;
  employeeCount?: number;
}

export interface Designation {
  id: number;
  title: string;
  description?: string;
  level: string;
  minSalary?: number;
  maxSalary?: number;
  departmentName?: string;
  employeeCount?: number;
}

export interface TaxBracket {
  id: number;
  country: string;
  taxYear: number;
  incomeFrom: number;
  incomeTo: number;
  taxRate: number;
  currency: string;
  description?: string;
}

export interface CountryInfo {
  code: string;
  name: string;
  currency: string;
  symbol: string;
}
