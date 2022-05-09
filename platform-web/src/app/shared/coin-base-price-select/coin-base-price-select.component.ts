import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {CoinPrice} from '../../asset/wallet/wallet.model';
import {CoinBaseService} from '../coin-base.service';
import {Observable, timer} from 'rxjs';
import {switchMap} from 'rxjs/operators';

@Component({
  selector: 'app-coin-base-price-select',
  template: `
    <div class="d-inline" *ngIf="coinPrice$ |async as coinPrice; else loading">
        <span i18n="比特币价格@@app.adverts.current-price">
          BTC Price: <strong
          class="font-weight-bold">{{ coinPrice.amount | currency: coinPrice.currency}}</strong></span>
      <select class="selectize-control ml-1" [(ngModel)]="priceCurrency"
              (change)="select()"
              i18n="各个国家的货币@@app.adverts.country.currency">
        <option value="CNY">CNY</option>
        <option value="USD">USD</option>
        <option value="CAD">CAD</option>
        <option value="NGN">NGN</option>
        <option value="GBP">GBP</option>
        <option value="NZD">NZD</option>
        <option value="AUD">AUD</option>
        <option value="EUR">EUR</option>
      </select>
    </div>
    <ng-template #loading>
      loading...
    </ng-template>`,
  styles: []
})
export class CoinBasePriceSelectComponent implements OnInit {

  coinPrice$: Observable<CoinPrice>;

  @Input() priceCurrency = 'USD';
  @Output() pricingChange = new EventEmitter<Observable<CoinPrice>>();

  constructor(private coinBase: CoinBaseService) {
  }

  ngOnInit() {
    this.select();
  }

  select() {
    this.coinPrice$ = timer(0, 5000).pipe(
      switchMap(_ =>
        this.coinBase.spotPrices('BTC-' + this.priceCurrency)));
    this.pricingChange.emit(this.coinPrice$);
  }

}
