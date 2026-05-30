import {Component, computed, effect, input, signal} from '@angular/core';
import {DashboardInventoryStatDto} from '../dashboard.service';
import {ListPaginationComponent} from '../../../shared/pagination/list-pagination.component';
import {getTotalPages, paginateItems} from '../../../shared/utils/pagination.utils';

const PAGE_SIZE = 5;

@Component({
    selector: 'app-dashboard-inventory-breakdown',
    standalone: true,
    imports: [ListPaginationComponent],
    templateUrl: './dashboard-inventory-breakdown.component.html'
})
export class DashboardInventoryBreakdownComponent {

    readonly inventories = input.required<DashboardInventoryStatDto[]>();
    protected readonly page = signal(1);
    protected readonly totalPages = computed(() => getTotalPages(this.inventories().length, PAGE_SIZE));
    protected readonly pagedInventories = computed(() => paginateItems(this.inventories(), this.page(), PAGE_SIZE));

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
