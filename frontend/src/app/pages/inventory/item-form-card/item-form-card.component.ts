import {Component, input, output} from '@angular/core';
import {ReactiveFormsModule, FormGroup} from '@angular/forms';
import {InventoryDto} from '../inventory.service';

@Component({
    selector: 'app-item-form-card',
    standalone: true,
    imports: [ReactiveFormsModule],
    templateUrl: './item-form-card.component.html'
})
export class ItemFormCardComponent {

    readonly editingItemId = input.required<string | null>();
    readonly itemForm = input.required<FormGroup>();
    readonly selectedInventory = input.required<InventoryDto | null>();

    readonly submitted = output<void>();

}
