import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AnalyticsService } from './analytics.service';
import { MockDataService } from './mock-data.service';

describe('AnalyticsService', () => {
  let service: AnalyticsService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AnalyticsService, MockDataService]
    });
    service = TestBed.inject(AnalyticsService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return dashboard metrics including total headcount and payroll', (done) => {
    service.getDashboardMetrics().subscribe(metrics => {
      expect(metrics).toBeTruthy();
      expect(metrics.totalEmployees).toBe(10000);
      expect(metrics.totalMonthlyPayrollByCurrency).toBeDefined();
      expect(metrics.headcountByDepartment.length).toBeGreaterThan(0);
      done();
    });

    const req = httpMock.expectOne('http://localhost:8080/api/v1/analytics/dashboard');
    req.error(new ProgressEvent('Network error'));
  });

  it('should return department distributions with Min, Max, and Average', (done) => {
    service.getDepartmentDistributions().subscribe(distributions => {
      expect(distributions).toBeTruthy();
      expect(distributions.length).toBeGreaterThan(0);
      expect(distributions[0].minSalary).toBeDefined();
      expect(distributions[0].maxSalary).toBeDefined();
      done();
    });

    const req = httpMock.expectOne('http://localhost:8080/api/v1/analytics/distribution');
    req.error(new ProgressEvent('Network error'));
  });
});
