import { Component, OnInit } from '@angular/core';
import { SalaryService } from '../../../core/services/salary.service';
import { NotificationService } from '../../../core/services/notification.service';
import { SalaryRecord } from '../../../core/models/models';

@Component({
  selector: 'app-salary-list',
  template: `
    <div class="page-wrapper">
      <!-- Header -->
      <div class="page-header">
        <div>
          <h2>Salary Records & Approvals</h2>
          <p class="text-muted">Manage compensation packages, approve pending salary proposals & generate salary slips</p>
        </div>
        <div class="header-actions">
          <button class="btn btn-secondary" (click)="exportCsv()">
            <i class="fa-solid fa-file-csv"></i>
            <span>Export CSV</span>
          </button>
          <button class="btn btn-primary" (click)="openCreateModal()">
            <i class="fa-solid fa-plus"></i>
            <span>Create Salary Record</span>
          </button>
        </div>
      </div>

      <!-- Filter Tabs -->
      <div class="glass-card toolbar-card">
        <div class="status-tabs">
          <button class="tab-btn" [class.active]="selectedTab === 'ALL'" (click)="setTab('ALL')">
            All Records
          </button>
          <button class="tab-btn" [class.active]="selectedTab === 'ACTIVE'" (click)="setTab('ACTIVE')">
            Active Payroll
          </button>
          <button class="tab-btn" [class.active]="selectedTab === 'INACTIVE'" (click)="setTab('INACTIVE')">
            Pending Approvals
            <span class="tab-count">14</span>
          </button>
        </div>
      </div>

      <!-- Salary Records Table -->
      <div class="glass-card mt-20">
        <div class="table-responsive">
          <table class="data-table">
            <thead>
              <tr>
                <th>Employee</th>
                <th>Base Salary</th>
                <th>Allowances</th>
                <th>Gross Pay</th>
                <th>Income Tax</th>
                <th>Net Salary</th>
                <th>Effective Date</th>
                <th>Status</th>
                <th class="text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngIf="loading">
                <td colspan="9" class="text-center py-30">
                  <i class="fa-solid fa-spinner fa-spin fa-2x text-primary"></i>
                  <p class="text-muted mt-8">Loading salary records...</p>
                </td>
              </tr>
              <tr *ngIf="!loading && salaries.length === 0">
                <td colspan="9" class="text-center py-30 text-muted">
                  <i class="fa-regular fa-folder-open fa-2x"></i>
                  <p class="mt-8">No salary records found for the selected filter.</p>
                </td>
              </tr>
              <tr *ngFor="let sal of salaries">
                <td>
                  <div>
                    <div class="font-semibold">{{ sal.employeeName || 'ACME Employee' }}</div>
                    <div class="text-xs text-muted">{{ sal.employeeCode }} • {{ sal.country }}</div>
                  </div>
                </td>
                <td>{{ getSymbol(sal.currency) }}{{ sal.baseSalary | number }}</td>
                <td class="text-success">+{{ getSymbol(sal.currency) }}{{ sal.allowances | number }}</td>
                <td class="font-semibold">{{ getSymbol(sal.currency) }}{{ sal.grossSalary | number }}</td>
                <td class="text-danger">-{{ getSymbol(sal.currency) }}{{ sal.tax | number }}</td>
                <td>
                  <span class="font-bold text-primary">{{ getSymbol(sal.currency) }}{{ sal.netSalary | number }}</span>
                </td>
                <td>{{ sal.effectiveDate }}</td>
                <td>
                  <span class="badge" [ngClass]="sal.status === 'ACTIVE' ? 'badge-success' : 'badge-warning'">
                    {{ sal.status }}
                  </span>
                </td>
                <td class="text-right">
                  <div class="action-btns">
                    <button class="btn btn-secondary btn-sm" (click)="openSlipModal(sal.id)" title="Generate Salary Slip">
                      <i class="fa-solid fa-receipt"></i>
                      <span>Slip</span>
                    </button>
                    <button *ngIf="sal.status !== 'ACTIVE'" class="btn btn-success btn-sm" (click)="approveSalary(sal)" title="Approve Salary">
                      <i class="fa-solid fa-check"></i>
                      <span>Approve</span>
                    </button>
                    <button class="btn btn-secondary btn-sm btn-icon" (click)="openEditModal(sal)" title="Edit Salary">
                      <i class="fa-regular fa-pen-to-square"></i>
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
            Showing Page <strong>{{ currentPage + 1 }}</strong> of <strong>{{ totalPages }}</strong>
          </div>
          <div class="pagination-controls">
            <button class="btn btn-secondary btn-sm" [disabled]="currentPage === 0" (click)="goToPage(currentPage - 1)">
              Previous
            </button>
            <span class="current-page-badge">{{ currentPage + 1 }}</span>
            <button class="btn btn-secondary btn-sm" [disabled]="currentPage >= totalPages - 1" (click)="goToPage(currentPage + 1)">
              Next
            </button>
          </div>
        </div>
      </div>

      <!-- Modals -->
      <app-salary-form-modal
        *ngIf="showFormModal"
        [salary]="selectedSalary"
        (close)="showFormModal = false"
        (saved)="onSalarySaved($event)"
      ></app-salary-form-modal>

      <app-salary-slip-modal
        *ngIf="showSlipModal"
        [salaryRecordId]="selectedSalaryRecordId"
        (close)="showSlipModal = false"
      ></app-salary-slip-modal>
    </div>
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; flex-wrap: wrap; gap: 16px; h2 { font-size: 1.5rem; font-weight: 800; } }
    .header-actions { display: flex; gap: 12px; }
    .toolbar-card { padding: 10px 16px; }
    .status-tabs { display: flex; gap: 8px; }
    .tab-btn {
      padding: 8px 16px;
      font-family: var(--font-primary);
      font-size: 0.85rem;
      font-weight: 600;
      color: #64748b;
      background: none;
      border: none;
      border-radius: 8px;
      cursor: pointer;
      display: inline-flex;
      align-items: center;
      gap: 8px;
      transition: all 0.2s;
      &:hover { background: #f1f5f9; color: #0f172a; }
      &.active { background: #4f46e5; color: white; .tab-count { background: rgba(255, 255, 255, 0.25); color: white; } }
    }
    .tab-count { font-size: 0.7rem; background: #fee2e2; color: #ef4444; padding: 2px 6px; border-radius: 999px; font-weight: 700; }
    .mt-20 { margin-top: 20px; }
    .text-xs { font-size: 0.75rem; }
    .text-success { color: #10b981; }
    .text-danger { color: #ef4444; }
    .action-btns { display: inline-flex; gap: 6px; }
    .pagination-bar { display: flex; justify-content: space-between; align-items: center; padding: 16px 8px 0; border-top: 1px solid var(--border-color); margin-top: 16px; font-size: 0.825rem; color: #64748b; }
    .pagination-controls { display: flex; align-items: center; gap: 6px; .current-page-badge { width: 28px; height: 28px; background: var(--primary); color: white; border-radius: 6px; display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 0.8rem; } }
  `]
})
export class SalaryListComponent implements OnInit {
  salaries: SalaryRecord[] = [];
  selectedTab: 'ALL' | 'ACTIVE' | 'INACTIVE' = 'ALL';
  currentPage = 0;
  pageSize = 10;
  totalRecords = 0;
  totalPages = 0;
  loading = false;

  showFormModal = false;
  showSlipModal = false;
  selectedSalary: SalaryRecord | null = null;
  selectedSalaryRecordId: number = 1;

  constructor(
    private salaryService: SalaryService,
    private notificationService: NotificationService
  ) {}

  ngOnInit() {
    this.loadSalaries();
  }

  loadSalaries() {
    this.loading = true;
    const status = this.selectedTab === 'ALL' ? undefined : this.selectedTab;
    this.salaryService.getSalaries(this.currentPage, this.pageSize, undefined, status).subscribe(res => {
      this.salaries = res.content;
      this.totalRecords = res.totalElements;
      this.totalPages = res.totalPages;
      this.loading = false;
    });
  }

  setTab(tab: 'ALL' | 'ACTIVE' | 'INACTIVE') {
    this.selectedTab = tab;
    this.currentPage = 0;
    this.loadSalaries();
  }

  goToPage(p: number) {
    this.currentPage = p;
    this.loadSalaries();
  }

  openCreateModal() {
    this.selectedSalary = null;
    this.showFormModal = true;
  }

  openEditModal(sal: SalaryRecord) {
    this.selectedSalary = { ...sal };
    this.showFormModal = true;
  }

  openSlipModal(id: number) {
    this.selectedSalaryRecordId = id;
    this.showSlipModal = true;
  }

  approveSalary(sal: SalaryRecord) {
    this.salaryService.approveSalary(sal.id).subscribe(() => {
      this.notificationService.success('Salary Approved', `Salary record for ${sal.employeeName || 'Employee'} is now active.`);
      this.loadSalaries();
    });
  }

  onSalarySaved(sal: SalaryRecord) {
    this.showFormModal = false;
    this.notificationService.success('Salary Record Saved', 'Salary information and multi-country tax calculations have been saved.');
    this.loadSalaries();
  }

  exportCsv() {
    this.salaryService.exportSalariesCsv().subscribe(csv => {
      const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `salaries_export_${new Date().toISOString().split('T')[0]}.csv`;
      a.click();
      URL.revokeObjectURL(url);
      this.notificationService.success('Export Ready', 'Salary records CSV has been downloaded.');
    });
  }

  getSymbol(curr?: string): string {
    switch (curr) {
      case 'INR': return '₹';
      case 'GBP': return '£';
      case 'EUR': return '€';
      case 'JPY': return '¥';
      case 'CAD': return 'C$';
      case 'AUD': return 'A$';
      case 'SGD': return 'S$';
      case 'USD':
      default: return '$';
    }
  }
}
