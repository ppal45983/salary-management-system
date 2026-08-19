import { Component, OnInit } from '@angular/core';
import { EmployeeService } from '../../../core/services/employee.service';
import { MasterDataService } from '../../../core/services/master-data.service';
import { NotificationService } from '../../../core/services/notification.service';
import { CountryInfo, Department, Employee, PageResponse } from '../../../core/models/models';

@Component({
  selector: 'app-employee-list',
  template: `
    <div class="page-wrapper">
      <!-- Header -->
      <div class="page-header">
        <div>
          <h2>Employee Directory (10,000 Records)</h2>
          <p class="text-muted">Search, filter, manage global employees, and compensation assignments</p>
        </div>
        <div class="header-actions">
          <button class="btn btn-secondary" (click)="exportCsv()">
            <i class="fa-solid fa-file-csv"></i>
            <span>Export CSV</span>
          </button>
          <button class="btn btn-primary" (click)="openCreateModal()">
            <i class="fa-solid fa-user-plus"></i>
            <span>Add Employee</span>
          </button>
        </div>
      </div>

      <!-- Filters & Search Toolbar -->
      <div class="glass-card toolbar-card">
        <div class="search-box">
          <i class="fa-solid fa-magnifying-glass search-icon"></i>
          <input
            type="text"
            class="form-control search-input"
            placeholder="Search by name, email, or employee ID (e.g. EMP-00123)..."
            [(ngModel)]="searchTerm"
            (keyup.enter)="onSearch()"
          />
        </div>

        <div class="filter-group">
          <!-- Department Filter -->
          <select class="form-control filter-select" [(ngModel)]="selectedDept" (change)="onSearch()">
            <option [ngValue]="null">All Departments</option>
            <option *ngFor="let d of departments" [ngValue]="d.id">{{ d.name }}</option>
          </select>

          <!-- Country Filter -->
          <select class="form-control filter-select" [(ngModel)]="selectedCountry" (change)="onSearch()">
            <option [ngValue]="null">All Countries</option>
            <option *ngFor="let c of countries" [ngValue]="c.name">{{ c.name }} ({{ c.currency }})</option>
          </select>

          <!-- Status Filter -->
          <select class="form-control filter-select" [(ngModel)]="selectedStatus" (change)="onSearch()">
            <option [ngValue]="null">All Statuses</option>
            <option value="ACTIVE">Active</option>
            <option value="ON_LEAVE">On Leave</option>
            <option value="INACTIVE">Inactive</option>
          </select>

          <button class="btn btn-secondary" (click)="resetFilters()" title="Reset Filters">
            <i class="fa-solid fa-filter-circle-xmark"></i>
          </button>
        </div>
      </div>

      <!-- Employee Data Table -->
      <div class="glass-card mt-20">
        <div class="table-responsive">
          <table class="data-table">
            <thead>
              <tr>
                <th>Employee</th>
                <th>Department</th>
                <th>Designation</th>
                <th>Country</th>
                <th>Hire Date</th>
                <th>Status</th>
                <th class="text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngIf="loading">
                <td colspan="7" class="text-center py-30">
                  <i class="fa-solid fa-spinner fa-spin fa-2x text-primary"></i>
                  <p class="text-muted mt-8">Loading employees...</p>
                </td>
              </tr>
              <tr *ngIf="!loading && employees.length === 0">
                <td colspan="7" class="text-center py-30 text-muted">
                  <i class="fa-regular fa-folder-open fa-2x"></i>
                  <p class="mt-8">No employees found matching current criteria.</p>
                </td>
              </tr>
              <tr *ngFor="let emp of employees">
                <td>
                  <div class="emp-cell">
                    <div class="emp-avatar">{{ getInitials(emp.firstName, emp.lastName) }}</div>
                    <div>
                      <div class="emp-name font-semibold hover-link" (click)="openDetailModal(emp)">
                        {{ emp.firstName }} {{ emp.lastName }}
                      </div>
                      <div class="emp-code">{{ emp.employeeId }} • {{ emp.email }}</div>
                    </div>
                  </div>
                </td>
                <td><span class="dept-tag">{{ emp.departmentName || 'Engineering' }}</span></td>
                <td>{{ emp.designationName || 'Software Engineer' }}</td>
                <td>
                  <span class="country-badge">
                    {{ emp.country }} <span class="currency-tag">({{ emp.currency }})</span>
                  </span>
                </td>
                <td>{{ emp.hireDate }}</td>
                <td>
                  <span class="badge" [ngClass]="emp.status === 'ACTIVE' ? 'badge-success' : 'badge-warning'">
                    {{ emp.status }}
                  </span>
                </td>
                <td class="text-right">
                  <div class="action-btns">
                    <button class="btn btn-secondary btn-sm btn-icon" (click)="openDetailModal(emp)" title="View Profile & Salary">
                      <i class="fa-regular fa-eye"></i>
                    </button>
                    <button class="btn btn-secondary btn-sm btn-icon" (click)="openEditModal(emp)" title="Edit Employee">
                      <i class="fa-regular fa-pen-to-square"></i>
                    </button>
                    <button class="btn btn-secondary btn-sm btn-icon text-danger" (click)="deactivateEmployee(emp)" title="Deactivate Employee">
                      <i class="fa-solid fa-user-xmark"></i>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Pagination Controls -->
        <div class="pagination-bar" *ngIf="totalPages > 1">
          <div class="pagination-info">
            Showing Page <strong>{{ currentPage + 1 }}</strong> of <strong>{{ totalPages }}</strong> ({{ totalRecords | number }} total employees)
          </div>
          <div class="pagination-controls">
            <button class="btn btn-secondary btn-sm" [disabled]="currentPage === 0" (click)="goToPage(0)" title="First Page">
              <i class="fa-solid fa-angles-left"></i>
            </button>
            <button class="btn btn-secondary btn-sm" [disabled]="currentPage === 0" (click)="goToPage(currentPage - 1)">
              Previous
            </button>
            <span class="current-page-badge">{{ currentPage + 1 }}</span>
            <button class="btn btn-secondary btn-sm" [disabled]="currentPage >= totalPages - 1" (click)="goToPage(currentPage + 1)">
              Next
            </button>
            <button class="btn btn-secondary btn-sm" [disabled]="currentPage >= totalPages - 1" (click)="goToPage(totalPages - 1)" title="Last Page">
              <i class="fa-solid fa-angles-right"></i>
            </button>
          </div>
        </div>
      </div>

      <!-- Modals -->
      <app-employee-form-modal
        *ngIf="showFormModal"
        [employee]="selectedEmployee"
        [departments]="departments"
        [countries]="countries"
        (close)="showFormModal = false"
        (saved)="onEmployeeSaved($event)"
      ></app-employee-form-modal>

      <app-employee-detail-modal
        *ngIf="showDetailModal"
        [employee]="selectedEmployee"
        (close)="showDetailModal = false"
      ></app-employee-detail-modal>
    </div>
  `,
  styles: [`
    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
      flex-wrap: wrap;
      gap: 16px;

      h2 { font-size: 1.5rem; font-weight: 800; }
    }
    .header-actions { display: flex; gap: 12px; }
    .toolbar-card {
      padding: 16px 20px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 16px;
      flex-wrap: wrap;
    }
    .search-box {
      flex: 1;
      min-width: 280px;
      position: relative;
      .search-icon {
        position: absolute;
        left: 14px;
        top: 50%;
        transform: translateY(-50%);
        color: #94a3b8;
      }
      .search-input { padding-left: 40px; }
    }
    .filter-group {
      display: flex;
      gap: 10px;
      align-items: center;
      flex-wrap: wrap;
    }
    .filter-select {
      width: auto;
      min-width: 150px;
    }
    .mt-20 { margin-top: 20px; }
    .py-30 { padding-top: 30px; padding-bottom: 30px; }
    .mt-8 { margin-top: 8px; }
    .text-center { text-align: center; }
    .text-right { text-align: right; }
    .emp-cell {
      display: flex;
      align-items: center;
      gap: 12px;
      .emp-avatar {
        width: 36px;
        height: 36px;
        border-radius: 50%;
        background: #eef2ff;
        color: #4f46e5;
        font-weight: 700;
        font-size: 0.8rem;
        display: flex;
        align-items: center;
        justify-content: center;
      }
      .emp-name { color: #0f172a; cursor: pointer; }
      .emp-code { font-size: 0.75rem; color: #64748b; }
    }
    .dept-tag {
      background: #f1f5f9;
      color: #334155;
      padding: 4px 8px;
      border-radius: 6px;
      font-size: 0.8rem;
      font-weight: 600;
    }
    .country-badge {
      font-weight: 600;
      .currency-tag { color: #64748b; font-size: 0.775rem; }
    }
    .action-btns {
      display: inline-flex;
      gap: 6px;
    }
    .pagination-bar {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px 8px 0;
      border-top: 1px solid var(--border-color);
      margin-top: 16px;
      flex-wrap: wrap;
      gap: 12px;
      font-size: 0.825rem;
      color: #64748b;
    }
    .pagination-controls {
      display: flex;
      align-items: center;
      gap: 6px;
      .current-page-badge {
        width: 28px;
        height: 28px;
        background: var(--primary);
        color: white;
        border-radius: 6px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: 700;
        font-size: 0.8rem;
      }
    }
  `]
})
export class EmployeeListComponent implements OnInit {
  employees: Employee[] = [];
  departments: Department[] = [];
  countries: CountryInfo[] = [];

  searchTerm = '';
  selectedDept: number | null = null;
  selectedCountry: string | null = null;
  selectedStatus: string | null = null;

  currentPage = 0;
  pageSize = 10;
  totalRecords = 0;
  totalPages = 0;
  loading = false;

  showFormModal = false;
  showDetailModal = false;
  selectedEmployee: Employee | null = null;

  constructor(
    private employeeService: EmployeeService,
    private masterDataService: MasterDataService,
    private notificationService: NotificationService
  ) {}

  ngOnInit() {
    this.loadMasters();
    this.loadEmployees();
  }

  loadMasters() {
    this.masterDataService.getDepartments().subscribe(d => this.departments = d);
    this.masterDataService.getCountries().subscribe(c => this.countries = c);
  }

  loadEmployees() {
    this.loading = true;
    this.employeeService.getEmployees(
      this.currentPage,
      this.pageSize,
      this.searchTerm || undefined,
      this.selectedDept || undefined,
      this.selectedCountry || undefined,
      this.selectedStatus || undefined
    ).subscribe(res => {
      this.employees = res.content;
      this.totalRecords = res.totalElements;
      this.totalPages = res.totalPages;
      this.loading = false;
    });
  }

  onSearch() {
    this.currentPage = 0;
    this.loadEmployees();
  }

  resetFilters() {
    this.searchTerm = '';
    this.selectedDept = null;
    this.selectedCountry = null;
    this.selectedStatus = null;
    this.currentPage = 0;
    this.loadEmployees();
  }

  goToPage(p: number) {
    this.currentPage = p;
    this.loadEmployees();
  }

  getInitials(fn: string, ln: string): string {
    return ((fn?.[0] || '') + (ln?.[0] || '')).toUpperCase();
  }

  openCreateModal() {
    this.selectedEmployee = null;
    this.showFormModal = true;
  }

  openEditModal(emp: Employee) {
    this.selectedEmployee = { ...emp };
    this.showFormModal = true;
  }

  openDetailModal(emp: Employee) {
    this.selectedEmployee = emp;
    this.showDetailModal = true;
  }

  onEmployeeSaved(emp: Employee) {
    this.showFormModal = false;
    this.notificationService.success('Employee Saved', `${emp.firstName} ${emp.lastName} has been saved successfully.`);
    this.loadEmployees();
  }

  deactivateEmployee(emp: Employee) {
    if (confirm(`Are you sure you want to deactivate ${emp.firstName} ${emp.lastName}?`)) {
      this.employeeService.deleteEmployee(emp.id).subscribe(() => {
        this.notificationService.info('Deactivated', `Employee ${emp.employeeId} has been deactivated.`);
        this.loadEmployees();
      });
    }
  }

  exportCsv() {
    this.employeeService.exportEmployeesCsv().subscribe(csv => {
      const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `employees_10000_${new Date().toISOString().split('T')[0]}.csv`;
      a.click();
      URL.revokeObjectURL(url);
      this.notificationService.success('Export Ready', 'Employee CSV has been downloaded successfully.');
    });
  }
}
