import {Injectable, signal} from '@angular/core';

export type ToastType = 'success' | 'error';

export interface ToastItem {
    id: number;
    message: string;
    type: ToastType;
}

@Injectable({ providedIn: 'root' })
export class ToastService {

    private nextId = 1;
    readonly toasts = signal<ToastItem[]>([]);

    show(message: string, type: ToastType): void {
        const id = this.nextId++;
        const toast = { id, message, type };

        this.toasts.update((items) => [...items, toast]);

        window.setTimeout(() => {
            this.dismiss(id);
        }, 4000);
    }

    success(message: string): void {
        this.show(message, 'success');
    }

    error(message: string): void {
        this.show(message, 'error');
    }

    dismiss(id: number): void {
        this.toasts.update((items) => items.filter((item) => item.id !== id));
    }

}
