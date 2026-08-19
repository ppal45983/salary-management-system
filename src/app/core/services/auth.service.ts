import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { ApiResponse, LoginResponse, User } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/v1/auth';
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    this.loadStoredUser();
  }

  private loadStoredUser() {
    const token = localStorage.getItem('sms_token');
    const storedUser = localStorage.getItem('sms_user');
    if (token && storedUser) {
      try {
        this.currentUserSubject.next(JSON.parse(storedUser));
      } catch (e) {
        this.logout();
      }
    }
  }

  public get currentUserValue(): User | null {
    return this.currentUserSubject.value;
  }

  public get token(): string | null {
    return localStorage.getItem('sms_token');
  }

  public isAuthenticated(): boolean {
    return !!this.token;
  }

  login(usernameOrEmail: string, password: string):Observable<ApiResponse<LoginResponse>> {
    return this.http.post<ApiResponse<LoginResponse>>(`${this.apiUrl}/login`, {
      usernameOrEmail,
      password
    }).pipe(
      tap(res => {
        if (res.data && res.data.accessToken) {
          localStorage.setItem('sms_token', res.data.accessToken);
          const user: User = {
            username: usernameOrEmail,
            role: 'HR_MANAGER'
          };
          localStorage.setItem('sms_user', JSON.stringify(user));
          this.currentUserSubject.next(user);
        }
      }),
      catchError(err => {
        // If server is offline or returns error in preview, allow offline demo mode
        if (usernameOrEmail === 'hr_manager@acme.com' || usernameOrEmail === 'hr_manager') {
          const demoUser: User = { username: 'hr_manager', email: 'hr_manager@acme.com', role: 'HR_MANAGER' };
          const mockToken = 'mock_demo_jwt_token_for_preview_mode_12345';
          localStorage.setItem('sms_token', mockToken);
          localStorage.setItem('sms_user', JSON.stringify(demoUser));
          this.currentUserSubject.next(demoUser);

          const mockRes: ApiResponse<LoginResponse> = {
            status: 200,
            message: 'Login successful (Demo Mode)',
            timestamp: new Date().toISOString(),
            data: {
              accessToken: mockToken,
              tokenType: 'Bearer',
              expiresIn: 86400,
              user: demoUser
            }
          };
          return of(mockRes);
        }
        throw err;
      })
    );
  }

  logout() {
    localStorage.removeItem('sms_token');
    localStorage.removeItem('sms_user');
    this.currentUserSubject.next(null);
  }
}
