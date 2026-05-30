import {Component, computed, effect, input, signal} from '@angular/core';
import {DashboardCompanyStatDto} from '../dashboard.service';
import {formatEnumLabel} from '../../../shared/enums';
import {ListPaginationComponent} from '../../../shared/pagination/list-pagination.component';
import {getTotalPages, paginateItems} from '../../../shared/utils/pagination.utils';

const PAGE_SIZE = 5;

@Component({
    selector: 'app-dashboard-company-breakdown',
    standalone: true,
    imports: [ListPaginationComponent],
    templateUrl: './dashboard-company-breakdown.component.html'
})
export class DashboardCompanyBreakdownComponent {

    readonly companies = input.required<DashboardCompanyStatDto[]>();
    protected readonly page = signal(1);
    protected readonly totalPages = computed(() => getTotalPages(this.companies().length, PAGE_SIZE));
    protected readonly pagedCompanies = computed(() => paginateItems(this.companies(), this.page(), PAGE_SIZE));
    protected readonly formatEnumLabel = formatEnumLabel;

    constructor() {
        effect(() => {
            const totalPages = this.totalPages();
            if (this.page() > totalPages) {
                this.page.set(totalPages);
            }
        });
    }

    protected goToPage(page: number): void {
        if (page < 1 || page > this.totalPages()) {
            return;
        }

        this.page.set(page);
    }

}
