import {Component, computed, effect, input, signal} from '@angular/core';
import {AnalyticsRecentActivityDto} from '../analytic.service';
import {ListPaginationComponent} from '../../../shared/pagination/list-pagination.component';
import {getTotalPages, paginateItems} from '../../../shared/utils/pagination.utils';

const PAGE_SIZE = 6;

@Component({
    selector: 'app-analytic-recent-activity',
    standalone: true,
    imports: [ListPaginationComponent],
    templateUrl: './analytic-recent-activity.component.html'
})
export class AnalyticRecentActivityComponent {

    readonly activity = input.required<AnalyticsRecentActivityDto[]>();
    protected readonly page = signal(1);
    protected readonly totalPages = computed(() => getTotalPages(this.activity().length, PAGE_SIZE));
    protected readonly pagedActivity = computed(() => paginateItems(this.activity(), this.page(), PAGE_SIZE));

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
