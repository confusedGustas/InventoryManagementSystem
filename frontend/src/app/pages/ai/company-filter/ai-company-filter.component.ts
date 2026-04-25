import {Component, input, output} from '@angular/core';
import {CompanyOptionDto} from '../../../shared/models/common.models';

@Component({
    selector: 'app-ai-company-filter',
    standalone: true,
    templateUrl: './ai-company-filter.component.html'
})
export class AiCompanyFilterComponent {

    readonly isPlatformAdmin = input.required<boolean>();
    readonly companies = input.required<CompanyOptionDto[]>();
    readonly selectedCompanyId = input.required<string | null>();

    readonly companySelected = output<string | null>();

}
