import { Component, OnInit, OnDestroy } from '@angular/core';
import { MasterDataService } from '../../core/services/master-data.service';
import { CurrencyConfig, CurrencyService } from '../../core/services/currency.service';
import { Department, Designation, TaxBracket } from '../../core/models/models';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-master-data',
  template: `
    <div class="page-wrapper">
      <div class="page-header">
        <div>
          <h2>Master Data & Statutory Tax Brackets</h2>
          <p class="text-muted">
            Explore organizational hierarchy, job levels, and country-specific tax rules across all 9 jurisdictions
            <span class="active-currency-badge">
              Viewing in {{ activeCurrency.flag }} {{ activeCurrency.code }} ({{ activeCurrency.symbol }}) • Rate: {{ activeCurrency.rateToUsd }}
            </span>
          </p>
        </div>
      </div>

      <!-- Navigation Tabs -->
      <div class="glass-card toolbar-card">
        <div class="status-tabs">
          <button class="tab-btn" [class.active]="activeTab === 'DEPTS'" (click)="activeTab = 'DEPTS'">
            <i class="fa-solid fa-building"></i> Departments ({{ departments.length }})
          </button>
          <button class="tab-btn" [class.active]="activeTab === 'DESIGS'" (click)="activeTab = 'DESIGS'">
            <i class="fa-solid fa-id-badge"></i> Designations & Levels ({{ designations.length }})
          </button>
          <button class="tab-btn" [class.active]="activeTab === 'TAX'" (click)="activeTab = 'TAX'">
            <i class="fa-solid fa-percent"></i> Statutory Tax Slabs
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
                <th>Annual Budget ({{ activeCurrency.code }})</th>
                <th>Staff Headcount</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let d of departments">
                <td><span class="badge badge-neutral">{{ d.departmentCode }}</span></td>
                <td class="font-semibold">{{ d.name }}</td>
                <td class="text-muted">{{ d.description }}</td>
                <td class="font-bold text-primary">
                  {{ formatBudget(d.budget) }}
                </td>
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
                <th>Salary Band Range ({{ activeCurrency.code }})</th>
                <th>Active Employees</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let des of designations">
                <td class="font-semibold">{{ des.title }}</td>
                <td><span class="badge badge-neutral">{{ des.level }}</span></td>
                <td>{{ des.departmentName }}</td>
                <td class="font-bold text-primary">
                  {{ formatSalaryRange(des.minSalary, des.maxSalary) }}
                </td>
                <td>{{ des.employeeCount | number }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Tab 3: Tax Brackets -->
      <div class="glass-card mt-20" *ngIf="activeTab === 'TAX'">
        <div class="filter-bar mb-16">
          <label class="mr-8 font-semibold">Filter Jurisdiction / Country:</label>
          <select class="form-control inline-select" [(ngModel)]="taxCountryFilter" (change)="loadTaxBrackets()">
            <option value="">All Jurisdictions (All Slabs)</option>
            <option value="India">🇮🇳 India (New Regime - INR ₹)</option>
            <option value="India (Old Regime)">🇮🇳 India (Old Regime - INR ₹)</option>
            <option value="United States">🇺🇸 United States (USD $)</option>
            <option value="United Kingdom">🇬🇧 United Kingdom (GBP £)</option>
            <option value="Germany">🇩🇪 Germany (EUR €)</option>
            <option value="France">🇫🇷 France (EUR €)</option>
            <option value="Canada">🇨🇦 Canada (CAD C$)</option>
            <option value="Australia">🇦🇺 Australia (AUD A$)</option>
            <option value="Japan">🇯🇵 Japan (JPY ¥)</option>
            <option value="Singapore">🇸🇬 Singapore (SGD S$)</option>
          </select>
        </div>

        <div class="table-responsive">
          <table class="data-table">
            <thead>
              <tr>
                <th>Jurisdiction</th>
                <th>Income Slab Bracket (Native Currency)</th>
                <th>Tax Rate</th>
                <th>Currency</th>
                <th>Statutory Description</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let t of taxBrackets">
                <td class="font-semibold">
                  <span class="country-pill">{{ t.country }}</span>
                </td>
                <td class="slab-cell">
                  <span class="slab-from font-bold">{{ getCurrencySymbol(t.currency) }}{{ t.incomeFrom | number }}</span>
                  <span class="text-muted"> — </span>
                  <span class="slab-to font-bold" *ngIf="t.incomeTo">{{ getCurrencySymbol(t.currency) }}{{ t.incomeTo | number }}</span>
                  <span class="slab-to badge badge-neutral" *ngIf="!t.incomeTo || t.incomeTo > 900000000">Above</span>
                </td>
                <td><span class="badge badge-success font-bold">{{ t.taxRate }}%</span></td>
                <td><span class="badge badge-neutral font-bold">{{ t.currency }}</span></td>
                <td class="text-muted">{{ t.description || 'Standard Progressive Slab' }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .page-header { margin-bottom: 20px; h2 { font-size: 1.5rem; font-weight: 800; } }
    .active-currency-badge {
      display: inline-block;
      margin-left: 10px;
      padding: 3px 10px;
      border-radius: 20px;
      background: #eef2ff;
      border: 1px solid #c7d2fe;
      color: #4f46e5;
      font-size: 0.775rem;
      font-weight: 700;
    }
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
    .inline-select { width: auto; display: inline-block; min-width: 260px; font-weight: 600; }
    .text-primary { color: #4f46e5; }
    .country-pill {
      display: inline-block;
      padding: 3px 8px;
      border-radius: 6px;
      background: #f1f5f9;
      font-size: 0.85rem;
      color: #0f172a;
    }
    .slab-cell {
      font-family: 'Outfit', sans-serif;
      font-size: 0.95rem;
      .slab-from { color: #0f172a; }
      .slab-to { color: #0f172a; }
    }
  `]
})
export class MasterDataComponent implements OnInit, OnDestroy {
  activeTab: 'DEPTS' | 'DESIGS' | 'TAX' = 'DEPTS';
  departments: Department[] = [];
  designations: Designation[] = [];
  taxBrackets: TaxBracket[] = [];
  taxCountryFilter = '';

  activeCurrency!: CurrencyConfig;
  private currencySub: Subscription | null = null;

  constructor(
    private masterDataService: MasterDataService,
    private currencyService: CurrencyService
  ) {}

  ngOnInit() {
    this.activeCurrency = this.currencyService.currentCurrency;
    this.currencySub = this.currencyService.activeCurrency$.subscribe(curr => {
      this.activeCurrency = curr;
    });

    this.masterDataService.getDepartments().subscribe(d => this.departments = d);
    this.masterDataService.getDesignations().subscribe(d => this.designations = d);
    this.loadTaxBrackets();
  }

  ngOnDestroy() {
    this.currencySub?.unsubscribe();
  }

  loadTaxBrackets() {
    this.masterDataService.getTaxBrackets(this.taxCountryFilter || undefined).subscribe(t => {
      this.taxBrackets = t;
    });
  }

  formatBudget(usdAmount?: number): string {
    if (!usdAmount) return '—';
    const converted = this.currencyService.convertFromUsd(usdAmount);
    const symbol = this.activeCurrency.symbol;

    if (this.activeCurrency.code === 'INR') {
      if (converted >= 10000000) {
        return `${symbol}${(converted / 10000000).toFixed(2)} Cr INR`;
      } else if (converted >= 100000) {
        return `${symbol}${(converted / 100000).toFixed(1)} Lakh INR`;
      }
    } else {
      if (converted >= 1000000) {
        return `${symbol}${(converted / 1000000).toFixed(2)}M ${this.activeCurrency.code}`;
      } else if (converted >= 1000) {
        return `${symbol}${(converted / 1000).toFixed(1)}k ${this.activeCurrency.code}`;
      }
    }
    return `${symbol}${converted.toLocaleString()} ${this.activeCurrency.code}`;
  }

  formatSalaryRange(minUsd?: number, maxUsd?: number): string {
    if (!minUsd || !maxUsd) return '—';
    const minConv = this.currencyService.convertFromUsd(minUsd);
    const maxConv = this.currencyService.convertFromUsd(maxUsd);
    const symbol = this.activeCurrency.symbol;

    return `${symbol}${minConv.toLocaleString()} — ${symbol}${maxConv.toLocaleString()} ${this.activeCurrency.code}`;
  }

  getCurrencySymbol(currency?: string): string {
    switch (currency) {
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
