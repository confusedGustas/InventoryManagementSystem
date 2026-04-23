import {Component, input} from '@angular/core';
import {DecimalPipe} from '@angular/common';
import {AnalyticsBrandInsightDto} from '../analytic.service';

@Component({
    selector: 'app-analytic-brand-insights',
    standalone: true,
    imports: [DecimalPipe],
    templateUrl: './analytic-brand-insights.component.html'
})
export class AnalyticBrandInsightsComponent {

    readonly brands = input.required<AnalyticsBrandInsightDto[]>();

}
