import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { ApiResponse, DashboardMetrics, DepartmentDistribution, PayEquity } from '../models/models';
import { MockDataService } from './mock-data.service';

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {
  private apiUrl = 'http://localhost:8080/api/v1/analytics';

  constructor(
    private http: HttpClient,
    private mockData: MockDataService
  ) {}

  getDashboardMetrics(): Observable<DashboardMetrics> {
    return this.http.get<ApiResponse<DashboardMetrics>>(`${this.apiUrl}/dashboard`).pipe(
      map(res => res.data),
      catchError(() => of(this.mockData.getDashboardMetrics()))
    );
  }

  getDepartmentDistributions(): Observable<DepartmentDistribution[]> {
    return this.http.get<ApiResponse<DepartmentDistribution[]>>(`${this.apiUrl}/distribution`).pipe(
      map(res => res.data),
      catchError(() => of(this.mockData.getDepartmentDistributions()))
    );
  }

  getPayEquityAnalysis(): Observable<PayEquity[]> {
    return this.http.get<ApiResponse<PayEquity[]>>(`${this.apiUrl}/pay-equity`).pipe(
      map(res => res.data),
      catchError(() => of(this.mockData.getPayEquityAnalysis()))
    );
  }
}
