import {Component, inject} from '@angular/core';
import {ToastService} from './toast.service';

@Component({
    selector: 'app-toast',
    standalone: true,
    templateUrl: './toast.component.html'
})
export class ToastComponent {

    protected readonly toastService = inject(ToastService);

    protected dismiss(id: number): void {
        this.toastService.dismiss(id);
    }

}
