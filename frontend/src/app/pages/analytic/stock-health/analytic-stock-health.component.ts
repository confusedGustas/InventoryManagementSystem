import {Component, computed, effect, input, signal} from '@angular/core';
import {AnalyticsStockHealthDto} from '../analytic.service';
import {ListPaginationComponent} from '../../../shared/pagination/list-pagination.component';
import {getTotalPages, paginateItems} from '../../../shared/utils/pagination.utils';

const PAGE_SIZE = 3;

@Component({
    selector: 'app-analytic-stock-health',
    standalone: true,
    imports: [ListPaginationComponent],
    templateUrl: './analytic-stock-health.component.html'
})
export class AnalyticStockHealthComponent {

    readonly stockHealth = input.required<AnalyticsStockHealthDto[]>();
    protected readonly page = signal(1);
    protected readonly totalPages = computed(() => getTotalPages(this.stockHealth().length, PAGE_SIZE));
    protected readonly pagedStockHealth = computed(() => paginateItems(this.stockHealth(), this.page(), PAGE_SIZE));

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
