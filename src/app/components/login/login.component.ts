import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { NotificationService } from '../../core/services/notification.service';

@Component({
  selector: 'app-login',
  template: `
    <div class="login-wrapper">
      <div class="login-decor">
        <div class="glow-orb orb-1"></div>
        <div class="glow-orb orb-2"></div>
      </div>

      <div class="login-card">
        <!-- Logo & Header -->
        <div class="login-header">
          <div class="brand-badge">
            <i class="fa-solid fa-vault"></i>
          </div>
          <h2 class="title">ACME Salary Platform</h2>
          <p class="subtitle">Global Employee Compensation & Tax Engine (10k Headcount)</p>
        </div>

        <!-- Form -->
        <form (ngSubmit)="onSubmit()" class="login-form">
          <div class="form-group">
            <label for="username">Username or Email</label>
            <div class="input-with-icon">
              <i class="fa-regular fa-envelope"></i>
              <input
                id="username"
                type="text"
                class="form-control"
                placeholder="e.g. hr_manager@acme.com"
                [(ngModel)]="username"
                name="username"
                required
              />
            </div>
          </div>

          <div class="form-group">
            <label for="password">Password</label>
            <div class="input-with-icon">
              <i class="fa-solid fa-lock"></i>
              <input
                id="password"
                type="password"
                class="form-control"
                placeholder="Enter password"
                [(ngModel)]="password"
                name="password"
                required
              />
            </div>
          </div>

          <button type="submit" class="btn btn-primary btn-block" [disabled]="loading">
            <i *ngIf="loading" class="fa-solid fa-spinner fa-spin"></i>
            <span>{{ loading ? 'Authenticating...' : 'Sign In to Dashboard' }}</span>
          </button>
        </form>

        <!-- Quick Demo Preset Button -->
        <div class="demo-box">
          <div class="demo-title">
            <i class="fa-solid fa-bolt"></i> Quick Demo Access
          </div>
          <p class="demo-desc">Click below to auto-fill HR Manager credentials</p>
          <button type="button" class="btn btn-secondary btn-sm btn-block" (click)="fillDemoCredentials()">
            <span>Auto-Fill: hr_manager / admin123</span>
          </button>
        </div>

        <div class="login-footer">
          <span>Enterprise Grade • JWT Security • Multi-Country Tax</span>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .login-wrapper {
      min-height: 100vh;
      width: 100vw;
      display: flex;
      align-items: center;
      justify-content: center;
      background: #0f172a;
      position: relative;
      overflow: hidden;
      padding: 24px;
    }

    .login-decor {
      position: absolute;
      inset: 0;
      pointer-events: none;

      .glow-orb {
        position: absolute;
        border-radius: 50%;
        filter: blur(120px);
        opacity: 0.35;
      }
      .orb-1 {
        width: 500px;
        height: 500px;
        background: #4f46e5;
        top: -100px;
        left: -100px;
      }
      .orb-2 {
        width: 450px;
        height: 450px;
        background: #06b6d4;
        bottom: -100px;
        right: -100px;
      }
    }

    .login-card {
      position: relative;
      z-index: 10;
      width: 100%;
      max-width: 460px;
      background: rgba(30, 41, 59, 0.85);
      backdrop-filter: blur(16px);
      border: 1px solid rgba(255, 255, 255, 0.1);
      border-radius: 24px;
      padding: 36px 32px;
      box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
      color: #ffffff;
    }

    .login-header {
      text-align: center;
      margin-bottom: 28px;

      .brand-badge {
        width: 54px;
        height: 54px;
        background: linear-gradient(135deg, #4f46e5, #06b6d4);
        border-radius: 16px;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        font-size: 1.6rem;
        color: #ffffff;
        margin-bottom: 16px;
        box-shadow: 0 8px 20px rgba(79, 70, 229, 0.4);
      }

      .title {
        font-size: 1.5rem;
        font-weight: 800;
        color: #ffffff;
        margin-bottom: 6px;
      }

      .subtitle {
        font-size: 0.825rem;
        color: #94a3b8;
      }
    }

    .login-form {
      display: flex;
      flex-direction: column;
      gap: 18px;

      .form-group {
        margin-bottom: 0;

        label {
          color: #cbd5e1;
          font-size: 0.8rem;
          font-weight: 600;
          margin-bottom: 6px;
        }
      }
    }

    .input-with-icon {
      position: relative;

      i {
        position: absolute;
        left: 14px;
        top: 50%;
        transform: translateY(-50%);
        color: #64748b;
        font-size: 0.95rem;
      }

      .form-control {
        padding-left: 42px;
        background: rgba(15, 23, 42, 0.6);
        border-color: #334155;
        color: #ffffff;

        &:focus {
          border-color: #6366f1;
          background: rgba(15, 23, 42, 0.9);
          box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.3);
        }

        &::placeholder {
          color: #64748b;
        }
      }
    }

    .btn-block {
      width: 100%;
      padding: 12px;
      font-size: 0.95rem;
      border-radius: 12px;
    }

    .demo-box {
      margin-top: 24px;
      padding: 14px 16px;
      background: rgba(15, 23, 42, 0.6);
      border: 1px dashed #334155;
      border-radius: 14px;
      text-align: center;

      .demo-title {
        font-size: 0.8rem;
        font-weight: 700;
        color: #38bdf8;
        margin-bottom: 2px;
      }

      .demo-desc {
        font-size: 0.725rem;
        color: #94a3b8;
        margin-bottom: 10px;
      }
    }

    .login-footer {
      margin-top: 24px;
      text-align: center;
      font-size: 0.7rem;
      color: #64748b;
    }
  `]
})
export class LoginComponent {
  username = 'hr_manager@acme.com';
  password = 'password123';
  loading = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private notificationService: NotificationService
  ) {}

  fillDemoCredentials() {
    this.username = 'hr_manager@acme.com';
    this.password = 'password123';
    this.onSubmit();
  }

  onSubmit() {
    if (!this.username || !this.password) {
      this.notificationService.warning('Validation', 'Please provide username and password');
      return;
    }

    this.loading = true;
    this.authService.login(this.username, this.password).subscribe({
      next: (res) => {
        this.loading = false;
        this.notificationService.success('Welcome back', 'Signed in successfully');
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.loading = false;
        this.notificationService.error('Login Failed', err.error?.message || 'Invalid username or password');
      }
    });
  }
}
