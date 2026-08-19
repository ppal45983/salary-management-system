import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Employee, SalaryHistory, SalaryRecord } from '../../../core/models/models';
import { SalaryService } from '../../../core/services/salary.service';

@Component({
  selector: 'app-employee-detail-modal',
  template: `
    <div class="modal-backdrop" (click)="onBackdropClick($event)">
      <div class="modal-content profile-modal">
        <div class="modal-header">
          <div class="header-profile">
            <div class="profile-avatar">{{ getInitials() }}</div>
            <div>
              <h3 class="modal-title">{{ employee?.firstName }} {{ employee?.lastName }}</h3>
              <p class="modal-sub">{{ employee?.employeeId }} • {{ employee?.designationName || 'Software Engineer' }} ({{ employee?.departmentName || 'Engineering' }})</p>
            </div>
          </div>
          <button class="btn-icon" (click)="closeModal()">
            <i class="fa-solid fa-xmark"></i>
          </button>
        </div>

        <div class="modal-body">
          <!-- Quick Info Cards -->
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">Corporate Email</span>
              <span class="detail-value font-semibold">{{ employee?.email }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Phone</span>
              <span class="detail-value">{{ employee?.phone || '+1-555-0192' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Location / Country</span>
              <span class="detail-value">{{ employee?.country }} ({{ employee?.currency }})</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Hire Date</span>
              <span class="detail-value">{{ employee?.hireDate }}</span>
            </div>
          </div>

          <!-- Current Active Compensation Card -->
          <div class="salary-card mt-20" *ngIf="activeSalary">
            <div class="salary-header">
              <div>
                <span class="salary-tag">ACTIVE COMPENSATION PACKAGE</span>
                <div class="salary-amount">{{ getSymbol(activeSalary.currency) }}{{ activeSalary.grossSalary | number }} <span class="per-period">/ year ({{ activeSalary.currency }})</span></div>
              </div>
              <span class="badge badge-success">{{ activeSalary.status }}</span>
            </div>

            <div class="breakdown-grid">
              <div class="breakdown-box">
                <span class="b-label">Base Salary</span>
                <span class="b-val">{{ getSymbol(activeSalary.currency) }}{{ activeSalary.baseSalary | number }}</span>
              </div>
              <div class="breakdown-box">
                <span class="b-label">Allowances</span>
                <span class="b-val text-success">+{{ getSymbol(activeSalary.currency) }}{{ activeSalary.allowances | number }}</span>
              </div>
              <div class="breakdown-box">
                <span class="b-label">Est. Income Tax</span>
                <span class="b-val text-danger">-{{ getSymbol(activeSalary.currency) }}{{ activeSalary.tax | number }}</span>
              </div>
              <div class="breakdown-box net-box">
                <span class="b-label">Estimated Net Pay</span>
                <span class="b-val font-bold text-primary">{{ getSymbol(activeSalary.currency) }}{{ activeSalary.netSalary | number }}</span>
              </div>
            </div>
          </div>

          <!-- Salary History Timeline -->
          <div class="history-section mt-20">
            <h4 class="section-title">
              <i class="fa-solid fa-timeline"></i> Compensation History & Audit Trail
            </h4>
            <div class="timeline-list" *ngIf="history.length > 0">
              <div class="timeline-item" *ngFor="let h of history">
                <div class="timeline-marker"></div>
                <div class="timeline-content">
                  <div class="timeline-header">
                    <span class="change-tag">{{ h.changeType }}</span>
                    <span class="timeline-date">{{ h.effectiveDate }}</span>
                  </div>
                  <div class="timeline-salary">
                    Gross: {{ getSymbol(h.currency) }}{{ h.grossSalary | number }} (Net: {{ getSymbol(h.currency) }}{{ h.netSalary | number }})
                  </div>
                  <div class="timeline-reason" *ngIf="h.changeReason">{{ h.changeReason }}</div>
                </div>
              </div>
            </div>
            <p *ngIf="history.length === 0" class="text-muted text-sm mt-8">No historical progression adjustments recorded yet.</p>
          </div>
        </div>

        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" (click)="closeModal()">Close</button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .profile-modal { max-width: 720px; }
    .header-profile { display: flex; align-items: center; gap: 14px; }
    .profile-avatar {
      width: 48px;
      height: 48px;
      border-radius: 14px;
      background: linear-gradient(135deg, #4f46e5, #06b6d4);
      color: white;
      font-weight: 800;
      font-size: 1.1rem;
      display: flex;
      align-items: center;
      justify-content: center;
    }
    .modal-title { font-size: 1.25rem; font-weight: 800; }
    .modal-sub { font-size: 0.8rem; color: #64748b; }
    .detail-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 12px;
      background: #f8fafc;
      padding: 16px;
      border-radius: 12px;
      border: 1px solid #e2e8f0;
    }
    .detail-label { font-size: 0.725rem; color: #64748b; text-transform: uppercase; font-weight: 700; display: block; }
    .detail-value { font-size: 0.875rem; color: #0f172a; margin-top: 2px; }
    .salary-card {
      background: #ffffff;
      border: 1px solid #c7d2fe;
      border-radius: 14px;
      padding: 18px;
      box-shadow: 0 4px 12px rgba(79, 70, 229, 0.08);
    }
    .salary-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 14px; }
    .salary-tag { font-size: 0.7rem; font-weight: 800; color: #4f46e5; letter-spacing: 0.05em; }
    .salary-amount { font-family: 'Outfit', sans-serif; font-size: 1.6rem; font-weight: 800; color: #0f172a; .per-period { font-size: 0.85rem; color: #64748b; font-weight: 500; } }
    .breakdown-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 10px; }
    .breakdown-box {
      background: #f8fafc;
      padding: 10px;
      border-radius: 8px;
      text-align: center;
      .b-label { font-size: 0.7rem; color: #64748b; display: block; margin-bottom: 4px; }
      .b-val { font-size: 0.9rem; font-weight: 700; }
    }
    .net-box { background: #eef2ff; }
    .section-title { font-size: 0.95rem; font-weight: 700; margin-bottom: 12px; color: #0f172a; }
    .timeline-list { display: flex; flex-direction: column; gap: 14px; border-left: 2px solid #e2e8f0; padding-left: 16px; margin-left: 8px; }
    .timeline-item { position: relative; }
    .timeline-marker { position: absolute; left: -22px; top: 4px; width: 10px; height: 10px; border-radius: 50%; background: #4f46e5; }
    .change-tag { background: #e2e8f0; color: #334155; font-size: 0.7rem; font-weight: 700; padding: 2px 6px; border-radius: 4px; }
    .timeline-date { font-size: 0.75rem; color: #64748b; margin-left: 8px; }
    .timeline-salary { font-weight: 700; font-size: 0.85rem; color: #0f172a; margin-top: 4px; }
    .timeline-reason { font-size: 0.775rem; color: #64748b; }
    .mt-20 { margin-top: 20px; }
  `]
})
export class EmployeeDetailModalComponent implements OnInit {
  @Input() employee: Employee | null = null;
  @Output() close = new EventEmitter<void>();

  activeSalary: SalaryRecord | null = null;
  history: SalaryHistory[] = [];

  constructor(private salaryService: SalaryService) {}

  ngOnInit() {
    if (this.employee) {
      this.salaryService.getSalaries(0, 1, this.employee.id).subscribe(res => {
        if (res.content.length > 0) {
          this.activeSalary = res.content[0];
        }
      });

      this.salaryService.getSalaryHistory(this.employee.id).subscribe(h => {
        this.history = h;
      });
    }
  }

  getInitials(): string {
    if (!this.employee) return 'EM';
    return ((this.employee.firstName?.[0] || '') + (this.employee.lastName?.[0] || '')).toUpperCase();
  }

  closeModal() {
    this.close.emit();
  }

  onBackdropClick(e: MouseEvent) {
    if ((e.target as HTMLElement).classList.contains('modal-backdrop')) {
      this.closeModal();
    }
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
