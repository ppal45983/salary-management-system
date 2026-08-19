import { Component, OnInit, OnDestroy } from '@angular/core';
import { MasterDataService } from '../../../core/services/master-data.service';
import { SalaryService } from '../../../core/services/salary.service';
import { CurrencyConfig, CurrencyService } from '../../../core/services/currency.service';
import { CountryInfo, TaxCalculationResponse } from '../../../core/models/models';
import { Subscription } from 'rxjs';

interface BenchmarkPreset {
  label: string;
  amount: number;
}

@Component({
  selector: 'app-tax-calculator',
  template: `
    <div class="page-wrapper">
      <div class="page-header">
        <div>
          <h2>Multi-Country Progressive Tax Simulator</h2>
          <p class="text-muted">
            Official statutory progressive income tax calculations across 9 countries with bracket-by-bracket breakdown
          </p>
        </div>
      </div>

      <div class="simulator-grid">
        <!-- Input Controls Card -->
        <div class="glass-card">
          <div class="card-header">
            <div class="card-title">
              <i class="fa-solid fa-sliders text-primary"></i> Simulation Parameters
            </div>
            <span class="badge badge-info">{{ currentCurrency }} Tax System</span>
          </div>

          <!-- Country Selector -->
          <div class="form-group">
            <label>Select Jurisdiction / Country</label>
            <select class="form-control" [(ngModel)]="selectedCountry" (change)="onCountryChange()">
              <option *ngFor="let c of countries" [value]="c.name">
                {{ c.name }} ({{ c.currency }} • {{ c.symbol }})
              </option>
            </select>
          </div>

          <!-- India Tax Regime Selection Toggle (New vs Old) -->
          <div class="regime-toggle-box" *ngIf="selectedCountry === 'India'">
            <label class="regime-label">India Tax Regime Selection:</label>
            <div class="regime-buttons">
              <button
                type="button"
                class="regime-btn"
                [class.active]="selectedRegime === 'NEW'"
                (click)="setRegime('NEW')"
              >
                <i class="fa-solid fa-bolt"></i>
                <div class="regime-text">
                  <strong>New Tax Regime</strong>
                  <span class="regime-sub">Default (Slabs 5% to 30% u/s 115BAC)</span>
                </div>
              </button>

              <button
                type="button"
                class="regime-btn"
                [class.active]="selectedRegime === 'OLD'"
                (click)="setRegime('OLD')"
              >
                <i class="fa-solid fa-file-invoice"></i>
                <div class="regime-text">
                  <strong>Old Tax Regime</strong>
                  <span class="regime-sub">Traditional (With 80C/80D/HRA deductions)</span>
                </div>
              </button>
            </div>
          </div>

          <!-- Salary Slider & Numeric Input -->
          <div class="form-group mt-16">
            <div class="label-row">
              <label>Annual Base Gross Salary ({{ currentCurrency }})</label>
              <span class="val-display font-bold">{{ currentSymbol }}{{ baseSalary | number }}</span>
            </div>
            <input
              type="range"
              class="range-slider"
              [min]="sliderMin"
              [max]="sliderMax"
              [step]="sliderStep"
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

          <!-- Allowances & Deductions -->
          <div class="form-row">
            <div class="form-group flex-1">
              <label>Allowances ({{ currentSymbol }})</label>
              <input type="number" class="form-control" [(ngModel)]="allowances" (input)="calculate()" min="0" />
            </div>
            <div class="form-group flex-1">
              <label>
                Pre-Tax Deductions ({{ currentSymbol }})
                <span *ngIf="selectedCountry === 'India' && selectedRegime === 'OLD'" class="text-xs text-primary font-bold">(80C, 80D, HRA)</span>
              </label>
              <input type="number" class="form-control" [(ngModel)]="deductions" (input)="calculate()" min="0" />
            </div>
          </div>

          <!-- Dynamic Presets -->
          <div class="preset-group mt-16">
            <span class="preset-label">Standard Industry Benchmarks ({{ currentCountryName }}):</span>
            <div class="preset-buttons">
              <button
                *ngFor="let p of currentPresets"
                type="button"
                class="btn btn-secondary btn-sm"
                (click)="setPreset(p.amount)"
              >
                {{ p.label }}
              </button>
            </div>
          </div>
        </div>

        <!-- Live Results Panel -->
        <div class="glass-card result-card" *ngIf="result">
          <div class="card-header">
            <div>
              <div class="card-title">
                <i class="fa-solid fa-chart-pie text-success"></i>
                {{ selectedCountry }} Tax Breakdown
                <span *ngIf="selectedCountry === 'India'" class="regime-tag">({{ selectedRegime === 'NEW' ? 'New Regime' : 'Old Regime' }})</span>
              </div>
              <div class="card-subtitle">Calculated using official statutory progressive tax brackets</div>
            </div>
            <span class="badge badge-success">{{ selectedCountry }}</span>
          </div>

          <!-- Net Take Home Highlight -->
          <div class="net-highlight">
            <div class="net-sub">ESTIMATED ANNUAL NET TAKE-HOME</div>
            <div class="net-val">{{ currentSymbol }}{{ result.netSalary | number }} {{ currentCurrency }}</div>
            <div class="monthly-sub">
              Monthly Take-Home: <strong>{{ currentSymbol }}{{ (result.netSalary / 12) | number:'1.0-0' }} {{ currentCurrency }}</strong>
            </div>
          </div>

          <!-- Summary Metric Pills -->
          <div class="metric-row mt-16">
            <div class="metric-pill">
              <span class="m-lbl">Taxable Gross</span>
              <span class="m-val font-bold">{{ currentSymbol }}{{ result.grossSalary | number }}</span>
            </div>
            <div class="metric-pill">
              <span class="m-lbl">Total Statutory Tax</span>
              <span class="m-val text-danger font-bold">{{ currentSymbol }}{{ result.totalTax | number }}</span>
            </div>
            <div class="metric-pill">
              <span class="m-lbl">Effective Tax Rate</span>
              <span class="m-val text-primary font-bold">{{ result.effectiveTaxRate }}%</span>
            </div>
          </div>

          <!-- India New vs Old Tax Regime Comparison Widget -->
          <div class="comparison-banner mt-16" *ngIf="result.comparison && selectedCountry === 'India'">
            <div class="comp-header">
              <div class="comp-title">
                <i class="fa-solid fa-scale-balanced text-primary"></i> Regime Comparison & Tax Optimizer
              </div>
              <span class="badge" [ngClass]="result.comparison.recommendation === selectedRegime ? 'badge-success' : 'badge-warning'">
                Recommended: {{ result.comparison.recommendation === 'NEW' ? 'New Regime' : 'Old Regime' }}
              </span>
            </div>
            <div class="comp-grid">
              <div class="comp-col" [class.active-regime]="selectedRegime === 'NEW'">
                <div class="c-title">New Tax Regime</div>
                <div class="c-amount">₹{{ result.comparison.newRegimeTax | number }}</div>
                <div class="c-sub" *ngIf="selectedRegime === 'NEW'">✓ Currently Selected</div>
              </div>
              <div class="comp-col" [class.active-regime]="selectedRegime === 'OLD'">
                <div class="c-title">Old Tax Regime</div>
                <div class="c-amount">₹{{ result.comparison.oldRegimeTax | number }}</div>
                <div class="c-sub" *ngIf="selectedRegime === 'OLD'">✓ Currently Selected</div>
              </div>
            </div>
            <div class="savings-msg">
              <i class="fa-solid fa-circle-check text-success"></i>
              <span>{{ result.comparison.savingsMessage }}</span>
            </div>
          </div>

          <!-- Progressive Tax Slabs Breakdown Table -->
          <div class="slabs-container mt-20" *ngIf="result.breakdown && result.breakdown.length > 0">
            <div class="slab-title">
              PROGRESSIVE SLAB-BY-SLAB COMPUTATION ({{ currentCurrency }} • {{ selectedCountry === 'India' ? (selectedRegime + ' REGIME') : 'STANDARD' }})
            </div>
            <table class="data-table">
              <thead>
                <tr>
                  <th>Statutory Tax Slab</th>
                  <th>Rate</th>
                  <th>Taxable in Slab</th>
                  <th class="text-right">Tax Amount</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let b of result.breakdown">
                  <td>{{ currentSymbol }}{{ b.bracketFrom | number }} - {{ b.bracketTo ? (currentSymbol + (b.bracketTo | number)) : 'Above' }}</td>
                  <td><span class="badge badge-neutral font-bold">{{ b.rate }}%</span></td>
                  <td>{{ currentSymbol }}{{ b.taxableAmountInBracket | number }}</td>
                  <td class="text-right font-bold text-danger">{{ currentSymbol }}{{ b.taxForBracket | number }}</td>
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

    .regime-toggle-box {
      margin-top: 14px;
      padding: 12px;
      background: #f8fafc;
      border: 1px solid #e2e8f0;
      border-radius: 10px;

      .regime-label {
        font-size: 0.725rem;
        font-weight: 700;
        text-transform: uppercase;
        color: #475569;
        margin-bottom: 8px;
        display: block;
      }

      .regime-buttons {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 8px;
      }

      .regime-btn {
        display: flex;
        align-items: center;
        gap: 10px;
        padding: 8px 12px;
        background: #ffffff;
        border: 1.5px solid #cbd5e1;
        border-radius: 8px;
        cursor: pointer;
        text-align: left;
        transition: all 0.2s;

        i { font-size: 1.1rem; color: #64748b; }

        .regime-text {
          display: flex;
          flex-direction: column;
          strong { font-size: 0.8rem; color: #0f172a; }
          .regime-sub { font-size: 0.675rem; color: #64748b; }
        }

        &:hover {
          border-color: #94a3b8;
          background: #f8fafc;
        }

        &.active {
          border-color: #4f46e5;
          background: #eef2ff;
          i { color: #4f46e5; }
          strong { color: #4f46e5; }
        }
      }
    }

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
      .net-val { font-family: 'Outfit', sans-serif; font-size: 2rem; font-weight: 800; color: #1e1b4b; margin: 4px 0; }
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

    .comparison-banner {
      background: #f8fafc;
      border: 1px solid #c7d2fe;
      border-radius: 12px;
      padding: 14px;

      .comp-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 10px;
        .comp-title { font-size: 0.8rem; font-weight: 800; color: #0f172a; }
      }

      .comp-grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 10px;
      }

      .comp-col {
        background: #ffffff;
        border: 1px solid #e2e8f0;
        border-radius: 8px;
        padding: 8px 12px;
        text-align: center;

        .c-title { font-size: 0.7rem; font-weight: 700; color: #64748b; }
        .c-amount { font-family: 'Outfit', sans-serif; font-size: 1.15rem; font-weight: 800; color: #0f172a; margin: 2px 0; }
        .c-sub { font-size: 0.65rem; color: #4f46e5; font-weight: 700; }

        &.active-regime {
          border-color: #6366f1;
          background: #eef2ff;
          .c-title { color: #4f46e5; }
        }
      }

      .savings-msg {
        margin-top: 10px;
        font-size: 0.75rem;
        font-weight: 600;
        color: #166534;
        display: flex;
        align-items: center;
        gap: 6px;
        background: #f0fdf4;
        padding: 6px 10px;
        border-radius: 6px;
        border: 1px solid #bbf7d0;
      }
    }

    .regime-tag { font-size: 0.75rem; color: #4f46e5; font-weight: 700; }
    .slabs-container {
      .slab-title { font-size: 0.75rem; font-weight: 800; color: #64748b; margin-bottom: 8px; letter-spacing: 0.05em; }
    }
    .text-danger { color: #ef4444; }
    .text-primary { color: #4f46e5; }
    .text-success { color: #10b981; }
    .text-right { text-align: right; }
  `]
})
export class TaxCalculatorComponent implements OnInit, OnDestroy {
  countries: CountryInfo[] = [];
  selectedCountry = 'India';
  selectedRegime: 'NEW' | 'OLD' = 'NEW';
  currentCountryName = 'India';
  currentCurrency = 'INR';
  currentSymbol = '₹';

  baseSalary = 1500000;
  allowances = 150000;
  deductions = 150000; // Standard + 80C deductions for realistic simulation

  sliderMin = 300000;
  sliderMax = 7500000;
  sliderStep = 50000;

  currentPresets: BenchmarkPreset[] = [];
  result: TaxCalculationResponse | null = null;
  private currencySub: Subscription | null = null;

  constructor(
    private masterDataService: MasterDataService,
    private salaryService: SalaryService,
    private currencyService: CurrencyService
  ) {}

  ngOnInit() {
    this.masterDataService.getCountries().subscribe(c => {
      this.countries = c;
      const active = this.currencyService.currentCurrency;
      if (active.code === 'INR') this.selectedCountry = 'India';
      else if (active.code === 'GBP') this.selectedCountry = 'United Kingdom';
      else if (active.code === 'EUR') this.selectedCountry = 'Germany';
      else this.selectedCountry = 'United States';

      this.onCountryChange();
    });

    this.currencySub = this.currencyService.activeCurrency$.subscribe(curr => {
      if (this.countries.length > 0) {
        if (curr.code === 'INR') this.selectedCountry = 'India';
        else if (curr.code === 'GBP') this.selectedCountry = 'United Kingdom';
        else if (curr.code === 'EUR') this.selectedCountry = 'Germany';
        else if (curr.code === 'USD') this.selectedCountry = 'United States';
        this.onCountryChange();
      }
    });
  }

  ngOnDestroy() {
    this.currencySub?.unsubscribe();
  }

  setRegime(regime: 'NEW' | 'OLD') {
    this.selectedRegime = regime;
    this.calculate();
  }

  onCountryChange() {
    const c = this.countries.find(item => item.name === this.selectedCountry);
    this.currentCountryName = this.selectedCountry;

    if (this.selectedCountry === 'India') {
      this.currentCurrency = 'INR';
      this.currentSymbol = '₹';
      this.sliderMin = 300000;
      this.sliderMax = 7500000;
      this.sliderStep = 50000;
      this.baseSalary = 1500000;
      this.allowances = 150000;
      this.deductions = 150000;
      this.currentPresets = [
        { label: '₹6L (Junior)', amount: 600000 },
        { label: '₹15L (Senior)', amount: 1500000 },
        { label: '₹28L (Staff/Lead)', amount: 2800000 },
        { label: '₹50L (Exec)', amount: 5000000 }
      ];
    } else if (this.selectedCountry === 'Japan') {
      this.currentCurrency = 'JPY';
      this.currentSymbol = '¥';
      this.sliderMin = 1000000;
      this.sliderMax = 25000000;
      this.sliderStep = 200000;
      this.baseSalary = 6500000;
      this.allowances = 600000;
      this.deductions = 200000;
      this.currentPresets = [
        { label: '¥3.5M (Junior)', amount: 3500000 },
        { label: '¥6.5M (Senior)', amount: 6500000 },
        { label: '¥10M (Staff/Lead)', amount: 10000000 },
        { label: '¥18M (Exec)', amount: 18000000 }
      ];
    } else if (this.selectedCountry === 'United Kingdom') {
      this.currentCurrency = 'GBP';
      this.currentSymbol = '£';
      this.sliderMin = 15000;
      this.sliderMax = 250000;
      this.sliderStep = 5000;
      this.baseSalary = 65000;
      this.allowances = 6000;
      this.deductions = 2000;
      this.currentPresets = [
        { label: '£30k (Junior)', amount: 30000 },
        { label: '£65k (Senior)', amount: 65000 },
        { label: '£100k (Staff/Lead)', amount: 100000 },
        { label: '£160k (Exec)', amount: 160000 }
      ];
    } else if (this.selectedCountry === 'Germany' || this.selectedCountry === 'France') {
      this.currentCurrency = 'EUR';
      this.currentSymbol = '€';
      this.sliderMin = 15000;
      this.sliderMax = 300000;
      this.sliderStep = 5000;
      this.baseSalary = 75000;
      this.allowances = 7000;
      this.deductions = 2500;
      this.currentPresets = [
        { label: '€35k (Junior)', amount: 35000 },
        { label: '€75k (Senior)', amount: 75000 },
        { label: '€115k (Staff/Lead)', amount: 115000 },
        { label: '€180k (Exec)', amount: 180000 }
      ];
    } else {
      // US, Canada, Australia, Singapore
      this.currentCurrency = c ? c.currency : 'USD';
      this.currentSymbol = c ? c.symbol : '$';
      this.sliderMin = 20000;
      this.sliderMax = 400000;
      this.sliderStep = 5000;
      this.baseSalary = 120000;
      this.allowances = 12000;
      this.deductions = 4000;
      this.currentPresets = [
        { label: `${this.currentSymbol}60k (Junior)`, amount: 60000 },
        { label: `${this.currentSymbol}120k (Senior)`, amount: 120000 },
        { label: `${this.currentSymbol}180k (Staff/Lead)`, amount: 180000 },
        { label: `${this.currentSymbol}250k (Exec)`, amount: 250000 }
      ];
    }

    this.calculate();
  }

  setPreset(amt: number) {
    this.baseSalary = amt;
    this.allowances = Math.round(amt * 0.1);
    this.deductions = this.selectedCountry === 'India' && this.selectedRegime === 'OLD' ? 150000 : Math.round(amt * 0.03);
    this.calculate();
  }

  calculate() {
    this.salaryService.calculateTaxPreview({
      baseSalary: this.baseSalary,
      allowances: this.allowances,
      deductions: this.deductions,
      country: this.selectedCountry,
      currency: this.currentCurrency,
      regime: this.selectedCountry === 'India' ? this.selectedRegime : undefined
    }).subscribe(res => {
      this.result = res;
    });
  }
}
