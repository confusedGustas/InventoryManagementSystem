import {Component, effect, inject, input, output, signal} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {AuthUtils} from '../../../auth/auth.utils';
import {UserRole} from '../../../shared/enums';
import {ToastService} from '../../../shared/toast/toast.service';
import {InventoryDto, InventoryService, ItemDto} from '../inventory.service';
import {ItemFormCardComponent} from '../item-form-card/item-form-card.component';
import {ItemTableCardComponent} from '../item-table-card/item-table-card.component';

@Component({
    selector: 'app-item-management',
    standalone: true,
    imports: [ItemFormCardComponent, ItemTableCardComponent],
    templateUrl: './item-management.component.html'
})
export class ItemManagementComponent {

    private readonly inventoryService = inject(InventoryService);
    private readonly formBuilder = inject(FormBuilder);
    private readonly toast = inject(ToastService);

    readonly selectedInventory = input.required<InventoryDto | null>();
    readonly showForm = input.required<boolean>();

    readonly showFormChange = output<boolean>();
    readonly selectedInventoryCleared = output<void>();

    protected readonly items = signal<ItemDto[]>([]);
    protected readonly selectedItemIds = signal<string[]>([]);
    protected readonly editingItemId = signal<string | null>(null);
    protected readonly pagination = signal({ page: 1, totalPages: 1 });
    private readonly activeInventoryId = signal<string | null>(null);

    protected readonly isPlatformAdmin = AuthUtils.hasRole(UserRole.PLATFORM_ADMIN);
    protected readonly isCompanyAdmin = AuthUtils.hasRole(UserRole.COMPANY_ADMIN);
    protected readonly canManageItems = this.isPlatformAdmin || this.isCompanyAdmin;

    protected readonly itemForm = this.formBuilder.nonNullable.group({
        name: ['', Validators.required],
        sku: [''],
        category: [''],
        brand: [''],
        manufacturer: [''],
        model: [''],
        partNumber: [''],
        serialNumber: [''],
        color: [''],
        dimensions: [''],
        weight: [''],
        unit: [''],
        quantity: [0, [Validators.required, Validators.min(0)]],
        description: [''],
        notes: [''],
    });

    constructor() {
        effect(() => {
            const inventoryId = this.selectedInventory()?.id ?? null;
            if (inventoryId === this.activeInventoryId()) {
                return;
            }

            this.activeInventoryId.set(inventoryId);

            this.selectedItemIds.set([]);
            this.pagination.set({ page: 1, totalPages: 1 });
            this.resetForm(false);

            if (!inventoryId) {
                this.items.set([]);
                this.showFormChange.emit(false);
                return;
            }

            this.loadItems();
        });
    }

    protected goToPage(page: number): void {
        if (page < 1 || page > this.pagination().totalPages) {
            return;
        }

        this.pagination.update(current => ({ ...current, page }));
        this.loadItems();
    }

    protected submitItem(): void {
        const inventoryId = this.selectedInventory()?.id;
        if (!inventoryId) {
            this.toast.error('Select an inventory before saving items.');
            return;
        }

        if (this.itemForm.invalid) {
            this.itemForm.markAllAsTouched();
            this.toast.error('Item information was invalid or incomplete. Please check the form and try again.');
            return;
        }

        const editingId = this.editingItemId();
        const request = editingId
            ? this.inventoryService.updateItem(editingId, this.toItemPayload(inventoryId))
            : this.inventoryService.saveItem(this.toItemPayload(inventoryId));

        request.subscribe({
            next: (item) => {
                this.resetForm();
                if (editingId) {
                    this.items.update(list => list.map(currentItem => currentItem.id === item.id ? item : currentItem));
                    return;
                }

                this.loadItems();
            },
            error: () => this.toast.error(editingId
                ? 'Unable to update item. Please try again.'
                : 'Unable to create item. Please try again.'),
        });
    }

    protected editItem(item: ItemDto): void {
        if (!this.canManageItems) {
            return;
        }

        this.editingItemId.set(item.id);
        this.itemForm.patchValue({
            name: item.name,
            sku: item.sku ?? '',
            category: item.category ?? '',
            brand: item.brand ?? '',
            manufacturer: item.manufacturer ?? '',
            model: item.model ?? '',
            partNumber: item.partNumber ?? '',
            serialNumber: item.serialNumber ?? '',
            color: item.color ?? '',
            dimensions: item.dimensions ?? '',
            weight: item.weight ?? '',
            unit: item.unit ?? '',
            quantity: item.quantity,
            description: item.description ?? '',
            notes: item.notes ?? '',
        });
        this.showFormChange.emit(true);
    }

    protected toggleItemSelection(itemId: string, checked: boolean): void {
        if (checked) {
            this.selectedItemIds.update(itemIds => itemIds.includes(itemId) ? itemIds : [...itemIds, itemId]);
            return;
        }

        this.selectedItemIds.update(itemIds => itemIds.filter(currentItemId => currentItemId !== itemId));
    }

    protected toggleSelectAllItems(checked: boolean): void {
        this.selectedItemIds.set(checked ? this.items().map(item => item.id) : []);
    }

    protected deleteSelectedItems(): void {
        const itemIds = this.selectedItemIds();
        if (itemIds.length === 0) {
            this.toast.error('Select at least one item to delete.');
            return;
        }

        this.inventoryService.deleteItems(itemIds).subscribe({
            next: () => {
                this.selectedItemIds.set([]);
                if (this.editingItemId() && itemIds.includes(this.editingItemId()!)) {
                    this.resetForm();
                }
                this.loadItems();
            },
            error: () => this.toast.error('Unable to delete selected items. Please try again.'),
        });
    }

    private loadItems(): void {
        const inventoryId = this.selectedInventory()?.id;
        if (!inventoryId) {
            this.items.set([]);
            this.selectedItemIds.set([]);
            this.pagination.set({ page: 1, totalPages: 1 });
            return;
        }

        this.inventoryService.getItems(inventoryId, this.pagination().page).subscribe({
            next: (response) => {
                this.pagination.set({
                    page: response.page,
                    totalPages: Math.max(response.totalPages, 1),
                });
                this.items.set(response.content);
                this.selectedItemIds.update(itemIds => itemIds.filter(itemId => response.content.some(item => item.id === itemId)));
            },
            error: () => this.toast.error('Unable to load items.'),
        });
    }

    private resetForm(emit = true): void {
        this.itemForm.reset();
        this.itemForm.patchValue({
            name: '',
            sku: '',
            category: '',
            brand: '',
            manufacturer: '',
            model: '',
            partNumber: '',
            serialNumber: '',
            color: '',
            dimensions: '',
            weight: '',
            unit: '',
            quantity: 0,
            description: '',
            notes: '',
        });
        this.editingItemId.set(null);
        if (emit) {
            this.showFormChange.emit(false);
        }
    }

    private toItemPayload(inventoryId: string) {
        const raw = this.itemForm.getRawValue();

        return {
            inventoryId,
            name: raw.name,
            sku: this.normalize(raw.sku),
            category: this.normalize(raw.category),
            brand: this.normalize(raw.brand),
            manufacturer: this.normalize(raw.manufacturer),
            model: this.normalize(raw.model),
            partNumber: this.normalize(raw.partNumber),
            serialNumber: this.normalize(raw.serialNumber),
            color: this.normalize(raw.color),
            dimensions: this.normalize(raw.dimensions),
            weight: this.normalize(raw.weight),
            unit: this.normalize(raw.unit),
            quantity: raw.quantity,
            description: this.normalize(raw.description),
            notes: this.normalize(raw.notes),
        };
    }

    private normalize(value: string): string | null {
        const trimmedValue = value.trim();
        return trimmedValue ? trimmedValue : null;
    }

}
