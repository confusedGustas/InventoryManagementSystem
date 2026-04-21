import {Component, input} from '@angular/core';
import {DashboardLocationStatDto} from '../dashboard.service';

@Component({
    selector: 'app-dashboard-location-breakdown',
    standalone: true,
    templateUrl: './dashboard-location-breakdown.component.html'
})
export class DashboardLocationBreakdownComponent {

    readonly locations = input.required<DashboardLocationStatDto[]>();

}
