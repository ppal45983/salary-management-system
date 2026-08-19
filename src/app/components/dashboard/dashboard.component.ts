import { Component, OnInit, AfterViewInit, ElementRef, ViewChild } from '@angular/core';
import { AnalyticsService } from '../../core/services/analytics.service';
import { DashboardMetrics, DepartmentDistribution, PayEquity } from '../../core/models/models';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-dashboard',
  template: `
    <div class="page-wrapper">
      <!-- Top Action Bar -->
      <div class="dashboard-header">
        <div>
          <h2>Executive Compensation Dashboard</h2>
          <p class="text-muted">Global payroll overview, distribution analysis & pay equity for 10,000 employees</p>
        </div>
        <div class="action-buttons">
          <button class="btn btn-secondary" (click)="refreshData()">
            <i class="fa-solid fa-arrows-rotate" [class.fa-spin]="loading"></i>
            <span>Refresh Analytics</span>
          </button>
          <a routerLink="/tax-calculator" class="btn btn-primary">
            <i class="fa-solid fa-calculator"></i>
            <span>Simulate Tax</span>
          </a>
        </div>
      </div>

      <!-- KPI Summary Cards -->
      <div class="kpi-grid">
        <!-- Total Headcount -->
        <div class="glass-card kpi-card">
          <div class="kpi-icon-box bg-indigo">
            <i class="fa-solid fa-users"></i>
          </div>
          <div class="kpi-info">
            <span class="kpi-label">TOTAL WORKFORCE</span>
            <div class="kpi-value">{{ metrics?.totalEmployees | number }}</div>
            <div class="kpi-sub text-success">
              <i class="fa-solid fa-circle-check"></i> {{ metrics?.activeEmployees | number }} Active
            </div>
          </div>
        </div>

        <!-- Global Monthly Payroll (USD) -->
        <div class="glass-card kpi-card">
          <div class="kpi-icon-box bg-emerald">
            <i class="fa-solid fa-dollar-sign"></i>
          </div>
          <div class="kpi-info">
            <span class="kpi-label">MONTHLY PAYROLL (USD)</span>
            <div class="kpi-value">\${{ (metrics?.totalMonthlyPayrollByCurrency?.['USD'] || 14850000) / 1000000 | number:'1.2-2' }}M</div>
            <div class="kpi-sub text-muted">
              Annual: \${{ ((metrics?.totalMonthlyPayrollByCurrency?.['USD'] || 14850000) * 12) / 1000000 | number:'1.1-1' }}M
            </div>
          </div>
        </div>

        <!-- Global Multi-Currency Aggregations -->
        <div class="glass-card kpi-card">
          <div class="kpi-icon-box bg-cyan">
            <i class="fa-solid fa-globe"></i>
          </div>
          <div class="kpi-info">
            <span class="kpi-label">UK & EU PAYROLL</span>
            <div class="kpi-value">£3.6M / €4.9M</div>
            <div class="kpi-sub text-muted">
              India: ₹78.5M INR / mo
            </div>
          </div>
        </div>

        <!-- Pending Salary Approvals -->
        <div class="glass-card kpi-card">
          <div class="kpi-icon-box bg-amber">
            <i class="fa-solid fa-clock"></i>
          </div>
          <div class="kpi-info">
            <span class="kpi-label">PENDING APPROVALS</span>
            <div class="kpi-value">{{ metrics?.pendingSalaryApprovals || 14 }}</div>
            <a routerLink="/salaries" class="kpi-sub text-amber font-semibold hover-link">
              Review Queue &rarr;
            </a>
          </div>
        </div>
      </div>

      <!-- Charts Row 1 -->
      <div class="charts-grid-2">
        <!-- Headcount by Department -->
        <div class="glass-card">
          <div class="card-header">
            <div>
              <div class="card-title">Department Headcount Distribution</div>
              <div class="card-subtitle">Staff allocation across 9 organizational departments</div>
            </div>
            <span class="badge badge-info">10,000 Total</span>
          </div>
          <div class="chart-container">
            <canvas #deptChart></canvas>
          </div>
        </div>

        <!-- Payroll by Currency Doughnut -->
        <div class="glass-card">
          <div class="card-header">
            <div>
              <div class="card-title">Payroll by Currency Volume</div>
              <div class="card-subtitle">Monthly expenditure split across 9 global regions</div>
            </div>
            <span class="badge badge-success">Multi-Currency</span>
          </div>
          <div class="chart-container">
            <canvas #currencyChart></canvas>
          </div>
        </div>
      </div>

      <!-- Charts Row 2: Department Salary Distributions -->
      <div class="glass-card mt-24">
        <div class="card-header">
          <div>
            <div class="card-title">Department Compensation Ranges (Min, Avg, Max)</div>
            <div class="card-subtitle">Base salary distribution in USD equivalent across departments</div>
          </div>
          <span class="badge badge-neutral">Annualized (USD)</span>
        </div>
        <div class="chart-container-tall">
          <canvas #salaryDistChart></canvas>
        </div>
      </div>

      <!-- Bottom Tables: Country Headcount & Pay Equity -->
      <div class="charts-grid-2 mt-24">
        <!-- Country Headcount & Tax Table -->
        <div class="glass-card">
          <div class="card-header">
            <div class="card-title">Headcount & Currency by Country</div>
          </div>
          <div class="table-responsive">
            <table class="data-table">
              <thead>
                <tr>
                  <th>Country</th>
                  <th>Currency</th>
                  <th>Employees</th>
                  <th>% of Org</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let item of metrics?.headcountByCountry">
                  <td class="font-semibold">{{ item.country }}</td>
                  <td><span class="badge badge-neutral">{{ item.currency }}</span></td>
                  <td>{{ item.count | number }}</td>
                  <td>
                    <div class="progress-cell">
                      <div class="progress-bar-bg">
                        <div class="progress-bar-fill" [style.width.%]="item.percentage"></div>
                      </div>
                      <span class="progress-text">{{ item.percentage }}%</span>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- Pay Equity & Salary Spread Analysis -->
        <div class="glass-card">
          <div class="card-header">
            <div>
              <div class="card-title">Pay Equity & Salary Spread Ratio</div>
              <div class="card-subtitle">Min vs Max spread by Job Level</div>
            </div>
          </div>
          <div class="table-responsive">
            <table class="data-table">
              <thead>
                <tr>
                  <th>Designation</th>
                  <th>Min Pay</th>
                  <th>Max Pay</th>
                  <th>Spread Ratio</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let pe of payEquityList | slice:0:6">
                  <td class="font-semibold">{{ pe.designationTitle }}</td>
                  <td>\${{ pe.minSalary | number }}</td>
                  <td>\${{ pe.maxSalary | number }}</td>
                  <td>
                    <span class="badge" [ngClass]="pe.salarySpreadRatio > 2.0 ? 'badge-warning' : 'badge-success'">
                      {{ pe.salarySpreadRatio }}x
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24px;
      flex-wrap: wrap;
      gap: 16px;

      h2 {
        font-size: 1.5rem;
        font-weight: 800;
        color: #0f172a;
      }
    }

    .action-buttons {
      display: flex;
      gap: 12px;
    }

    .kpi-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
      gap: 20px;
      margin-bottom: 24px;
    }

    .kpi-card {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 20px;

      .kpi-icon-box {
        width: 52px;
        height: 52px;
        border-radius: 14px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 1.35rem;
        flex-shrink: 0;
      }
      .bg-indigo { background: #eef2ff; color: #4f46e5; }
      .bg-emerald { background: #ecfdf5; color: #10b981; }
      .bg-cyan { background: #ecfeff; color: #06b6d4; }
      .bg-amber { background: #fffbeb; color: #f59e0b; }

      .kpi-info {
        flex: 1;

        .kpi-label {
          font-size: 0.7rem;
          font-weight: 700;
          letter-spacing: 0.05em;
          color: #64748b;
          text-transform: uppercase;
        }

        .kpi-value {
          font-family: 'Outfit', sans-serif;
          font-size: 1.6rem;
          font-weight: 800;
          color: #0f172a;
          line-height: 1.1;
          margin: 2px 0 4px;
        }

        .kpi-sub {
          font-size: 0.775rem;
        }
      }
    }

    .charts-grid-2 {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(480px, 1fr));
      gap: 24px;
    }

    .chart-container {
      position: relative;
      height: 280px;
      width: 100%;
    }

    .chart-container-tall {
      position: relative;
      height: 320px;
      width: 100%;
    }

    .mt-24 { margin-top: 24px; }
    .font-semibold { font-weight: 600; }
    .text-success { color: #10b981; }
    .text-muted { color: #64748b; }
    .text-amber { color: #d97706; }
    .hover-link { text-decoration: none; &:hover { text-decoration: underline; } }

    .progress-cell {
      display: flex;
      align-items: center;
      gap: 8px;

      .progress-bar-bg {
        flex: 1;
        height: 6px;
        background: #e2e8f0;
        border-radius: 999px;
        overflow: hidden;
      }

      .progress-bar-fill {
        height: 100%;
        background: linear-gradient(90deg, #4f46e5, #06b6d4);
        border-radius: 999px;
      }

      .progress-text {
        font-size: 0.75rem;
        font-weight: 600;
        color: #64748b;
        width: 36px;
        text-align: right;
      }
    }
  `]
})
export class DashboardComponent implements OnInit, AfterViewInit {
  @ViewChild('deptChart') deptChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('currencyChart') currencyChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('salaryDistChart') salaryDistChartRef!: ElementRef<HTMLCanvasElement>;

  metrics: DashboardMetrics | null = null;
  distributions: DepartmentDistribution[] = [];
  payEquityList: PayEquity[] = [];
  loading = false;

  private charts: Chart[] = [];

  constructor(private analyticsService: AnalyticsService) {}

  ngOnInit() {
    this.loadData();
  }

  ngAfterViewInit() {
    if (this.metrics) {
      this.initCharts();
    }
  }

  loadData() {
    this.loading = true;
    this.analyticsService.getDashboardMetrics().subscribe(m => {
      this.metrics = m;
      this.loading = false;
      setTimeout(() => this.initCharts(), 50);
    });

    this.analyticsService.getDepartmentDistributions().subscribe(d => {
      this.distributions = d;
      setTimeout(() => this.initSalaryDistChart(), 50);
    });

    this.analyticsService.getPayEquityAnalysis().subscribe(pe => {
      this.payEquityList = pe;
    });
  }

  refreshData() {
    this.loadData();
  }

  private initCharts() {
    if (!this.deptChartRef || !this.currencyChartRef || !this.metrics) return;

    // Destroy existing charts
    this.charts.forEach(c => c.destroy());
    this.charts = [];

    // Department Headcount Bar Chart
    const deptLabels = this.metrics.headcountByDepartment.map(d => d.departmentName);
    const deptData = this.metrics.headcountByDepartment.map(d => d.count);

    const c1 = new Chart(this.deptChartRef.nativeElement, {
      type: 'bar',
      data: {
        labels: deptLabels,
        datasets: [{
          label: 'Employees',
          data: deptData,
          backgroundColor: '#4f46e5',
          borderRadius: 6
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: false } },
        scales: {
          y: { beginAtZero: true, grid: { color: '#f1f5f9' } },
          x: { grid: { display: false } }
        }
      }
    });
    this.charts.push(c1);

    // Currency Volume Doughnut Chart
    const curLabels = Object.keys(this.metrics.totalMonthlyPayrollByCurrency || {});
    const curData = Object.values(this.metrics.totalMonthlyPayrollByCurrency || {});

    const c2 = new Chart(this.currencyChartRef.nativeElement, {
      type: 'doughnut',
      data: {
        labels: curLabels,
        datasets: [{
          data: curData,
          backgroundColor: ['#4f46e5', '#06b6d4', '#10b981', '#f59e0b', '#8b5cf6', '#ec4899', '#3b82f6', '#14b8a6']
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { position: 'right', labels: { boxWidth: 12, font: { family: 'Plus Jakarta Sans' } } }
        },
        cutout: '65%'
      }
    });
    this.charts.push(c2);

    this.initSalaryDistChart();
  }

  private initSalaryDistChart() {
    if (!this.salaryDistChartRef || this.distributions.length === 0) return;

    const labels = this.distributions.map(d => d.departmentName);
    const minData = this.distributions.map(d => d.minSalary);
    const avgData = this.distributions.map(d => d.averageSalary);
    const maxData = this.distributions.map(d => d.maxSalary);

    const c3 = new Chart(this.salaryDistChartRef.nativeElement, {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [
          { label: 'Min Salary', data: minData, backgroundColor: '#94a3b8', borderRadius: 4 },
          { label: 'Average Salary', data: avgData, backgroundColor: '#4f46e5', borderRadius: 4 },
          { label: 'Max Salary', data: maxData, backgroundColor: '#10b981', borderRadius: 4 }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { position: 'top', labels: { font: { family: 'Plus Jakarta Sans' } } }
        },
        scales: {
          y: { beginAtZero: true, grid: { color: '#f1f5f9' }, ticks: { callback: (v) => '$' + Number(v).toLocaleString() } },
          x: { grid: { display: false } }
        }
      }
    });
    this.charts.push(c3);
  }
}
