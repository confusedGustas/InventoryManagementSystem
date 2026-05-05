import {Component, computed, inject, signal} from '@angular/core';
import {AuthUtils} from '../../auth/auth.utils';
import {UserRole} from '../../shared/enums';
import {ToastService} from '../../shared/toast/toast.service';
import {InventoryHeaderComponent} from './inventory-header/inventory-header.component';
import {InventoryDto} from './inventory.service';
import {InventoryManagementComponent} from './inventory-management/inventory-management.component';
import {ItemManagementComponent} from './item-management/item-management.component';

@Component({
    selector: 'app-inventory',
    standalone: true,
    imports: [
        InventoryHeaderComponent,
        InventoryManagementComponent,
        ItemManagementComponent,
    ],
    templateUrl: './inventory.component.html'
})
export class InventoryComponent {

    private readonly toast = inject(ToastService);

    protected readonly selectedInventory = signal<InventoryDto | null>(null);
    protected readonly showInventoryForm = signal(false);
    protected readonly showItemForm = signal(false);
    private readonly pendingItemFormOpen = signal(false);
    protected readonly canManageInventories = AuthUtils.hasRole(UserRole.PLATFORM_ADMIN)
        || AuthUtils.hasRole(UserRole.COMPANY_ADMIN);
    protected readonly canManageItems = AuthUtils.hasRole(UserRole.PLATFORM_ADMIN)
        || AuthUtils.hasRole(UserRole.COMPANY_ADMIN)
        || AuthUtils.hasRole(UserRole.COMPANY_USER);
    protected readonly selectedInventoryId = computed(() => this.selectedInventory()?.id ?? null);

    protected toggleInventoryForm(): void {
        this.showInventoryForm.update(current => !current);
    }

    protected toggleItemForm(): void {
        if (!this.canManageItems) {
            this.toast.error('You do not have permission to create items.');
            return;
        }

        if (!this.selectedInventory()) {
            this.pendingItemFormOpen.set(true);
            this.toast.error('Select an inventory first. The item form will open automatically once you choose one.');
            return;
        }

        this.pendingItemFormOpen.set(false);
        this.showItemForm.update(current => !current);
    }

    protected handleSelectedInventoryChanged(inventory: InventoryDto | null): void {
        this.selectedInventory.set(inventory);
        if (!inventory) {
            this.showItemForm.set(false);
            return;
        }

        if (this.pendingItemFormOpen() && this.canManageItems) {
            this.showItemForm.set(true);
            this.pendingItemFormOpen.set(false);
        }
    }

}
