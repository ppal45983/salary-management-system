import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { ApiResponse, Employee, PageResponse } from '../models/models';
import { MockDataService } from './mock-data.service';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {
  private apiUrl = 'http://localhost:8080/api/v1/employees';

  constructor(
    private http: HttpClient,
    private mockData: MockDataService
  ) {}

  getEmployees(page: number = 0, size: number = 10, search?: string, departmentId?: number, country?: string, status?: string): Observable<PageResponse<Employee>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (search) params = params.set('search', search);
    if (departmentId) params = params.set('department', departmentId.toString());
    if (country) params = params.set('country', country);
    if (status) params = params.set('status', status);

    return this.http.get<ApiResponse<PageResponse<Employee>>>(this.apiUrl, { params }).pipe(
      map(res => res.data),
      catchError(() => {
        // Fallback to mock data
        let filtered = [...this.mockData.employees];
        if (search) {
          const q = search.toLowerCase();
          filtered = filtered.filter(e =>
            e.firstName.toLowerCase().includes(q) ||
            e.lastName.toLowerCase().includes(q) ||
            e.email.toLowerCase().includes(q) ||
            e.employeeId.toLowerCase().includes(q)
          );
        }
        if (departmentId) {
          filtered = filtered.filter(e => e.departmentId === Number(departmentId));
        }
        if (country) {
          filtered = filtered.filter(e => e.country.toLowerCase() === country.toLowerCase());
        }
        if (status) {
          filtered = filtered.filter(e => e.status.toLowerCase() === status.toLowerCase());
        }

        const total = filtered.length;
        const totalPages = Math.ceil(total / size);
        const start = page * size;
        const pageContent = filtered.slice(start, start + size);

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

  getEmployeeById(id: number): Observable<Employee> {
    return this.http.get<ApiResponse<Employee>>(`${this.apiUrl}/${id}`).pipe(
      map(res => res.data),
      catchError(() => {
        const emp = this.mockData.employees.find(e => e.id === id) || this.mockData.employees[0];
        return of(emp);
      })
    );
  }

  createEmployee(employee: Partial<Employee>): Observable<Employee> {
    return this.http.post<ApiResponse<Employee>>(this.apiUrl, employee).pipe(
      map(res => res.data),
      catchError(() => {
        const newEmp: Employee = {
          id: this.mockData.employees.length + 1,
          employeeId: employee.employeeId || `EMP-${String(this.mockData.employees.length + 1).padStart(5, '0')}`,
          firstName: employee.firstName || '',
          lastName: employee.lastName || '',
          email: employee.email || '',
          departmentId: employee.departmentId || 1,
          departmentName: this.mockData.departments.find(d => d.id === employee.departmentId)?.name || 'Engineering',
          designationId: employee.designationId || 1,
          designationName: this.mockData.designations.find(d => d.id === employee.designationId)?.title || 'Software Engineer',
          hireDate: employee.hireDate || new Date().toISOString().split('T')[0],
          country: employee.country || 'United States',
          currency: employee.currency || 'USD',
          status: employee.status || 'ACTIVE'
        };
        this.mockData.employees.unshift(newEmp);
        return of(newEmp);
      })
    );
  }

  updateEmployee(id: number, employee: Partial<Employee>): Observable<Employee> {
    return this.http.put<ApiResponse<Employee>>(`${this.apiUrl}/${id}`, employee).pipe(
      map(res => res.data),
      catchError(() => {
        const idx = this.mockData.employees.findIndex(e => e.id === id);
        if (idx !== -1) {
          this.mockData.employees[idx] = { ...this.mockData.employees[idx], ...employee };
          return of(this.mockData.employees[idx]);
        }
        return of(this.mockData.employees[0]);
      })
    );
  }

  deleteEmployee(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`).pipe(
      map(() => {}),
      catchError(() => {
        const idx = this.mockData.employees.findIndex(e => e.id === id);
        if (idx !== -1) {
          this.mockData.employees[idx].status = 'INACTIVE';
        }
        return of(undefined);
      })
    );
  }

  exportEmployeesCsv(): Observable<string> {
    return this.http.get(`${this.apiUrl}/export`, { responseType: 'text' }).pipe(
      catchError(() => {
        const header = "Employee ID,First Name,Last Name,Email,Department,Country,Currency,Status\n";
        const rows = this.mockData.employees.slice(0, 100).map(e =>
          `"${e.employeeId}","${e.firstName}","${e.lastName}","${e.email}","${e.departmentName}","${e.country}","${e.currency}","${e.status}"`
        ).join("\n");
        return of(header + rows);
      })
    );
  }
}
