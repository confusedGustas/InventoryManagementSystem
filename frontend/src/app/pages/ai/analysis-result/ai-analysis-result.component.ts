import {DatePipe} from '@angular/common';
import {Component, computed, effect, input, output, signal} from '@angular/core';
import {AiAnalysisDto} from '../ai.service';
import {ListPaginationComponent} from '../../../shared/pagination/list-pagination.component';
import {getTotalPages, paginateItems} from '../../../shared/utils/pagination.utils';

const PAGE_SIZE = 4;
const ACTIONS_PAGE_SIZE = 5;

@Component({
    selector: 'app-ai-analysis-result',
    standalone: true,
    imports: [DatePipe, ListPaginationComponent],
    templateUrl: './ai-analysis-result.component.html'
})
export class AiAnalysisResultComponent {

    readonly analysis = input.required<AiAnalysisDto | null>();
    readonly loading = input.required<boolean>();
    protected readonly depletionPage = signal(1);
    protected readonly oversupplyPage = signal(1);
    protected readonly purchasesPage = signal(1);
    protected readonly actionsPage = signal(1);
    protected readonly depletionTotalPages = computed(() => getTotalPages(this.analysis()?.depletionRisks.length ?? 0, PAGE_SIZE));
    protected readonly oversupplyTotalPages = computed(() => getTotalPages(this.analysis()?.oversupplyRisks.length ?? 0, PAGE_SIZE));
    protected readonly purchasesTotalPages = computed(() => getTotalPages(this.analysis()?.suggestedPurchases.length ?? 0, PAGE_SIZE));
    protected readonly actionsTotalPages = computed(() => getTotalPages(this.analysis()?.actions.length ?? 0, ACTIONS_PAGE_SIZE));
    protected readonly pagedDepletionRisks = computed(() => paginateItems(this.analysis()?.depletionRisks ?? [], this.depletionPage(), PAGE_SIZE));
    protected readonly pagedOversupplyRisks = computed(() => paginateItems(this.analysis()?.oversupplyRisks ?? [], this.oversupplyPage(), PAGE_SIZE));
    protected readonly pagedSuggestedPurchases = computed(() => paginateItems(this.analysis()?.suggestedPurchases ?? [], this.purchasesPage(), PAGE_SIZE));
    protected readonly pagedActions = computed(() => paginateItems(this.analysis()?.actions ?? [], this.actionsPage(), ACTIONS_PAGE_SIZE));

    readonly pdfRequested = output<string>();

    constructor() {
        effect(() => this.clampPage(this.depletionPage, this.depletionTotalPages()));
        effect(() => this.clampPage(this.oversupplyPage, this.oversupplyTotalPages()));
        effect(() => this.clampPage(this.purchasesPage, this.purchasesTotalPages()));
        effect(() => this.clampPage(this.actionsPage, this.actionsTotalPages()));
    }

    protected goToDepletionPage(page: number): void {
        this.goToPage(this.depletionPage, page, this.depletionTotalPages());
    }

    protected goToOversupplyPage(page: number): void {
        this.goToPage(this.oversupplyPage, page, this.oversupplyTotalPages());
    }

    protected goToPurchasesPage(page: number): void {
        this.goToPage(this.purchasesPage, page, this.purchasesTotalPages());
    }

    protected goToActionsPage(page: number): void {
        this.goToPage(this.actionsPage, page, this.actionsTotalPages());
    }

    private goToPage(pageSignal: { set: (value: number) => void }, page: number, totalPages: number): void {
        if (page < 1 || page > totalPages) {
            return;
        }

        pageSignal.set(page);
    }

    private clampPage(pageSignal: { (): number; set: (value: number) => void }, totalPages: number): void {
        if (pageSignal() > totalPages) {
            pageSignal.set(totalPages);
        }
    }

}
