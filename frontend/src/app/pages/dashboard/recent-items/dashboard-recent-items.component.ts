import {DatePipe} from '@angular/common';
import {Component, input} from '@angular/core';
import {DashboardRecentItemDto} from '../dashboard.service';

@Component({
    selector: 'app-dashboard-recent-items',
    standalone: true,
    imports: [DatePipe],
    templateUrl: './dashboard-recent-items.component.html'
})
export class DashboardRecentItemsComponent {

    readonly items = input.required<DashboardRecentItemDto[]>();

}
