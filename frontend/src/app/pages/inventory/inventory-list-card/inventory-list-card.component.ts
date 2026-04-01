import {Component, input, output} from '@angular/core';
import {DatePipe} from '@angular/common';
import {InventoryDto} from '../inventory.service';
import {getToggleButtonClass} from '../../../shared/utils/ui.utils';

@Component({
    selector: 'app-inventory-list-card',
    standalone: true,
    imports: [DatePipe],
    templateUrl: './inventory-list-card.component.html'
})
export class InventoryListCardComponent {

    readonly inventories = input.required<InventoryDto[]>();
    readonly selectedInventoryId = input.required<string | null>();
    readonly pagination = input.required<{ page: number; totalPages: number }>();
    readonly canManageInventories = input.required<boolean>();

    readonly inventorySelected = output<InventoryDto>();
    readonly inventoryEdited = output<InventoryDto>();
    readonly inventoryToggled = output<InventoryDto>();
    readonly pageChanged = output<number>();

    protected readonly getToggleButtonClass = getToggleButtonClass;

}
