import {Component, input} from '@angular/core';
import {DecimalPipe} from '@angular/common';
import {AnalyticsDataQualityDto} from '../analytic.service';

@Component({
    selector: 'app-analytic-data-quality',
    standalone: true,
    imports: [DecimalPipe],
    templateUrl: './analytic-data-quality.component.html'
})
export class AnalyticDataQualityComponent {

    readonly metrics = input.required<AnalyticsDataQualityDto[]>();

}
