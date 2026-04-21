import {Component, input} from '@angular/core';
import {DashboardCategoryStatDto} from '../dashboard.service';

@Component({
    selector: 'app-dashboard-category-breakdown',
    standalone: true,
    templateUrl: './dashboard-category-breakdown.component.html'
})
export class DashboardCategoryBreakdownComponent {

    readonly categories = input.required<DashboardCategoryStatDto[]>();

}
