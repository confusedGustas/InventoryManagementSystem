import {DatePipe} from '@angular/common';
import {Component, computed, effect, input, output, signal} from '@angular/core';
import {AiReportDto} from '../ai.service';
import {ListPaginationComponent} from '../../../shared/pagination/list-pagination.component';
import {getTotalPages, paginateItems} from '../../../shared/utils/pagination.utils';

const PAGE_SIZE = 4;

@Component({
    selector: 'app-ai-reports',
    standalone: true,
    imports: [DatePipe, ListPaginationComponent],
    templateUrl: './ai-reports.component.html'
})
export class AiReportsComponent {

    readonly reports = input.required<AiReportDto[]>();
    readonly loading = input.required<boolean>();
    protected readonly page = signal(1);
    protected readonly totalPages = computed(() => getTotalPages(this.reports().length, PAGE_SIZE));
    protected readonly pagedReports = computed(() => paginateItems(this.reports(), this.page(), PAGE_SIZE));

    readonly reportDownloaded = output<string>();

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
