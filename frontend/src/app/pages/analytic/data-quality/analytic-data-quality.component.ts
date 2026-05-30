import {Component, computed, effect, input, signal} from '@angular/core';
import {DecimalPipe} from '@angular/common';
import {AnalyticsDataQualityDto} from '../analytic.service';
import {ListPaginationComponent} from '../../../shared/pagination/list-pagination.component';
import {getTotalPages, paginateItems} from '../../../shared/utils/pagination.utils';

const PAGE_SIZE = 4;

@Component({
    selector: 'app-analytic-data-quality',
    standalone: true,
    imports: [DecimalPipe, ListPaginationComponent],
    templateUrl: './analytic-data-quality.component.html'
})
export class AnalyticDataQualityComponent {

    readonly metrics = input.required<AnalyticsDataQualityDto[]>();
    protected readonly page = signal(1);
    protected readonly totalPages = computed(() => getTotalPages(this.metrics().length, PAGE_SIZE));
    protected readonly pagedMetrics = computed(() => paginateItems(this.metrics(), this.page(), PAGE_SIZE));

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
