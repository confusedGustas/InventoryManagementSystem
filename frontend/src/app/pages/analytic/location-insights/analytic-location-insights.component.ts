import {Component, computed, effect, input, signal} from '@angular/core';
import {DecimalPipe} from '@angular/common';
import {AnalyticsLocationInsightDto} from '../analytic.service';
import {ListPaginationComponent} from '../../../shared/pagination/list-pagination.component';
import {getTotalPages, paginateItems} from '../../../shared/utils/pagination.utils';

const PAGE_SIZE = 5;

@Component({
    selector: 'app-analytic-location-insights',
    standalone: true,
    imports: [DecimalPipe, ListPaginationComponent],
    templateUrl: './analytic-location-insights.component.html'
})
export class AnalyticLocationInsightsComponent {

    readonly locations = input.required<AnalyticsLocationInsightDto[]>();
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
