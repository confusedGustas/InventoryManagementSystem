import {Component, computed, effect, input, signal} from '@angular/core';
import {DashboardCategoryStatDto} from '../dashboard.service';
import {ListPaginationComponent} from '../../../shared/pagination/list-pagination.component';
import {getTotalPages, paginateItems} from '../../../shared/utils/pagination.utils';

const PAGE_SIZE = 5;

@Component({
    selector: 'app-dashboard-category-breakdown',
    standalone: true,
    imports: [ListPaginationComponent],
    templateUrl: './dashboard-category-breakdown.component.html'
})
export class DashboardCategoryBreakdownComponent {

    readonly categories = input.required<DashboardCategoryStatDto[]>();
    protected readonly page = signal(1);
    protected readonly totalPages = computed(() => getTotalPages(this.categories().length, PAGE_SIZE));
    protected readonly pagedCategories = computed(() => paginateItems(this.categories(), this.page(), PAGE_SIZE));
    protected readonly maxQuantity = computed(() => this.categories()[0]?.totalQuantity || 1);

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
