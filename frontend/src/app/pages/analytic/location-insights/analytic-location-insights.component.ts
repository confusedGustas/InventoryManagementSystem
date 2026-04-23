import {Component, input} from '@angular/core';
import {DecimalPipe} from '@angular/common';
import {AnalyticsLocationInsightDto} from '../analytic.service';

@Component({
    selector: 'app-analytic-location-insights',
    standalone: true,
    imports: [DecimalPipe],
    templateUrl: './analytic-location-insights.component.html'
})
export class AnalyticLocationInsightsComponent {

    readonly locations = input.required<AnalyticsLocationInsightDto[]>();

}
