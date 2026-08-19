import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';

export interface CurrencyConfig {
  code: string;
  symbol: string;
  name: string;
  flag: string;
  rateToUsd: number; // 1 USD = rateToUsd in target currency
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
    { code: 'INR', symbol: '₹', name: 'Indian Rupee', flag: '🇮🇳', rateToUsd: 95.76 },
    { code: 'GBP', symbol: '£', name: 'British Pound', flag: '🇬🇧', rateToUsd: 0.74 },
    { code: 'EUR', symbol: '€', name: 'Euro', flag: '🇪🇺', rateToUsd: 0.86 },
    { code: 'CAD', symbol: 'C$', name: 'Canadian Dollar', flag: '🇨🇦', rateToUsd: 1.39 },
    { code: 'AUD', symbol: 'A$', name: 'Australian Dollar', flag: '🇦🇺', rateToUsd: 1.41 },
    { code: 'JPY', symbol: '¥', name: 'Japanese Yen', flag: '🇯🇵', rateToUsd: 159.59 },
    { code: 'SGD', symbol: 'S$', name: 'Singapore Dollar', flag: '🇸🇬', rateToUsd: 1.28 }
  ];

  public currencies: CurrencyConfig[] = [...CurrencyService.BASE_CURRENCIES];

  private activeCurrencySubject = new BehaviorSubject<CurrencyConfig>(this.currencies[0]);
  public activeCurrency$: Observable<CurrencyConfig> = this.activeCurrencySubject.asObservable();

  private liveStatusSubject = new BehaviorSubject<{ isLive: boolean; lastUpdated: string; inrRate: number }>({
    isLive: false,
    lastUpdated: 'Initializing...',
    inrRate: 95.76
  });
  public liveStatus$ = this.liveStatusSubject.asObservable();

  constructor(private http: HttpClient) {
    const saved = localStorage.getItem('sms_currency');
    if (saved) {
      const found = this.currencies.find(c => c.code === saved);
      if (found) this.activeCurrencySubject.next(found);
    }
    this.fetchLiveExchangeRates();
  }

  /**
   * Fetch live foreign exchange rates from open live rates endpoint
   */
  public fetchLiveExchangeRates(): void {
    const primaryApi = 'https://open.er-api.com/v6/latest/USD';
    const fallbackApi = 'https://api.exchangerate-api.com/v4/latest/USD';

    this.http.get<LiveFxRateResponse>(primaryApi).pipe(
      catchError(() => this.http.get<any>(fallbackApi)),
      catchError(() => of(null))
    ).subscribe(res => {
      if (res && res.rates) {
        const rates = res.rates;
        this.currencies.forEach(c => {
          if (rates[c.code]) {
            c.rateToUsd = Number(rates[c.code]);
          }
        });

        // Update active currency with new rate
        const currentCode = this.activeCurrencySubject.value.code;
        const updated = this.currencies.find(c => c.code === currentCode) || this.currencies[0];
        this.activeCurrencySubject.next(updated);

        const inrRate = rates['INR'] || 95.76;
        const lastUpdated = res.time_last_update_utc ? new Date(res.time_last_update_utc).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

        this.liveStatusSubject.next({
          isLive: true,
          lastUpdated: lastUpdated,
          inrRate: inrRate
        });

        localStorage.setItem('sms_fx_rates', JSON.stringify({ rates, time: new Date().toISOString() }));
      }
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

  /**
   * Convert an amount in USD to current active currency using live rate
   */
  public convertFromUsd(amountInUsd: number): number {
    if (!amountInUsd) return 0;
    return Math.round(amountInUsd * this.currentCurrency.rateToUsd);
  }

  /**
   * Format an amount in USD into formatted string with current currency symbol
   */
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
