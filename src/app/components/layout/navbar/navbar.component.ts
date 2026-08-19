import { Component, Input, OnInit } from '@angular/core';
import { CurrencyConfig, CurrencyService } from '../../../core/services/currency.service';

@Component({
  selector: 'app-navbar',
  template: `
    <header class="navbar">
      <div class="navbar-left">
        <div class="page-title-group">
          <h1 class="page-heading">{{ title }}</h1>
          <span class="page-sub">{{ subtitle }}</span>
        </div>
      </div>

      <div class="navbar-right">
        <!-- Live FX Exchange Rate Ticker Badge -->
        <div class="live-fx-pill" (click)="refreshRates()" title="Live Foreign Exchange Rates (Click to Refresh)">
          <span class="live-pulse"></span>
          <span class="fx-text">
            <i class="fa-solid fa-chart-line"></i>
            Live FX: <strong>$1 = ₹{{ liveStatus.inrRate | number:'1.2-2' }}</strong>
          </span>
          <button class="fx-refresh-btn" [class.fa-spin]="refreshing" title="Refresh Live Rates">
            <i class="fa-solid fa-arrows-rotate"></i>
          </button>
        </div>

        <!-- Interactive Multi-Currency Switcher -->
        <div class="currency-switcher">
          <span class="switcher-label">View In:</span>
          <div class="country-pills">
            <button
              *ngFor="let c of currencies"
              type="button"
              class="flag-pill"
              [class.active]="activeCurrency.code === c.code"
              (click)="onSelectCurrency(c.code)"
              [title]="'Switch entire dashboard to ' + c.name + ' (' + c.symbol + ') - Live Rate: 1 USD = ' + c.rateToUsd"
            >
              <span class="flag-icon">{{ c.flag }}</span>
              <span class="curr-code">{{ c.code }}</span>
            </button>
          </div>
        </div>

        <!-- Notifications -->
        <button class="nav-icon-btn" title="Pending Notifications">
          <i class="fa-regular fa-bell"></i>
          <span class="badge-dot"></span>
        </button>
      </div>
    </header>
  `,
  styles: [`
    .navbar {
      height: 70px;
      background: #ffffff;
      border-bottom: 1px solid var(--border-color);
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 32px;
      flex-shrink: 0;
    }

    .navbar-left {
      .page-title-group {
        display: flex;
        flex-direction: column;

        .page-heading {
          font-size: 1.35rem;
          font-weight: 800;
          color: #0f172a;
          line-height: 1.2;
        }

        .page-sub {
          font-size: 0.8rem;
          color: #64748b;
        }
      }
    }

    .navbar-right {
      display: flex;
      align-items: center;
      gap: 14px;
    }

    .live-fx-pill {
      display: flex;
      align-items: center;
      gap: 8px;
      background: #f0fdf4;
      border: 1px solid #bbf7d0;
      color: #166534;
      font-size: 0.775rem;
      font-weight: 600;
      padding: 6px 12px;
      border-radius: 999px;
      cursor: pointer;
      user-select: none;
      transition: all 0.2s;

      &:hover {
        background: #dcfce7;
        border-color: #86efac;
      }

      .live-pulse {
        width: 7px;
        height: 7px;
        background: #22c55e;
        border-radius: 50%;
        box-shadow: 0 0 0 2px rgba(34, 197, 94, 0.35);
        animation: pulse 2s infinite;
      }

      .fx-text {
        display: flex;
        align-items: center;
        gap: 6px;
        strong { color: #14532d; font-weight: 800; }
      }

      .fx-refresh-btn {
        background: none;
        border: none;
        color: #166534;
        cursor: pointer;
        padding: 0 2px;
        font-size: 0.75rem;
        display: flex;
        align-items: center;
      }
    }

    @keyframes pulse {
      0%, 100% { transform: scale(1); opacity: 1; }
      50% { transform: scale(1.3); opacity: 0.7; }
    }

    .currency-switcher {
      display: flex;
      align-items: center;
      gap: 8px;
      background: #f8fafc;
      padding: 4px 6px 4px 10px;
      border-radius: 10px;
      border: 1px solid #e2e8f0;

      .switcher-label {
        font-size: 0.725rem;
        font-weight: 700;
        color: #64748b;
        text-transform: uppercase;
        letter-spacing: 0.04em;
      }
    }

    .country-pills {
      display: flex;
      gap: 4px;

      .flag-pill {
        display: inline-flex;
        align-items: center;
        gap: 4px;
        background: #ffffff;
        border: 1px solid #cbd5e1;
        font-size: 0.75rem;
        font-weight: 700;
        color: #475569;
        padding: 5px 10px;
        border-radius: 7px;
        cursor: pointer;
        transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1);

        .flag-icon { font-size: 0.85rem; }
        .curr-code { font-family: var(--font-primary); font-size: 0.725rem; }

        &:hover {
          background: #f1f5f9;
          color: #0f172a;
          border-color: #94a3b8;
          transform: translateY(-1px);
        }

        &.active {
          background: #4f46e5;
          color: #ffffff;
          border-color: #4338ca;
          box-shadow: 0 2px 8px rgba(79, 70, 229, 0.35);
        }
      }
    }

    .nav-icon-btn {
      width: 38px;
      height: 38px;
      border-radius: 10px;
      border: 1px solid var(--border-color);
      background: #ffffff;
      color: #64748b;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      position: relative;
      transition: var(--transition);

      &:hover {
        background: var(--bg-app);
        color: var(--primary);
        border-color: #cbd5e1;
      }

      .badge-dot {
        position: absolute;
        top: 8px;
        right: 8px;
        width: 8px;
        height: 8px;
        background: #ef4444;
        border-radius: 50%;
        border: 2px solid #ffffff;
      }
    }
  `]
})
export class NavbarComponent implements OnInit {
  @Input() title: string = 'Compensation Dashboard';
  @Input() subtitle: string = 'Real-time multi-country salary management & analytics';

  currencies: CurrencyConfig[] = [];
  activeCurrency: CurrencyConfig;
  liveStatus = { isLive: false, lastUpdated: 'Just now', inrRate: 95.76 };
  refreshing = false;

  constructor(private currencyService: CurrencyService) {
    this.currencies = this.currencyService.currencies.slice(0, 4); // USD, INR, GBP, EUR
    this.activeCurrency = this.currencyService.currentCurrency;
  }

  ngOnInit() {
    this.currencyService.activeCurrency$.subscribe(curr => {
      this.activeCurrency = curr;
    });

    this.currencyService.liveStatus$.subscribe(status => {
      this.liveStatus = status;
      this.refreshing = false;
    });
  }

  onSelectCurrency(code: string) {
    this.currencyService.setCurrency(code);
  }

  refreshRates() {
    this.refreshing = true;
    this.currencyService.fetchLiveExchangeRates();
  }
}
