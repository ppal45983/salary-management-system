import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { SalarySlip } from '../../../core/models/models';
import { SalaryService } from '../../../core/services/salary.service';

@Component({
  selector: 'app-salary-slip-modal',
  template: `
    <div class="modal-backdrop" (click)="onBackdropClick($event)">
      <div class="modal-content slip-modal">
        <!-- Action Header -->
        <div class="modal-header no-print">
          <div>
            <h3 class="modal-title">Employee Salary Slip</h3>
            <p class="modal-sub">Formatted official pay voucher & progressive tax breakdown</p>
          </div>
          <div class="header-actions">
            <button class="btn btn-primary btn-sm" (click)="printSlip()">
              <i class="fa-solid fa-print"></i>
              <span>Print / Download PDF</span>
            </button>
            <button class="btn-icon" (click)="closeModal()">
              <i class="fa-solid fa-xmark"></i>
            </button>
          </div>
        </div>

        <!-- Printable Body -->
        <div class="modal-body print-area" *ngIf="slip">
          <!-- Company Branding -->
          <div class="slip-brand">
            <div class="brand-left">
              <div class="slip-logo">
                <i class="fa-solid fa-vault"></i>
              </div>
              <div>
                <h2 class="company-name">{{ slip.companyName }}</h2>
                <div class="company-addr">{{ slip.companyAddress }}</div>
              </div>
            </div>
            <div class="brand-right">
              <div class="slip-title">PAYSLIP VOUCHER</div>
              <div class="slip-meta">Period: <strong>{{ slip.payPeriod }}</strong></div>
              <div class="slip-meta">Voucher #: <strong>{{ slip.slipNumber }}</strong></div>
            </div>
          </div>

          <div class="divider"></div>

          <!-- Employee Information Grid -->
          <div class="emp-summary-grid">
            <div class="grid-item">
              <span class="g-label">Employee Name</span>
              <span class="g-val font-bold">{{ slip.employeeName }}</span>
            </div>
            <div class="grid-item">
              <span class="g-label">Employee ID</span>
              <span class="g-val">{{ slip.employeeCode }}</span>
            </div>
            <div class="grid-item">
              <span class="g-label">Department</span>
              <span class="g-val">{{ slip.departmentName }}</span>
            </div>
            <div class="grid-item">
              <span class="g-label">Designation</span>
              <span class="g-val">{{ slip.designationTitle }}</span>
            </div>
            <div class="grid-item">
              <span class="g-label">Country / Currency</span>
              <span class="g-val">{{ slip.country }} ({{ slip.currency }})</span>
            </div>
            <div class="grid-item">
              <span class="g-label">Bank Account</span>
              <span class="g-val">{{ slip.bankAccount }} ({{ slip.bankCode }})</span>
            </div>
          </div>

          <!-- Earnings & Deductions Tables -->
          <div class="tables-split mt-20">
            <!-- Earnings -->
            <div class="table-box">
              <div class="box-title text-success">EARNINGS</div>
              <table class="slip-table">
                <tbody>
                  <tr>
                    <td>Base Basic Salary</td>
                    <td class="text-right">\${{ slip.baseSalary | number }}</td>
                  </tr>
                  <tr>
                    <td>Allowances & Benefits</td>
                    <td class="text-right">\${{ slip.allowances | number }}</td>
                  </tr>
                  <tr class="total-row">
                    <td><strong>Gross Earnings</strong></td>
                    <td class="text-right"><strong>\${{ slip.grossSalary | number }}</strong></td>
                  </tr>
                </tbody>
              </table>
            </div>

            <!-- Deductions -->
            <div class="table-box">
              <div class="box-title text-danger">DEDUCTIONS & TAX</div>
              <table class="slip-table">
                <tbody>
                  <tr>
                    <td>Standard Pre-Tax Deductions</td>
                    <td class="text-right">\${{ slip.standardDeductions | number }}</td>
                  </tr>
                  <tr>
                    <td>Income Tax ({{ slip.country }})</td>
                    <td class="text-right text-danger">\${{ slip.incomeTax | number }}</td>
                  </tr>
                  <tr class="total-row">
                    <td><strong>Total Deductions</strong></td>
                    <td class="text-right"><strong>\${{ slip.totalDeductions | number }}</strong></td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <!-- Net Pay Card -->
          <div class="net-pay-banner mt-20">
            <div class="net-left">
              <span class="net-label">NET TAKE-HOME PAY ({{ slip.currency }})</span>
              <div class="net-words">Effective Progressive Tax Rate: <strong>{{ slip.effectiveTaxRate }}%</strong></div>
            </div>
            <div class="net-amount">
              \${{ slip.netSalary | number }}
            </div>
          </div>

          <!-- Progressive Tax Breakdown Sub-table -->
          <div class="tax-breakdown-box mt-20" *ngIf="slip.taxBreakdown && slip.taxBreakdown.length > 0">
            <div class="box-title">PROGRESSIVE TAX SLABS COMPUTED</div>
            <table class="slip-table text-xs">
              <thead>
                <tr>
                  <th>Income Slab Bracket</th>
                  <th>Tax Rate</th>
                  <th>Taxable in Bracket</th>
                  <th class="text-right">Tax for Bracket</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let b of slip.taxBreakdown">
                  <td>\${{ b.bracketFrom | number }} - {{ b.bracketTo ? ('$' + (b.bracketTo | number)) : 'Above' }}</td>
                  <td>{{ b.rate }}%</td>
                  <td>\${{ b.taxableAmountInBracket | number }}</td>
                  <td class="text-right font-semibold">\${{ b.taxForBracket | number }}</td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Signature Footer -->
          <div class="slip-footer mt-24">
            <div class="sig-block">
              <div class="sig-line"></div>
              <span>HR / Payroll Officer</span>
            </div>
            <div class="sig-block text-right">
              <div class="sig-line"></div>
              <span>System Generated & Verified</span>
            </div>
          </div>
        </div>

        <div class="modal-footer no-print">
          <button type="button" class="btn btn-secondary" (click)="closeModal()">Close</button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .slip-modal { max-width: 780px; }
    .modal-title { font-size: 1.25rem; font-weight: 800; color: #0f172a; }
    .modal-sub { font-size: 0.8rem; color: #64748b; margin-top: 2px; }
    .header-actions { display: flex; gap: 10px; align-items: center; }

    .print-area {
      padding: 24px;
      background: #ffffff;
      color: #0f172a;
    }

    .slip-brand {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .brand-left {
        display: flex;
        align-items: center;
        gap: 12px;
        .slip-logo {
          width: 44px;
          height: 44px;
          border-radius: 10px;
          background: #4f46e5;
          color: white;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 1.25rem;
        }
        .company-name { font-size: 1.2rem; font-weight: 800; }
        .company-addr { font-size: 0.75rem; color: #64748b; }
      }

      .brand-right {
        text-align: right;
        .slip-title { font-size: 1.1rem; font-weight: 800; letter-spacing: 0.05em; color: #4f46e5; }
        .slip-meta { font-size: 0.75rem; color: #64748b; margin-top: 2px; }
      }
    }

    .divider { height: 1px; background: #e2e8f0; margin: 16px 0; }

    .emp-summary-grid {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 12px;
      background: #f8fafc;
      padding: 14px;
      border-radius: 8px;
      border: 1px solid #e2e8f0;
      .g-label { font-size: 0.7rem; color: #64748b; text-transform: uppercase; font-weight: 700; display: block; }
      .g-val { font-size: 0.85rem; color: #0f172a; }
    }

    .tables-split { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
    .table-box {
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      padding: 12px;
      .box-title { font-size: 0.75rem; font-weight: 800; letter-spacing: 0.05em; margin-bottom: 8px; }
    }

    .slip-table {
      width: 100%;
      border-collapse: collapse;
      font-size: 0.825rem;
      td, th { padding: 6px 4px; }
      .total-row { border-top: 1px solid #e2e8f0; }
    }

    .net-pay-banner {
      background: #eef2ff;
      border: 2px dashed #6366f1;
      border-radius: 12px;
      padding: 16px 20px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      .net-label { font-size: 0.75rem; font-weight: 800; color: #4f46e5; letter-spacing: 0.05em; display: block; }
      .net-words { font-size: 0.8rem; color: #64748b; margin-top: 2px; }
      .net-amount { font-family: 'Outfit', sans-serif; font-size: 2rem; font-weight: 800; color: #1e1b4b; }
    }

    .tax-breakdown-box {
      background: #f8fafc;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      padding: 12px;
      .box-title { font-size: 0.7rem; font-weight: 800; color: #64748b; margin-bottom: 6px; }
    }

    .slip-footer {
      display: flex;
      justify-content: space-between;
      margin-top: 32px;
      padding-top: 16px;
      .sig-block {
        width: 180px;
        .sig-line { height: 1px; background: #94a3b8; margin-bottom: 6px; }
        span { font-size: 0.75rem; color: #64748b; }
      }
    }

    .mt-20 { margin-top: 20px; }
    .mt-24 { margin-top: 24px; }
    .text-xs { font-size: 0.75rem; }
    .text-right { text-align: right; }

    @media print {
      .no-print { display: none !important; }
      .modal-backdrop { position: static; background: white; }
      .modal-content { box-shadow: none; border: none; max-width: 100%; }
      .print-area { padding: 0; }
    }
  `]
})
export class SalarySlipModalComponent implements OnInit {
  @Input() salaryRecordId: number = 1;
  @Output() close = new EventEmitter<void>();

  slip: SalarySlip | null = null;

  constructor(private salaryService: SalaryService) {}

  ngOnInit() {
    this.salaryService.generateSalarySlip(this.salaryRecordId).subscribe(s => {
      this.slip = s;
    });
  }

  printSlip() {
    window.print();
  }

  closeModal() {
    this.close.emit();
  }

  onBackdropClick(e: MouseEvent) {
    if ((e.target as HTMLElement).classList.contains('modal-backdrop')) {
      this.closeModal();
    }
  }
}
