import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import {
  ApiResponse, PageResponse, SalaryRecord,
  SalaryHistory, SalarySlip, TaxCalculationRequest,
  TaxCalculationResponse
} from '../models/models';
import { MockDataService } from './mock-data.service';

@Injectable({
  providedIn: 'root'
})
export class SalaryService {
  private apiUrl = 'http://localhost:8080/api/v1/salaries';

  constructor(
    private http: HttpClient,
    private mockData: MockDataService
  ) {}

  getSalaries(page: number = 0, size: number = 10, employeeId?: number, status?: string): Observable<PageResponse<SalaryRecord>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (employeeId) params = params.set('employeeId', employeeId.toString());
    if (status) params = params.set('status', status);

    return this.http.get<ApiResponse<PageResponse<SalaryRecord>>>(this.apiUrl, { params }).pipe(
      map(res => res.data),
      catchError(() => {
        let list = [...this.mockData.salaries];
        if (employeeId) {
          list = list.filter(s => s.employeeId === Number(employeeId));
        }
        if (status) {
          list = list.filter(s => s.status.toLowerCase() === status.toLowerCase());
        }

        const total = list.length;
        const totalPages = Math.ceil(total / size);
        const start = page * size;
        const pageContent = list.slice(start, start + size);

        return of({
          content: pageContent,
          pageNumber: page,
          pageSize: size,
          totalElements: total,
          totalPages: totalPages,
          hasNext: page < totalPages - 1,
          hasPrevious: page > 0
        });
      })
    );
  }

  getSalaryById(id: number): Observable<SalaryRecord> {
    return this.http.get<ApiResponse<SalaryRecord>>(`${this.apiUrl}/${id}`).pipe(
      map(res => res.data),
      catchError(() => {
        const record = this.mockData.salaries.find(s => s.id === id) || this.mockData.salaries[0];
        return of(record);
      })
    );
  }

  createSalary(record: Partial<SalaryRecord>): Observable<SalaryRecord> {
    return this.http.post<ApiResponse<SalaryRecord>>(this.apiUrl, record).pipe(
      map(res => res.data),
      catchError(() => {
        const emp = this.mockData.employees.find(e => e.id === record.employeeId) || this.mockData.employees[0];
        const base = record.baseSalary || 80000;
        const allow = record.allowances || 0;
        const ded = record.deductions || 0;
        const gross = base + allow;
        const tax = Math.round(gross * 0.20);
        const net = gross - ded - tax;

        const newRec: SalaryRecord = {
          id: this.mockData.salaries.length + 1,
          employeeId: emp.id,
          employeeCode: emp.employeeId,
          employeeName: `${emp.firstName} ${emp.lastName}`,
          baseSalary: base,
          allowances: allow,
          deductions: ded,
          grossSalary: gross,
          tax,
          netSalary: net,
          effectiveDate: record.effectiveDate || new Date().toISOString().split('T')[0],
          status: record.status || 'ACTIVE',
          payFrequency: record.payFrequency || 'MONTHLY',
          currency: record.currency || emp.currency,
          country: emp.country
        };
        this.mockData.salaries.unshift(newRec);
        return of(newRec);
      })
    );
  }

  updateSalary(id: number, record: Partial<SalaryRecord>): Observable<SalaryRecord> {
    return this.http.put<ApiResponse<SalaryRecord>>(`${this.apiUrl}/${id}`, record).pipe(
      map(res => res.data),
      catchError(() => {
        const idx = this.mockData.salaries.findIndex(s => s.id === id);
        if (idx !== -1) {
          this.mockData.salaries[idx] = { ...this.mockData.salaries[idx], ...record };
          return of(this.mockData.salaries[idx]);
        }
        return of(this.mockData.salaries[0]);
      })
    );
  }

  deleteSalary(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`).pipe(
      map(() => {}),
      catchError(() => {
        const idx = this.mockData.salaries.findIndex(s => s.id === id);
        if (idx !== -1) {
          this.mockData.salaries[idx].status = 'ARCHIVED';
        }
        return of(undefined);
      })
    );
  }

  approveSalary(id: number): Observable<SalaryRecord> {
    return this.http.put<ApiResponse<SalaryRecord>>(`${this.apiUrl}/${id}/approve`, {}).pipe(
      map(res => res.data),
      catchError(() => {
        const idx = this.mockData.salaries.findIndex(s => s.id === id);
        if (idx !== -1) {
          this.mockData.salaries[idx].status = 'ACTIVE';
          this.mockData.salaries[idx].approvedAt = new Date().toISOString();
          return of(this.mockData.salaries[idx]);
        }
        return of(this.mockData.salaries[0]);
      })
    );
  }

  calculateTaxPreview(request: TaxCalculationRequest): Observable<TaxCalculationResponse> {
    return this.http.post<ApiResponse<TaxCalculationResponse>>(`${this.apiUrl}/calculate-tax`, request).pipe(
      map(res => res.data),
      catchError(() => {
        return of(this.mockData.calculateTaxPreview(request));
      })
    );
  }

  generateSalarySlip(salaryRecordId: number): Observable<SalarySlip> {
    return this.http.get<ApiResponse<SalarySlip>>(`${this.apiUrl}/${salaryRecordId}/slip`).pipe(
      map(res => res.data),
      catchError(() => {
        return of(this.mockData.generateSalarySlip(salaryRecordId));
      })
    );
  }

  getSalaryHistory(employeeId: number): Observable<SalaryHistory[]> {
    return this.http.get<ApiResponse<SalaryHistory[]>>(`${this.apiUrl}/employee/${employeeId}/history`).pipe(
      map(res => res.data),
      catchError(() => {
        return of([
          {
            id: 1,
            employeeId,
            salaryRecordId: 1,
            baseSalary: 75000,
            allowances: 8000,
            deductions: 3000,
            grossSalary: 83000,
            tax: 16600,
            netSalary: 63400,
            effectiveDate: '2023-01-15',
            changeType: 'INITIAL',
            changeReason: 'Starting Offer',
            currency: 'USD',
            createdAt: '2023-01-15T09:00:00'
          },
          {
            id: 2,
            employeeId,
            salaryRecordId: 2,
            baseSalary: 92000,
            allowances: 10000,
            deductions: 3500,
            grossSalary: 102000,
            tax: 20400,
            netSalary: 78100,
            effectiveDate: '2024-01-15',
            changeType: 'PROMOTION',
            changeReason: 'Annual Merit & Promotion to Senior level',
            currency: 'USD',
            createdAt: '2024-01-15T09:00:00'
          }
        ]);
      })
    );
  }

  exportSalariesCsv(): Observable<string> {
    return this.http.get(`${this.apiUrl}/export`, { responseType: 'text' }).pipe(
      catchError(() => {
        const header = "Salary ID,Employee ID,Employee Name,Base Salary,Gross Salary,Tax,Net Salary,Currency,Status\n";
        const rows = this.mockData.salaries.slice(0, 100).map(s =>
          `"${s.id}","${s.employeeCode}","${s.employeeName}",${s.baseSalary},${s.grossSalary},${s.tax},${s.netSalary},"${s.currency}","${s.status}"`
        ).join("\n");
        return of(header + rows);
      })
    );
  }
}
