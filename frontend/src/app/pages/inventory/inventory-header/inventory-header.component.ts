import {Component, input, output} from '@angular/core';

@Component({
    selector: 'app-inventory-header',
    standalone: true,
    templateUrl: './inventory-header.component.html'
})
export class InventoryHeaderComponent {

    readonly canManageInventories = input.required<boolean>();
    readonly showInventoryForm = input.required<boolean>();
    readonly showItemForm = input.required<boolean>();

    readonly inventoryFormToggled = output<void>();
    readonly itemFormToggled = output<void>();

}
