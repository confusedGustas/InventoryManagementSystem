import {Component, input} from '@angular/core';
import {DashboardSummaryDto} from '../dashboard.service';

@Component({
    selector: 'app-dashboard-summary-cards',
    standalone: true,
    templateUrl: './dashboard-summary-cards.component.html'
})
export class DashboardSummaryCardsComponent {

    readonly summary = input.required<DashboardSummaryDto>();

}
