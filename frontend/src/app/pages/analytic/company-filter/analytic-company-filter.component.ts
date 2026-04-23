import {Component, input, output} from '@angular/core';
import {CompanyOptionDto} from '../../../shared/models/common.models';

@Component({
    selector: 'app-analytic-company-filter',
    standalone: true,
    templateUrl: './analytic-company-filter.component.html'
})
export class AnalyticCompanyFilterComponent {

    readonly isPlatformAdmin = input.required<boolean>();
    readonly companies = input.required<CompanyOptionDto[]>();
    readonly selectedCompanyId = input.required<string | null>();

    readonly companySelected = output<string | null>();

}
