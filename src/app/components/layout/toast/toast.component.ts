import { Component } from '@angular/core';
import { NotificationService, ToastMessage } from '../../../core/services/notification.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-toast',
  template: `
    <div class="toast-container">
      <div *ngFor="let toast of toasts$ | async" class="toast-item toast-{{toast.type}}">
        <div class="toast-icon">
          <i *ngIf="toast.type === 'success'" class="fa-solid fa-circle-check"></i>
          <i *ngIf="toast.type === 'danger'" class="fa-solid fa-circle-exclamation"></i>
          <i *ngIf="toast.type === 'warning'" class="fa-solid fa-triangle-exclamation"></i>
          <i *ngIf="toast.type === 'info'" class="fa-solid fa-circle-info"></i>
        </div>
        <div class="toast-content">
          <div class="toast-title">{{ toast.title }}</div>
          <div class="toast-message">{{ toast.message }}</div>
        </div>
        <button class="toast-close" (click)="close(toast.id)">
          <i class="fa-solid fa-xmark"></i>
        </button>
      </div>
    </div>
  `,
  styles: [`
    .toast-container {
      position: fixed;
      top: 20px;
      right: 24px;
      z-index: 9999;
      display: flex;
      flex-direction: column;
      gap: 10px;
      pointer-events: none;
    }
    .toast-item {
      pointer-events: auto;
      min-width: 320px;
      max-width: 420px;
      padding: 14px 16px;
      border-radius: 12px;
      background: #ffffff;
      box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.15), 0 8px 10px -6px rgba(0, 0, 0, 0.1);
      display: flex;
      align-items: flex-start;
      gap: 12px;
      animation: slideInRight 0.25s cubic-bezier(0.16, 1, 0.3, 1);
      border-left: 4px solid transparent;
    }
    .toast-success { border-left-color: #10b981; .toast-icon { color: #10b981; } }
    .toast-danger { border-left-color: #ef4444; .toast-icon { color: #ef4444; } }
    .toast-warning { border-left-color: #f59e0b; .toast-icon { color: #f59e0b; } }
    .toast-info { border-left-color: #3b82f6; .toast-icon { color: #3b82f6; } }
    .toast-icon { font-size: 1.25rem; margin-top: 2px; }
    .toast-content { flex: 1; }
    .toast-title { font-weight: 700; font-size: 0.875rem; color: #0f172a; margin-bottom: 2px; }
    .toast-message { font-size: 0.8rem; color: #64748b; line-height: 1.35; }
    .toast-close { background: none; border: none; color: #94a3b8; cursor: pointer; padding: 2px; font-size: 0.9rem; &:hover { color: #0f172a; } }
    @keyframes slideInRight { from { transform: translateX(100%); opacity: 0; } to { transform: translateX(0); opacity: 1; } }
  `]
})
export class ToastComponent {
  toasts$: Observable<ToastMessage[]> = this.notificationService.toasts;

  constructor(private notificationService: NotificationService) {}

  close(id: number) {
    this.notificationService.remove(id);
  }
}
