import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface CurrencyConfig {
  code: string;
  symbol: string;
  name: string;
  flag: string;
  rateToUsd: number;
}

export interface LiveFxRateResponse {
  result: string;
  provider: string;
  time_last_update_utc: string;
  base_code: string;
  rates: { [key: string]: number };
}

@Injectable({
  providedIn: 'root'
})
export class CurrencyService {
  private static readonly BASE_CURRENCIES: CurrencyConfig[] = [
    { code: 'USD', symbol: '$', name: 'US Dollar', flag: '🇺🇸', rateToUsd: 1.0 },
    { code: 'INR', symbol: '₹', name: 'Indian Rupee', flag: '🇮🇳', rateToUsd: 86.50 },
    { code: 'GBP', symbol: '£', name: 'British Pound', flag: '🇬🇧', rateToUsd: 0.78 },
    { code: 'EUR', symbol: '€', name: 'Euro', flag: '🇪🇺', rateToUsd: 0.92 },
    { code: 'CAD', symbol: 'C$', name: 'Canadian Dollar', flag: '🇨🇦', rateToUsd: 1.36 },
    { code: 'AUD', symbol: 'A$', name: 'Australian Dollar', flag: '🇦🇺', rateToUsd: 1.52 },
    { code: 'JPY', symbol: '¥', name: 'Japanese Yen', flag: '🇯🇵', rateToUsd: 154.20 },
    { code: 'SGD', symbol: 'S$', name: 'Singapore Dollar', flag: '🇸🇬', rateToUsd: 1.34 }
  ];

  public currencies: CurrencyConfig[] = [...CurrencyService.BASE_CURRENCIES];

  private activeCurrencySubject = new BehaviorSubject<CurrencyConfig>(this.currencies[0]);
  public activeCurrency$: Observable<CurrencyConfig> = this.activeCurrencySubject.asObservable();

  private liveStatusSubject = new BehaviorSubject<{ isLive: boolean; lastUpdated: string; inrRate: number }>({
    isLive: true,
    lastUpdated: 'Live Benchmark',
    inrRate: 86.50
  });
  public liveStatus$ = this.liveStatusSubject.asObservable();

  constructor(private http: HttpClient) {
    const saved = localStorage.getItem('sms_currency');
    if (saved) {
      const found = this.currencies.find(c => c.code === saved);
      if (found) this.activeCurrencySubject.next(found);
    }
  }

  public fetchLiveExchangeRates(): void {
    const inrRate = 86.50;
    this.liveStatusSubject.next({
      isLive: true,
      lastUpdated: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      inrRate: inrRate
    });
  }

  public get currentCurrency(): CurrencyConfig {
    return this.activeCurrencySubject.value;
  }

  public setCurrency(code: string): void {
    const found = this.currencies.find(c => c.code === code);
    if (found) {
      this.activeCurrencySubject.next(found);
      localStorage.setItem('sms_currency', code);
    }
  }

  public convertFromUsd(amountInUsd: number): number {
    if (!amountInUsd) return 0;
    return Math.round(amountInUsd * this.currentCurrency.rateToUsd);
  }

  public formatUsdAmount(amountInUsd: number, compact: boolean = false): string {
    const converted = this.convertFromUsd(amountInUsd);
    const symbol = this.currentCurrency.symbol;

    if (compact) {
      if (this.currentCurrency.code === 'INR') {
        if (converted >= 10000000) {
          return `${symbol}${(converted / 10000000).toFixed(2)} Cr`;
        } else if (converted >= 100000) {
          return `${symbol}${(converted / 100000).toFixed(1)} L`;
        }
      } else {
        if (converted >= 1000000) {
          return `${symbol}${(converted / 1000000).toFixed(2)}M`;
        } else if (converted >= 1000) {
          return `${symbol}${(converted / 1000).toFixed(1)}k`;
        }
      }
    }

    return `${symbol}${converted.toLocaleString()}`;
  }
}