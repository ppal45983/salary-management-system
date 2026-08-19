import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface ToastMessage {
  id: number;
  type: 'success' | 'danger' | 'warning' | 'info';
  title: string;
  message: string;
  duration?: number;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private toasts$ = new BehaviorSubject<ToastMessage[]>([]);
  public toasts = this.toasts$.asObservable();

  private counter = 0;

  show(type: 'success' | 'danger' | 'warning' | 'info', title: string, message: string, duration: number = 4000) {
    const id = ++this.counter;
    const toast: ToastMessage = { id, type, title, message, duration };
    const current = this.toasts$.value;
    this.toasts$.next([...current, toast]);

    if (duration > 0) {
      setTimeout(() => this.remove(id), duration);
    }
  }

  success(title: string, message: string) {
    this.show('success', title, message);
  }

  error(title: string, message: string) {
    this.show('danger', title, message, 6000);
  }

  warning(title: string, message: string) {
    this.show('warning', title, message);
  }

  info(title: string, message: string) {
    this.show('info', title, message);
  }

  remove(id: number) {
    const updated = this.toasts$.value.filter(t => t.id !== id);
    this.toasts$.next(updated);
  }
}
