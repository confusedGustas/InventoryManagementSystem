import {Component, input} from '@angular/core';
import {DashboardCompanyStatDto} from '../dashboard.service';
import {formatEnumLabel} from '../../../shared/enums';

@Component({
    selector: 'app-dashboard-company-breakdown',
    standalone: true,
    templateUrl: './dashboard-company-breakdown.component.html'
})
export class DashboardCompanyBreakdownComponent {

    readonly companies = input.required<DashboardCompanyStatDto[]>();
    protected readonly formatEnumLabel = formatEnumLabel;

}
