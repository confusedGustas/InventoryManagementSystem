import {Component, input} from '@angular/core';
import {AnalyticsStockHealthDto} from '../analytic.service';

@Component({
    selector: 'app-analytic-stock-health',
    standalone: true,
    templateUrl: './analytic-stock-health.component.html'
})
export class AnalyticStockHealthComponent {

    readonly stockHealth = input.required<AnalyticsStockHealthDto[]>();

}
