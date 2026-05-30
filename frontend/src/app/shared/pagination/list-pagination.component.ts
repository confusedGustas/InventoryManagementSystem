import {Component, input, output} from '@angular/core';

@Component({
    selector: 'app-list-pagination',
    standalone: true,
    template: `
        @if (totalPages() > 1) {
            <div class="mt-5 flex flex-col gap-4 border-t border-slate-200 pt-5 text-sm text-slate-500 md:flex-row md:items-center md:justify-between">
                <div class="flex items-center gap-2">
                    <button
                        type="button"
                        class="rounded-lg border border-slate-200 px-3 py-2 transition hover:border-slate-300 hover:text-slate-700 disabled:cursor-not-allowed disabled:opacity-50"
                        [disabled]="page() <= 1"
                        (click)="pageChanged.emit(page() - 1)"
                    >
                        Previous
                    </button>
                    <span class="rounded-lg bg-slate-100 px-3 py-2 font-medium text-slate-700">Page {{ page() }} / {{ totalPages() }}</span>
                    <button
                        type="button"
                        class="rounded-lg border border-slate-200 px-3 py-2 transition hover:border-slate-300 hover:text-slate-700 disabled:cursor-not-allowed disabled:opacity-50"
                        [disabled]="page() >= totalPages()"
                        (click)="pageChanged.emit(page() + 1)"
                    >
                        Next
                    </button>
                </div>
            </div>
        }
    `
})
export class ListPaginationComponent {

    readonly page = input.required<number>();
    readonly totalPages = input.required<number>();

    readonly pageChanged = output<number>();

}
