import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { SalaryService } from './salary.service';
import { MockDataService } from './mock-data.service';

describe('SalaryService', () => {
  let service: SalaryService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [SalaryService, MockDataService]
    });
    service = TestBed.inject(SalaryService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should calculate live progressive tax preview properly', (done) => {
    service.calculateTaxPreview({
      baseSalary: 100000,
      allowances: 10000,
      deductions: 5000,
      country: 'United States',
      currency: 'USD'
    }).subscribe(result => {
      expect(result).toBeTruthy();
      expect(result.grossSalary).toBe(110000);
      expect(result.totalTax).toBeGreaterThan(0);
      expect(result.netSalary).toBeLessThan(result.grossSalary);
      done();
    });

    const req = httpMock.expectOne('http://localhost:8080/api/v1/salaries/calculate-tax');
    req.error(new ProgressEvent('Network error')); // Test fallback to MockDataService
  });

  it('should generate salary slip with formatted earnings and deductions', (done) => {
    service.generateSalarySlip(1).subscribe(slip => {
      expect(slip).toBeTruthy();
      expect(slip.companyName).toBe('ACME Global Corporation');
      expect(slip.baseSalary).toBeDefined();
      expect(slip.netSalary).toBeDefined();
      done();
    });

    const req = httpMock.expectOne('http://localhost:8080/api/v1/salaries/1/slip');
    req.error(new ProgressEvent('Network error'));
  });
});
