import {Component, computed, effect, input, signal} from '@angular/core';
import {DashboardLocationStatDto} from '../dashboard.service';
import {ListPaginationComponent} from '../../../shared/pagination/list-pagination.component';
import {getTotalPages, paginateItems} from '../../../shared/utils/pagination.utils';

const PAGE_SIZE = 5;

@Component({
    selector: 'app-dashboard-location-breakdown',
    standalone: true,
    imports: [ListPaginationComponent],
    templateUrl: './dashboard-location-breakdown.component.html'
})
export class DashboardLocationBreakdownComponent {

    readonly locations = input.required<DashboardLocationStatDto[]>();
    protected readonly page = signal(1);
    protected readonly totalPages = computed(() => getTotalPages(this.locations().length, PAGE_SIZE));
    protected readonly pagedLocations = computed(() => paginateItems(this.locations(), this.page(), PAGE_SIZE));

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
