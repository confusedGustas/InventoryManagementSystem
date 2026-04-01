import {Component, inject, input, output, signal} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {AuthUtils} from '../../../auth/auth.utils';
import {UserRole} from '../../../shared/enums';
import {CompanyOptionDto, LocationOptionDto} from '../../../shared/models/common.models';
import {ToastService} from '../../../shared/toast/toast.service';
import {InventoryDto, InventoryService} from '../inventory.service';
import {InventoryFormCardComponent} from '../inventory-form-card/inventory-form-card.component';
import {InventoryListCardComponent} from '../inventory-list-card/inventory-list-card.component';

@Component({
    selector: 'app-inventory-management',
    standalone: true,
    imports: [InventoryFormCardComponent, InventoryListCardComponent],
    templateUrl: './inventory-management.component.html'
})
export class InventoryManagementComponent {

    private readonly inventoryService = inject(InventoryService);
    private readonly formBuilder = inject(FormBuilder);
    private readonly toast = inject(ToastService);

    readonly showForm = input.required<boolean>();
    readonly selectedInventoryId = input.required<string | null>();

    readonly showFormChange = output<boolean>();
    readonly selectedInventoryChanged = output<InventoryDto | null>();

    protected readonly inventories = signal<InventoryDto[]>([]);
    protected readonly companies = signal<CompanyOptionDto[]>([]);
    protected readonly locations = signal<LocationOptionDto[]>([]);
    protected readonly editingInventoryId = signal<string | null>(null);
    protected readonly pagination = signal({ page: 1, totalPages: 1 });

    protected readonly isPlatformAdmin = AuthUtils.hasRole(UserRole.PLATFORM_ADMIN);
    protected readonly isCompanyAdmin = AuthUtils.hasRole(UserRole.COMPANY_ADMIN);
    protected readonly canManageInventories = this.isPlatformAdmin || this.isCompanyAdmin;

    protected readonly inventoryForm = this.formBuilder.nonNullable.group({
        name: ['', Validators.required],
        code: ['', Validators.required],
        description: [''],
        companyId: [''],
        locationId: [''],
    });

    constructor() {
        this.loadInventories();
    }

    protected goToPage(page: number): void {
        if (page < 1 || page > this.pagination().totalPages) {
            return;
        }

        this.pagination.update(current => ({ ...current, page }));
        this.loadInventories();
    }

    protected selectInventory(inventory: InventoryDto): void {
        if (this.selectedInventoryId() === inventory.id) {
            this.selectedInventoryChanged.emit(null);
            return;
        }

        this.selectedInventoryChanged.emit(inventory);
    }

    protected submitInventory(): void {
        if (this.inventoryForm.invalid) {
            this.inventoryForm.markAllAsTouched();
            this.toast.error('Inventory information was invalid or incomplete. Please check the form and try again.');
            return;
        }

        const editingId = this.editingInventoryId();
        const request = editingId
            ? this.inventoryService.updateInventory(editingId, this.toInventoryPayload())
            : this.inventoryService.saveInventory(this.toInventoryPayload());

        request.subscribe({
            next: (inventory) => {
                this.resetForm();
                if (editingId) {
                    this.inventories.update(list => list.map(item => item.id === inventory.id ? inventory : item));
                    if (this.selectedInventoryId() === inventory.id) {
                        this.selectedInventoryChanged.emit(inventory);
                    }
                    return;
                }

                this.loadInventories();
            },
            error: () => this.toast.error(editingId
                ? 'Unable to update inventory. Please try again.'
                : 'Unable to create inventory. Please try again.'),
        });
    }

    protected editInventory(inventory: InventoryDto): void {
        this.editingInventoryId.set(inventory.id);
        this.showFormChange.emit(true);
        this.inventoryForm.setValue({
            name: inventory.name,
            code: inventory.code,
            description: inventory.description ?? '',
            companyId: inventory.companyId,
            locationId: inventory.locationId ?? '',
        });
    }

    protected toggleInventoryEnabled(inventory: InventoryDto): void {
        const isEnabled = inventory.enabled;
        const request = isEnabled
            ? this.inventoryService.disableInventory(inventory.id)
            : this.inventoryService.enableInventory(inventory.id);

        request.subscribe({
            next: (updatedInventory) => {
                if (this.editingInventoryId() === inventory.id) {
                    this.resetForm();
                }

                this.inventories.update(list => list.map(item => item.id === updatedInventory.id ? updatedInventory : item));
                if (this.selectedInventoryId() === updatedInventory.id) {
                    this.selectedInventoryChanged.emit(updatedInventory);
                }
            },
            error: () => this.toast.error(isEnabled
                ? 'Unable to disable inventory. Please try again.'
                : 'Unable to enable inventory. Please try again.'),
        });
    }

    protected availableLocations(): LocationOptionDto[] {
        if (this.isCompanyAdmin) {
            return this.locations();
        }

        const companyId = this.inventoryForm.get('companyId')?.value ?? '';
        if (!companyId) {
            return this.locations();
        }

        return this.locations().filter(location => location.companyId === companyId);
    }

    private loadInventories(): void {
        this.inventoryService.getInventories(this.pagination().page).subscribe({
            next: (response) => {
                this.pagination.set({
                    page: response.page,
                    totalPages: Math.max(response.totalPages, 1),
                });
                this.inventories.set(response.content);
                this.companies.set(response.companies);
                this.locations.set(response.locations);

                const matchingInventory = response.content.find(inventory => inventory.id === this.selectedInventoryId()) ?? null;
                this.selectedInventoryChanged.emit(matchingInventory);
            },
            error: () => this.toast.error('Unable to load inventories.'),
        });
    }

    private resetForm(): void {
        this.inventoryForm.reset();
        this.inventoryForm.patchValue({
            name: '',
            code: '',
            description: '',
            companyId: '',
            locationId: '',
        });
        this.editingInventoryId.set(null);
        this.showFormChange.emit(false);
    }

    private toInventoryPayload() {
        const raw = this.inventoryForm.getRawValue();

        return {
            ...raw,
            description: this.normalize(raw.description),
            companyId: this.isCompanyAdmin ? null : this.normalize(raw.companyId),
            locationId: this.normalize(raw.locationId),
        };
    }

    private normalize(value: string): string | null {
        const trimmedValue = value.trim();
        return trimmedValue ? trimmedValue : null;
    }

}
