import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should store token and user on successful login', () => {
    const mockResponse = {
      status: 200,
      message: 'Login successful',
      timestamp: '2024-08-20T00:00:00',
      data: {
        accessToken: 'sample-jwt-token',
        tokenType: 'Bearer',
        expiresIn: 86400,
        user: { username: 'hr_manager', role: 'HR_MANAGER' }
      }
    };

    service.login('hr_manager@acme.com', 'password123').subscribe(res => {
      expect(res.data.accessToken).toBe('sample-jwt-token');
      expect(service.token).toBe('sample-jwt-token');
      expect(service.isAuthenticated()).toBeTrue();
    });

    const req = httpMock.expectOne('http://localhost:8080/api/v1/auth/login');
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should clear stored token and user on logout', () => {
    localStorage.setItem('sms_token', 'sample-token');
    service.logout();
    expect(service.token).toBeNull();
    expect(service.isAuthenticated()).toBeFalse();
  });
});
