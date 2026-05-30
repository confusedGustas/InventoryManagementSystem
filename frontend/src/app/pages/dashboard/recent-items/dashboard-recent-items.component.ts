import {DatePipe} from '@angular/common';
import {Component, computed, effect, input, signal} from '@angular/core';
import {DashboardRecentItemDto} from '../dashboard.service';
import {ListPaginationComponent} from '../../../shared/pagination/list-pagination.component';
import {getTotalPages, paginateItems} from '../../../shared/utils/pagination.utils';

const PAGE_SIZE = 5;

@Component({
    selector: 'app-dashboard-recent-items',
    standalone: true,
    imports: [DatePipe, ListPaginationComponent],
    templateUrl: './dashboard-recent-items.component.html'
})
export class DashboardRecentItemsComponent {

    readonly items = input.required<DashboardRecentItemDto[]>();
    protected readonly page = signal(1);
    protected readonly totalPages = computed(() => getTotalPages(this.items().length, PAGE_SIZE));
    protected readonly pagedItems = computed(() => paginateItems(this.items(), this.page(), PAGE_SIZE));

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
