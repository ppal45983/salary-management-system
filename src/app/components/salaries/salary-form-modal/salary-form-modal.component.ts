import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Employee, SalaryRecord, TaxCalculationResponse } from '../../../core/models/models';
import { EmployeeService } from '../../../core/services/employee.service';
import { SalaryService } from '../../../core/services/salary.service';

@Component({
  selector: 'app-salary-form-modal',
  template: `
    <div class="modal-backdrop" (click)="onBackdropClick($event)">
      <div class="modal-content salary-modal">
        <div class="modal-header">
          <div>
            <h3 class="modal-title">{{ isEdit ? 'Update Salary Record' : 'Create Salary Record' }}</h3>
            <p class="modal-sub">Calculates automatic multi-country progressive tax slabs and net pay</p>
          </div>
          <button class="btn-icon" (click)="closeModal()">
            <i class="fa-solid fa-xmark"></i>
          </button>
        </div>

        <form (ngSubmit)="saveSalary()">
          <div class="modal-body">
            <!-- Employee Selector (only in create mode) -->
            <div class="form-group" *ngIf="!isEdit">
              <label>Select Employee *</label>
              <select class="form-control" [(ngModel)]="formData.employeeId" name="employeeId" required (change)="onEmployeeSelected()">
                <option *ngFor="let emp of employees" [value]="emp.id">
                  {{ emp.employeeId }} - {{ emp.firstName }} {{ emp.lastName }} ({{ emp.country }})
                </option>
              </select>
            </div>

            <!-- Salary Input Components -->
            <div class="form-row">
              <div class="form-group flex-1">
                <label>Annual Base Salary *</label>
                <input
                  type="number"
                  class="form-control font-bold"
                  [(ngModel)]="formData.baseSalary"
                  name="baseSalary"
                  required
                  min="1"
                  (input)="recalculateTaxPreview()"
                />
              </div>
              <div class="form-group flex-1">
                <label>Allowances</label>
                <input
                  type="number"
                  class="form-control"
                  [(ngModel)]="formData.allowances"
                  name="allowances"
                  min="0"
                  (input)="recalculateTaxPreview()"
                />
              </div>
              <div class="form-group flex-1">
                <label>Pre-Tax Deductions</label>
                <input
                  type="number"
                  class="form-control"
                  [(ngModel)]="formData.deductions"
                  name="deductions"
                  min="0"
                  (input)="recalculateTaxPreview()"
                />
              </div>
            </div>

            <!-- Real-Time Tax & Net Pay Live Calculation Widget -->
            <div class="live-calc-box mt-16" *ngIf="taxPreview">
              <div class="calc-header">
                <span class="calc-badge">
                  <i class="fa-solid fa-bolt"></i> REAL-TIME TAX COMPUTATION ({{ selectedCountry }})
                </span>
                <span class="effective-rate">Effective Tax Rate: {{ taxPreview.effectiveTaxRate }}%</span>
              </div>
              <div class="calc-metrics">
                <div class="calc-col">
                  <span class="c-title">Gross Salary</span>
                  <span class="c-val">{{ getSymbol(formData.currency) }}{{ taxPreview.grossSalary | number }}</span>
                </div>
                <div class="calc-col">
                  <span class="c-title">Calculated Tax</span>
                  <span class="c-val text-danger">-{{ getSymbol(formData.currency) }}{{ taxPreview.totalTax | number }}</span>
                </div>
                <div class="calc-col">
                  <span class="c-title">Net Annual Pay</span>
                  <span class="c-val text-success font-bold">{{ getSymbol(formData.currency) }}{{ taxPreview.netSalary | number }}</span>
                </div>
                <div class="calc-col">
                  <span class="c-title">Monthly Net Take-Home</span>
                  <span class="c-val text-primary font-bold">{{ getSymbol(formData.currency) }}{{ (taxPreview.netSalary / 12) | number:'1.0-0' }}</span>
                </div>
              </div>
            </div>

            <div class="form-row mt-16">
              <div class="form-group flex-1">
                <label>Effective Date *</label>
                <input type="date" class="form-control" [(ngModel)]="formData.effectiveDate" name="effectiveDate" required />
              </div>
              <div class="form-group flex-1">
                <label>Pay Frequency</label>
                <select class="form-control" [(ngModel)]="formData.payFrequency" name="payFrequency">
                  <option value="MONTHLY">MONTHLY</option>
                  <option value="BI_WEEKLY">BI_WEEKLY</option>
                  <option value="ANNUALLY">ANNUALLY</option>
                </select>
              </div>
              <div class="form-group flex-1">
                <label>Status</label>
                <select class="form-control" [(ngModel)]="formData.status" name="status">
                  <option value="ACTIVE">ACTIVE (Immediate)</option>
                  <option value="INACTIVE">INACTIVE (Requires Approval)</option>
                </select>
              </div>
            </div>

            <div class="form-group">
              <label>Approval / Adjustment Comments</label>
              <textarea class="form-control" rows="2" [(ngModel)]="formData.comments" name="comments" placeholder="e.g., Annual merit revision approved by department head"></textarea>
            </div>
          </div>

          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" (click)="closeModal()">Cancel</button>
            <button type="submit" class="btn btn-primary" [disabled]="saving">
              <i *ngIf="saving" class="fa-solid fa-spinner fa-spin"></i>
              <span>{{ saving ? 'Processing...' : 'Save & Calculate Salary' }}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .salary-modal { max-width: 680px; }
    .modal-title { font-size: 1.25rem; font-weight: 800; color: #0f172a; }
    .modal-sub { font-size: 0.8rem; color: #64748b; margin-top: 2px; }
    .form-row { display: flex; gap: 14px; }
    .flex-1 { flex: 1; }
    .mt-16 { margin-top: 16px; }
    .font-bold { font-weight: 700; }
    .text-danger { color: #ef4444; }
    .text-success { color: #10b981; }
    .text-primary { color: #4f46e5; }
    .live-calc-box {
      background: #f8fafc;
      border: 1px solid #c7d2fe;
      border-radius: 12px;
      padding: 14px 16px;
    }
    .calc-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
    .calc-badge { font-size: 0.7rem; font-weight: 800; color: #4f46e5; letter-spacing: 0.05em; }
    .effective-rate { font-size: 0.775rem; font-weight: 700; color: #0f172a; }
    .calc-metrics { display: grid; grid-template-columns: repeat(4, 1fr); gap: 10px; text-align: center; }
    .calc-col { background: #ffffff; padding: 8px 10px; border-radius: 8px; border: 1px solid #e2e8f0; }
    .c-title { font-size: 0.675rem; color: #64748b; text-transform: uppercase; font-weight: 600; display: block; margin-bottom: 2px; }
    .c-val { font-size: 0.95rem; }
  `]
})
export class SalaryFormModalComponent implements OnInit {
  @Input() salary: SalaryRecord | null = null;
  @Output() close = new EventEmitter<void>();
  @Output() saved = new EventEmitter<SalaryRecord>();

  employees: Employee[] = [];
  selectedCountry = 'United States';
  taxPreview: TaxCalculationResponse | null = null;
  isEdit = false;
  saving = false;

  formData: Partial<SalaryRecord> = {
    employeeId: 1,
    baseSalary: 95000,
    allowances: 10000,
    deductions: 3500,
    effectiveDate: new Date().toISOString().split('T')[0],
    payFrequency: 'MONTHLY',
    status: 'ACTIVE',
    currency: 'USD',
    comments: 'Standard compensation plan'
  };

  constructor(
    private employeeService: EmployeeService,
    private salaryService: SalaryService
  ) {}

  ngOnInit() {
    this.employeeService.getEmployees(0, 50).subscribe(res => {
      this.employees = res.content;
      if (this.salary) {
        this.isEdit = true;
        this.formData = { ...this.salary };
        this.selectedCountry = this.salary.country || 'United States';
      } else if (this.employees.length > 0) {
        this.formData.employeeId = this.employees[0].id;
        this.selectedCountry = this.employees[0].country;
      }
      this.recalculateTaxPreview();
    });
  }

  onEmployeeSelected() {
    const emp = this.employees.find(e => e.id === Number(this.formData.employeeId));
    if (emp) {
      this.selectedCountry = emp.country;
      this.formData.currency = emp.currency;
      this.recalculateTaxPreview();
    }
  }

  recalculateTaxPreview() {
    this.salaryService.calculateTaxPreview({
      baseSalary: this.formData.baseSalary || 0,
      allowances: this.formData.allowances || 0,
      deductions: this.formData.deductions || 0,
      country: this.selectedCountry,
      currency: this.formData.currency || 'USD'
    }).subscribe(prev => {
      this.taxPreview = prev;
    });
  }

  closeModal() {
    this.close.emit();
  }

  onBackdropClick(e: MouseEvent) {
    if ((e.target as HTMLElement).classList.contains('modal-backdrop')) {
      this.closeModal();
    }
  }

  saveSalary() {
    this.saving = true;
    if (this.isEdit && this.salary) {
      this.salaryService.updateSalary(this.salary.id, this.formData).subscribe(res => {
        this.saving = false;
        this.saved.emit(res);
      });
    } else {
      this.salaryService.createSalary(this.formData).subscribe(res => {
        this.saving = false;
        this.saved.emit(res);
      });
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
