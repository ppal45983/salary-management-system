import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { ApiResponse, CountryInfo, Department, Designation, TaxBracket } from '../models/models';
import { MockDataService } from './mock-data.service';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MasterDataService {
  private apiUrl = `${environment.apiUrl}/masters`;

  constructor(
    private http: HttpClient,
    private mockData: MockDataService
  ) {}

  getDepartments(): Observable<Department[]> {
    return this.http.get<ApiResponse<Department[]>>(`${this.apiUrl}/departments`).pipe(
      map(res => res.data),
      catchError((err) => environment.useMockFallback ? of(this.mockData.departments) : throwError(() => err))
    );
  }

  getDesignations(): Observable<Designation[]> {
    return this.http.get<ApiResponse<Designation[]>>(`${this.apiUrl}/designations`).pipe(
      map(res => res.data),
      catchError((err) => environment.useMockFallback ? of(this.mockData.designations) : throwError(() => err))
    );
  }

  getTaxBrackets(country?: string, taxYear?: number): Observable<TaxBracket[]> {
    let params = new HttpParams();
    if (country) params = params.set('country', country);
    if (taxYear) params = params.set('taxYear', taxYear.toString());

    return this.http.get<ApiResponse<TaxBracket[]>>(`${this.apiUrl}/tax-brackets`, { params }).pipe(
      map(res => res.data),
      catchError((err) => {
        if (!environment.useMockFallback) return throwError(() => err);
        let list = this.mockData.taxBrackets;
        if (country) {
          list = list.filter(b => b.country.toLowerCase() === country.toLowerCase());
        }
        return of(list);
      })
    );
  }

  getCountries(): Observable<CountryInfo[]> {
    return this.http.get<ApiResponse<CountryInfo[]>>(`${this.apiUrl}/countries`).pipe(
      map(res => res.data),
      catchError((err) => environment.useMockFallback ? of(this.mockData.countries) : throwError(() => err))
    );
  }
}
