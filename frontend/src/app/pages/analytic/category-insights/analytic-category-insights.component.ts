import {Component, input} from '@angular/core';
import {DecimalPipe} from '@angular/common';
import {AnalyticsCategoryInsightDto} from '../analytic.service';

@Component({
    selector: 'app-analytic-category-insights',
    standalone: true,
    imports: [DecimalPipe],
    templateUrl: './analytic-category-insights.component.html'
})
export class AnalyticCategoryInsightsComponent {

    readonly categories = input.required<AnalyticsCategoryInsightDto[]>();

}
