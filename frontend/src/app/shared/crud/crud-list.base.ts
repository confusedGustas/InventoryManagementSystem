import {signal, WritableSignal} from '@angular/core';
import {Observable} from 'rxjs';
import {ToastService} from '../toast/toast.service';
import {Identifiable, PagedResponse} from '../models/common.models';

interface ValidatableForm {
    invalid: boolean;
    markAllAsTouched(): void;
}

interface SubmitOptions<TItem> {
    editingId: string | null;
    createRequest: Observable<TItem>;
    updateRequest: Observable<TItem>;
    createErrorMessage: string;
    updateErrorMessage: string;
    resetForm: () => void;
    onCreateSuccess: (item: TItem) => void;
    onUpdateSuccess: (item: TItem) => void;
}

interface ToggleOptions<TItem extends Identifiable> {
    item: TItem;
    request: Observable<TItem>;
    activeErrorMessage: string;
    inactiveErrorMessage: string;
    resetForm: () => void;
}

export abstract class CrudListBase<TItem extends Identifiable> {

    protected readonly items: WritableSignal<TItem[]> = signal<TItem[]>([]);
    protected readonly showCreateForm = signal(false);
    protected readonly editingItemId = signal<string | null>(null);
    protected readonly pagination = signal({ page: 1, totalPages: 1 });

    protected constructor(private readonly toastService: ToastService) {}

    protected toggleCreateFormState(resetForm: () => void): void {
        if (this.showCreateForm()) {
            resetForm();
            return;
        }

        this.showCreateForm.set(true);
    }

    protected goToPage(page: number, loadPage: () => void): void {
        if (page < 1 || page > this.pagination().totalPages) {
            return;
        }

        this.pagination.update(current => ({ ...current, page }));
        loadPage();
    }

    protected startEditing(item: TItem, populateForm: (item: TItem) => void): void {
        this.editingItemId.set(item.id);
        this.showCreateForm.set(true);
        populateForm(item);
    }

    protected submitCrud(form: ValidatableForm, invalidFormMessage: string, options: SubmitOptions<TItem>): void {
        if (form.invalid) {
            form.markAllAsTouched();
            this.toastService.error(invalidFormMessage);
            return;
        }

        const request = options.editingId ? options.updateRequest : options.createRequest;
        request.subscribe({
            next: (item) => {
                options.resetForm();
                if (options.editingId) {
                    options.onUpdateSuccess(item);
                    return;
                }

                options.onCreateSuccess(item);
            },
            error: () => this.toastService.error(options.editingId ? options.updateErrorMessage : options.createErrorMessage),
        });
    }

    protected toggleItemState(options: ToggleOptions<TItem>, isActive: boolean): void {
        options.request.subscribe({
            next: (updatedItem) => {
                if (this.editingItemId() === options.item.id) {
                    options.resetForm();
                }

                this.replaceItem(updatedItem);
            },
            error: () => this.toastService.error(isActive ? options.activeErrorMessage : options.inactiveErrorMessage),
        });
    }

    protected applyPage(response: PagedResponse<TItem>): void {
        this.pagination.set({
            page: response.page,
            totalPages: Math.max(response.totalPages, 1),
        });
        this.items.set(response.content);
    }

    protected resetCrudState(afterReset?: () => void): void {
        afterReset?.();
        this.editingItemId.set(null);
        this.showCreateForm.set(false);
    }

    protected replaceItem(updatedItem: TItem): void {
        this.items.update(list => list.map(item => item.id === updatedItem.id ? updatedItem : item));
    }

}
