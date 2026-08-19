import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-sidebar',
  template: `
    <aside class="sidebar">
      <!-- Brand Logo -->
      <div class="brand">
        <div class="brand-logo">
          <i class="fa-solid fa-vault"></i>
        </div>
        <div class="brand-text">
          <span class="brand-name">ACME</span>
          <span class="brand-tag">Compensation</span>
        </div>
      </div>

      <!-- Navigation Links -->
      <nav class="nav-menu">
        <div class="nav-section-title">MAIN MENU</div>
        
        <a routerLink="/dashboard" routerLinkActive="active" class="nav-item">
          <i class="fa-solid fa-chart-pie"></i>
          <span>Analytics Dashboard</span>
        </a>

        <a routerLink="/employees" routerLinkActive="active" class="nav-item">
          <i class="fa-solid fa-users"></i>
          <span>Employee Directory</span>
          <span class="nav-badge">10,000</span>
        </a>

        <a routerLink="/salaries" routerLinkActive="active" class="nav-item">
          <i class="fa-solid fa-money-bill-wave"></i>
          <span>Salary Records</span>
        </a>

        <a routerLink="/tax-calculator" routerLinkActive="active" class="nav-item">
          <i class="fa-solid fa-calculator"></i>
          <span>Tax Simulator</span>
          <span class="nav-badge-pill">Live</span>
        </a>

        <div class="nav-section-title">ADMINISTRATION</div>

        <a routerLink="/masters" routerLinkActive="active" class="nav-item">
          <i class="fa-solid fa-building-columns"></i>
          <span>Master Data & Tax Slabs</span>
        </a>
      </nav>

      <!-- Bottom Profile / Logout -->
      <div class="sidebar-footer">
        <div class="user-card">
          <div class="user-avatar">
            <i class="fa-solid fa-user-tie"></i>
          </div>
          <div class="user-info">
            <div class="user-name">HR Manager</div>
            <div class="user-role">Administrator</div>
          </div>
          <button class="logout-btn" (click)="logout()" title="Logout">
            <i class="fa-solid fa-arrow-right-from-bracket"></i>
          </button>
        </div>
      </div>
    </aside>
  `,
  styles: [`
    .sidebar {
      width: 260px;
      background: #0f172a;
      color: #94a3b8;
      display: flex;
      flex-direction: column;
      height: 100vh;
      border-right: 1px solid #1e293b;
      user-select: none;
      flex-shrink: 0;
    }

    .brand {
      padding: 24px 20px;
      display: flex;
      align-items: center;
      gap: 12px;
      border-bottom: 1px solid #1e293b;

      .brand-logo {
        width: 40px;
        height: 40px;
        background: linear-gradient(135deg, #4f46e5, #06b6d4);
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #ffffff;
        font-size: 1.25rem;
        box-shadow: 0 4px 12px rgba(79, 70, 229, 0.4);
      }

      .brand-text {
        display: flex;
        flex-direction: column;

        .brand-name {
          font-family: 'Outfit', sans-serif;
          font-weight: 800;
          font-size: 1.25rem;
          color: #ffffff;
          letter-spacing: -0.02em;
          line-height: 1.1;
        }
        .brand-tag {
          font-size: 0.7rem;
          color: #38bdf8;
          font-weight: 600;
          text-transform: uppercase;
          letter-spacing: 0.08em;
        }
      }
    }

    .nav-menu {
      flex: 1;
      padding: 20px 12px;
      overflow-y: auto;
      display: flex;
      flex-direction: column;
      gap: 4px;
    }

    .nav-section-title {
      font-size: 0.65rem;
      font-weight: 700;
      letter-spacing: 0.1em;
      color: #475569;
      padding: 12px 12px 6px;
      text-transform: uppercase;
    }

    .nav-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 10px 14px;
      border-radius: 8px;
      color: #94a3b8;
      text-decoration: none;
      font-size: 0.875rem;
      font-weight: 600;
      transition: all 0.2s ease;

      i {
        font-size: 1rem;
        width: 20px;
        text-align: center;
        color: #64748b;
        transition: color 0.2s ease;
      }

      &:hover {
        background: #1e293b;
        color: #ffffff;
        i { color: #38bdf8; }
      }

      &.active {
        background: #4f46e5;
        color: #ffffff;
        box-shadow: 0 4px 12px rgba(79, 70, 229, 0.35);
        i { color: #ffffff; }
      }
    }

    .nav-badge {
      margin-left: auto;
      font-size: 0.7rem;
      background: #1e293b;
      color: #38bdf8;
      padding: 2px 7px;
      border-radius: 999px;
      font-weight: 700;
    }

    .nav-badge-pill {
      margin-left: auto;
      font-size: 0.65rem;
      background: rgba(16, 185, 129, 0.2);
      color: #34d399;
      padding: 2px 7px;
      border-radius: 999px;
      font-weight: 700;
      text-transform: uppercase;
    }

    .sidebar-footer {
      padding: 16px;
      border-top: 1px solid #1e293b;
      background: #090e1a;
    }

    .user-card {
      display: flex;
      align-items: center;
      gap: 10px;

      .user-avatar {
        width: 36px;
        height: 36px;
        border-radius: 50%;
        background: #312e81;
        color: #818cf8;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 0.95rem;
      }

      .user-info {
        flex: 1;
        overflow: hidden;

        .user-name {
          color: #ffffff;
          font-size: 0.85rem;
          font-weight: 600;
          white-space: nowrap;
          text-overflow: ellipsis;
        }

        .user-role {
          font-size: 0.7rem;
          color: #64748b;
        }
      }

      .logout-btn {
        background: none;
        border: none;
        color: #64748b;
        cursor: pointer;
        padding: 6px;
        border-radius: 6px;
        transition: all 0.2s;

        &:hover {
          color: #ef4444;
          background: rgba(239, 68, 68, 0.1);
        }
      }
    }
  `]
})
export class SidebarComponent {
  constructor(private authService: AuthService, private router: Router) {}

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
