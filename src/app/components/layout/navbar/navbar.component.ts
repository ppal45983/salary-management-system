import { Component, Input } from '@angular/core';

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
        <!-- Global Status Tag -->
        <div class="status-pill">
          <span class="status-dot"></span>
          <span>10,000 Global Records</span>
        </div>

        <!-- Multi-country flag pills -->
        <div class="country-pills">
          <span class="flag-pill" title="United States">🇺🇸 USD</span>
          <span class="flag-pill" title="United Kingdom">🇬🇧 GBP</span>
          <span class="flag-pill" title="European Union">🇪🇺 EUR</span>
          <span class="flag-pill" title="India">🇮🇳 INR</span>
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
      gap: 16px;
    }

    .status-pill {
      display: flex;
      align-items: center;
      gap: 8px;
      background: #f0fdf4;
      border: 1px solid #bbf7d0;
      color: #166534;
      font-size: 0.775rem;
      font-weight: 700;
      padding: 6px 12px;
      border-radius: 999px;

      .status-dot {
        width: 7px;
        height: 7px;
        background: #22c55e;
        border-radius: 50%;
        box-shadow: 0 0 0 2px rgba(34, 197, 94, 0.3);
      }
    }

    .country-pills {
      display: flex;
      gap: 6px;

      .flag-pill {
        background: #f8fafc;
        border: 1px solid #e2e8f0;
        font-size: 0.725rem;
        font-weight: 600;
        color: #475569;
        padding: 4px 8px;
        border-radius: 6px;
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
export class NavbarComponent {
  @Input() title: string = 'Compensation Dashboard';
  @Input() subtitle: string = 'Real-time multi-country salary management & analytics';
}
