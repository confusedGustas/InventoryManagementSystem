import {Component, input, output} from '@angular/core';
import {DatePipe} from '@angular/common';
import {InventoryDto, ItemDto} from '../inventory.service';

@Component({
    selector: 'app-item-table-card',
    standalone: true,
    imports: [DatePipe],
    templateUrl: './item-table-card.component.html'
})
export class ItemTableCardComponent {

    readonly selectedInventory = input.required<InventoryDto | null>();
    readonly items = input.required<ItemDto[]>();
    readonly selectedItemIds = input.required<string[]>();
    readonly pagination = input.required<{ page: number; totalPages: number }>();
    readonly canManageItems = input.required<boolean>();

    readonly inventorySelectionCleared = output<void>();
    readonly itemsDeleted = output<void>();
    readonly selectAllToggled = output<boolean>();
    readonly itemSelectionToggled = output<{ itemId: string; checked: boolean }>();
    readonly itemEdited = output<ItemDto>();
    readonly pageChanged = output<number>();

    protected areAllItemsSelected(): boolean {
        const items = this.items();
        const selectedItemIds = this.selectedItemIds();
        return items.length > 0 && items.every(item => selectedItemIds.includes(item.id));
    }

}
