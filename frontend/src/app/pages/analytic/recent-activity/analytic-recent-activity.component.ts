import {Component, input} from '@angular/core';
import {AnalyticsRecentActivityDto} from '../analytic.service';

@Component({
    selector: 'app-analytic-recent-activity',
    standalone: true,
    templateUrl: './analytic-recent-activity.component.html'
})
export class AnalyticRecentActivityComponent {

    readonly activity = input.required<AnalyticsRecentActivityDto[]>();

}
