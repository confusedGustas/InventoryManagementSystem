import {Component, input} from '@angular/core';
import {DashboardInventoryStatDto} from '../dashboard.service';

@Component({
    selector: 'app-dashboard-inventory-breakdown',
    standalone: true,
    templateUrl: './dashboard-inventory-breakdown.component.html'
})
export class DashboardInventoryBreakdownComponent {

    readonly inventories = input.required<DashboardInventoryStatDto[]>();

}
