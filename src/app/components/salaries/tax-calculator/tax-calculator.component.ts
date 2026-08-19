import { Component, OnInit } from '@angular/core';
import { MasterDataService } from '../../../core/services/master-data.service';
import { SalaryService } from '../../../core/services/salary.service';
import { CountryInfo, TaxCalculationResponse } from '../../../core/models/models';

@Component({
  selector: 'app-tax-calculator',
  template: `
    <div class="page-wrapper">
      <div class="page-header">
        <div>
          <h2>Multi-Country Tax Simulator</h2>
          <p class="text-muted">Test & simulate progressive income tax calculations across 9 countries with instant breakdown</p>
        </div>
      </div>

      <div class="simulator-grid">
        <!-- Input Controls Card -->
        <div class="glass-card">
          <div class="card-header">
            <div class="card-title">
              <i class="fa-solid fa-sliders text-primary"></i> Simulation Parameters
            </div>
          </div>

          <div class="form-group">
            <label>Select Country</label>
            <select class="form-control" [(ngModel)]="selectedCountry" (change)="onCountryChange()">
              <option *ngFor="let c of countries" [value]="c.name">
                {{ c.name }} ({{ c.currency }})
              </option>
            </select>
          </div>

          <div class="form-group">
            <div class="label-row">
              <label>Annual Base Salary ({{ currentCurrency }})</label>
              <span class="val-display font-bold">\${{ baseSalary | number }}</span>
            </div>
            <input
              type="range"
              class="range-slider"
              min="20000"
              max="350000"
              step="5000"
              [(ngModel)]="baseSalary"
              (input)="calculate()"
            />
            <input
              type="number"
              class="form-control mt-8"
              [(ngModel)]="baseSalary"
              (input)="calculate()"
            />
          </div>

          <div class="form-row">
            <div class="form-group flex-1">
              <label>Allowances</label>
              <input type="number" class="form-control" [(ngModel)]="allowances" (input)="calculate()" min="0" />
            </div>
            <div class="form-group flex-1">
              <label>Pre-Tax Deductions</label>
              <input type="number" class="form-control" [(ngModel)]="deductions" (input)="calculate()" min="0" />
            </div>
          </div>

          <!-- Preset Salary Buttons -->
          <div class="preset-group mt-16">
            <span class="preset-label">Quick Benchmarks:</span>
            <div class="preset-buttons">
              <button class="btn btn-secondary btn-sm" (click)="setPreset(60000)">\$60k (Junior)</button>
              <button class="btn btn-secondary btn-sm" (click)="setPreset(120000)">\$120k (Senior)</button>
              <button class="btn btn-secondary btn-sm" (click)="setPreset(180000)">\$180k (Staff/Lead)</button>
              <button class="btn btn-secondary btn-sm" (click)="setPreset(250000)">\$250k (Exec)</button>
            </div>
          </div>
        </div>

        <!-- Live Results Panel -->
        <div class="glass-card result-card" *ngIf="result">
          <div class="card-header">
            <div class="card-title">
              <i class="fa-solid fa-chart-pie text-success"></i> Calculation Breakdown
            </div>
            <span class="badge badge-success">{{ selectedCountry }}</span>
          </div>

          <!-- Net Take Home Highlight -->
          <div class="net-highlight">
            <div class="net-sub">ESTIMATED ANNUAL NET PAY</div>
            <div class="net-val">{{ currentCurrency }} \${{ result.netSalary | number }}</div>
            <div class="monthly-sub">
              Monthly Take-Home: <strong>\${{ (result.netSalary / 12) | number:'1.0-0' }} {{ currentCurrency }}</strong>
            </div>
          </div>

          <!-- Summary Metric Pills -->
          <div class="metric-row mt-16">
            <div class="metric-pill">
              <span class="m-lbl">Gross Salary</span>
              <span class="m-val font-bold">\${{ result.grossSalary | number }}</span>
            </div>
            <div class="metric-pill">
              <span class="m-lbl">Income Tax</span>
              <span class="m-val text-danger font-bold">\${{ result.totalTax | number }}</span>
            </div>
            <div class="metric-pill">
              <span class="m-lbl">Effective Rate</span>
              <span class="m-val text-primary font-bold">{{ result.effectiveTaxRate }}%</span>
            </div>
          </div>

          <!-- Progressive Tax Slabs Breakdown Table -->
          <div class="slabs-container mt-20" *ngIf="result.breakdown && result.breakdown.length > 0">
            <div class="slab-title">PROGRESSIVE SLAB COMPUTATION</div>
            <table class="data-table">
              <thead>
                <tr>
                  <th>Bracket Range</th>
                  <th>Rate</th>
                  <th>Taxable</th>
                  <th class="text-right">Tax Amount</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let b of result.breakdown">
                  <td>\${{ b.bracketFrom | number }} - {{ b.bracketTo ? ('$' + (b.bracketTo | number)) : 'Above' }}</td>
                  <td><span class="badge badge-neutral">{{ b.rate }}%</span></td>
                  <td>\${{ b.taxableAmountInBracket | number }}</td>
                  <td class="text-right font-bold text-danger">\${{ b.taxForBracket | number }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .page-header { margin-bottom: 24px; h2 { font-size: 1.5rem; font-weight: 800; } }
    .simulator-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 24px; }
    .label-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px; }
    .val-display { color: var(--primary); font-size: 1.1rem; }
    .range-slider {
      width: 100%;
      height: 6px;
      border-radius: 5px;
      background: #e2e8f0;
      outline: none;
      -webkit-appearance: none;
      accent-color: #4f46e5;
    }
    .form-row { display: flex; gap: 14px; }
    .flex-1 { flex: 1; }
    .mt-8 { margin-top: 8px; }
    .mt-16 { margin-top: 16px; }
    .mt-20 { margin-top: 20px; }
    .preset-group {
      .preset-label { font-size: 0.75rem; color: #64748b; font-weight: 600; margin-bottom: 6px; display: block; }
      .preset-buttons { display: flex; gap: 8px; flex-wrap: wrap; }
    }
    .net-highlight {
      background: #eef2ff;
      border-radius: 14px;
      padding: 20px;
      text-align: center;
      .net-sub { font-size: 0.75rem; font-weight: 800; color: #4f46e5; letter-spacing: 0.05em; }
      .net-val { font-family: 'Outfit', sans-serif; font-size: 2.2rem; font-weight: 800; color: #1e1b4b; margin: 4px 0; }
      .monthly-sub { font-size: 0.85rem; color: #64748b; }
    }
    .metric-row { display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px; }
    .metric-pill {
      background: #f8fafc;
      border: 1px solid #e2e8f0;
      border-radius: 10px;
      padding: 10px;
      text-align: center;
      .m-lbl { font-size: 0.7rem; color: #64748b; display: block; margin-bottom: 2px; }
      .m-val { font-size: 1rem; }
    }
    .slabs-container {
      .slab-title { font-size: 0.75rem; font-weight: 800; color: #64748b; margin-bottom: 8px; letter-spacing: 0.05em; }
    }
    .text-danger { color: #ef4444; }
    .text-primary { color: #4f46e5; }
    .text-success { color: #10b981; }
    .text-right { text-align: right; }
  `]
})
export class TaxCalculatorComponent implements OnInit {
  countries: CountryInfo[] = [];
  selectedCountry = 'United States';
  currentCurrency = 'USD';

  baseSalary = 120000;
  allowances = 12000;
  deductions = 4000;

  result: TaxCalculationResponse | null = null;

  constructor(
    private masterDataService: MasterDataService,
    private salaryService: SalaryService
  ) {}

  ngOnInit() {
    this.masterDataService.getCountries().subscribe(c => {
      this.countries = c;
      this.calculate();
    });
  }

  onCountryChange() {
    const c = this.countries.find(item => item.name === this.selectedCountry);
    if (c) this.currentCurrency = c.currency;
    this.calculate();
  }

  setPreset(amt: number) {
    this.baseSalary = amt;
    this.allowances = Math.round(amt * 0.1);
    this.deductions = Math.round(amt * 0.03);
    this.calculate();
  }

  calculate() {
    this.salaryService.calculateTaxPreview({
      baseSalary: this.baseSalary,
      allowances: this.allowances,
      deductions: this.deductions,
      country: this.selectedCountry,
      currency: this.currentCurrency
    }).subscribe(res => {
      this.result = res;
    });
  }
}
