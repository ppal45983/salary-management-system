import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface CurrencyConfig {
  code: string;
  symbol: string;
  name: string;
  flag: string;
  rateToUsd: number; // 1 USD = rateToUsd in target currency
}

@Injectable({
  providedIn: 'root'
})
export class CurrencyService {
  public static readonly CURRENCIES: CurrencyConfig[] = [
    { code: 'USD', symbol: '$', name: 'US Dollar', flag: '🇺🇸', rateToUsd: 1.0 },
    { code: 'INR', symbol: '₹', name: 'Indian Rupee', flag: '🇮🇳', rateToUsd: 83.5 },
    { code: 'GBP', symbol: '£', name: 'British Pound', flag: '🇬🇧', rateToUsd: 0.79 },
    { code: 'EUR', symbol: '€', name: 'Euro', flag: '🇪🇺', rateToUsd: 0.92 },
    { code: 'CAD', symbol: 'C$', name: 'Canadian Dollar', flag: '🇨🇦', rateToUsd: 1.36 },
    { code: 'AUD', symbol: 'A$', name: 'Australian Dollar', flag: '🇦🇺', rateToUsd: 1.52 },
    { code: 'JPY', symbol: '¥', name: 'Japanese Yen', flag: '🇯🇵', rateToUsd: 155.0 },
    { code: 'SGD', symbol: 'S$', name: 'Singapore Dollar', flag: '🇸🇬', rateToUsd: 1.35 }
  ];

  private activeCurrencySubject = new BehaviorSubject<CurrencyConfig>(CurrencyService.CURRENCIES[0]); // default USD
  public activeCurrency$: Observable<CurrencyConfig> = this.activeCurrencySubject.asObservable();

  constructor() {
    const saved = localStorage.getItem('sms_currency');
    if (saved) {
      const found = CurrencyService.CURRENCIES.find(c => c.code === saved);
      if (found) this.activeCurrencySubject.next(found);
    }
  }

  public get currentCurrency(): CurrencyConfig {
    return this.activeCurrencySubject.value;
  }

  public setCurrency(code: string): void {
    const found = CurrencyService.CURRENCIES.find(c => c.code === code);
    if (found) {
      this.activeCurrencySubject.next(found);
      localStorage.setItem('sms_currency', code);
    }
  }

  /**
   * Convert an amount in USD to current active currency
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
