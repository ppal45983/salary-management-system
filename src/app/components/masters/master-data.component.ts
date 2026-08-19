import { Component, OnInit } from '@angular/core';
import { MasterDataService } from '../../core/services/master-data.service';
import { Department, Designation, TaxBracket } from '../../core/models/models';

@Component({
  selector: 'app-master-data',
  template: `
    <div class="page-wrapper">
      <div class="page-header">
        <div>
          <h2>Master Data & Tax Brackets</h2>
          <p class="text-muted">Explore organizational hierarchy, job levels, and country-specific tax rules</p>
        </div>
      </div>

      <!-- Navigation Tabs -->
      <div class="glass-card toolbar-card">
        <div class="status-tabs">
          <button class="tab-btn" [class.active]="activeTab === 'DEPTS'" (click)="activeTab = 'DEPTS'">
            <i class="fa-solid fa-building"></i> Departments (9)
          </button>
          <button class="tab-btn" [class.active]="activeTab === 'DESIGS'" (click)="activeTab = 'DESIGS'">
            <i class="fa-solid fa-id-badge"></i> Designations & Levels (14)
          </button>
          <button class="tab-btn" [class.active]="activeTab === 'TAX'" (click)="activeTab = 'TAX'">
            <i class="fa-solid fa-percent"></i> Country Tax Brackets
          </button>
        </div>
      </div>

      <!-- Tab 1: Departments -->
      <div class="glass-card mt-20" *ngIf="activeTab === 'DEPTS'">
        <div class="table-responsive">
          <table class="data-table">
            <thead>
              <tr>
                <th>Code</th>
                <th>Department Name</th>
                <th>Description</th>
                <th>Annual Budget</th>
                <th>Staff Headcount</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let d of departments">
                <td><span class="badge badge-neutral">{{ d.departmentCode }}</span></td>
                <td class="font-semibold">{{ d.name }}</td>
                <td class="text-muted">{{ d.description }}</td>
                <td>\${{ (d.budget || 0) / 1000000 | number:'1.1-1' }}M USD</td>
                <td><span class="badge badge-info">{{ d.employeeCount | number }} Employees</span></td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Tab 2: Designations -->
      <div class="glass-card mt-20" *ngIf="activeTab === 'DESIGS'">
        <div class="table-responsive">
          <table class="data-table">
            <thead>
              <tr>
                <th>Job Title</th>
                <th>Level</th>
                <th>Department</th>
                <th>Salary Band Range (USD)</th>
                <th>Active Employees</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let des of designations">
                <td class="font-semibold">{{ des.title }}</td>
                <td><span class="badge badge-neutral">{{ des.level }}</span></td>
                <td>{{ des.departmentName }}</td>
                <td>\${{ des.minSalary | number }} - \${{ des.maxSalary | number }}</td>
                <td>{{ des.employeeCount | number }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Tab 3: Tax Brackets -->
      <div class="glass-card mt-20" *ngIf="activeTab === 'TAX'">
        <div class="filter-bar mb-16">
          <label class="mr-8 font-semibold">Filter Country:</label>
          <select class="form-control inline-select" [(ngModel)]="taxCountryFilter" (change)="loadTaxBrackets()">
            <option value="">All Countries (9)</option>
            <option value="United States">United States (USD)</option>
            <option value="United Kingdom">United Kingdom (GBP)</option>
            <option value="India">India (INR)</option>
            <option value="Germany">Germany (EUR)</option>
          </select>
        </div>

        <div class="table-responsive">
          <table class="data-table">
            <thead>
              <tr>
                <th>Country</th>
                <th>Income Slab Bracket</th>
                <th>Tax Rate</th>
                <th>Currency</th>
                <th>Description</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let t of taxBrackets">
                <td class="font-semibold">{{ t.country }}</td>
                <td>{{ t.currency }} \${{ t.incomeFrom | number }} - {{ t.incomeTo ? ('$' + (t.incomeTo | number)) : 'Above' }}</td>
                <td><span class="badge badge-success font-bold">{{ t.taxRate }}%</span></td>
                <td>{{ t.currency }}</td>
                <td class="text-muted">{{ t.description || 'Standard Bracket' }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .page-header { margin-bottom: 20px; h2 { font-size: 1.5rem; font-weight: 800; } }
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
      &.active { background: #4f46e5; color: white; }
    }
    .mt-20 { margin-top: 20px; }
    .mb-16 { margin-bottom: 16px; }
    .mr-8 { margin-right: 8px; }
    .inline-select { width: auto; display: inline-block; min-width: 200px; }
  `]
})
export class MasterDataComponent implements OnInit {
  activeTab: 'DEPTS' | 'DESIGS' | 'TAX' = 'DEPTS';
  departments: Department[] = [];
  designations: Designation[] = [];
  taxBrackets: TaxBracket[] = [];
  taxCountryFilter = '';

  constructor(private masterDataService: MasterDataService) {}

  ngOnInit() {
    this.masterDataService.getDepartments().subscribe(d => this.departments = d);
    this.masterDataService.getDesignations().subscribe(d => this.designations = d);
    this.loadTaxBrackets();
  }

  loadTaxBrackets() {
    this.masterDataService.getTaxBrackets(this.taxCountryFilter || undefined).subscribe(t => {
      this.taxBrackets = t;
    });
  }
}
