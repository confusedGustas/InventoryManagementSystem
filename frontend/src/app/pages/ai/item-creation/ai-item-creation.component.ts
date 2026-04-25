import {Component, computed, effect, inject, input, output, signal} from '@angular/core';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {AuthUtils} from '../../../auth/auth.utils';
import {UserRole} from '../../../shared/enums';
import {ToastService} from '../../../shared/toast/toast.service';
import {InventoryDto, InventoryService} from '../../inventory/inventory.service';
import {ItemFormCardComponent} from '../../inventory/item-form-card/item-form-card.component';
import {AiInventoryOptionDto, AiItemSuggestionDto} from '../ai.service';

@Component({
    selector: 'app-ai-item-creation',
    standalone: true,
    imports: [ReactiveFormsModule, ItemFormCardComponent],
    templateUrl: './ai-item-creation.component.html'
})
export class AiItemCreationComponent {

    private readonly formBuilder = inject(FormBuilder);
    private readonly inventoryService = inject(InventoryService);
    private readonly toast = inject(ToastService);

    readonly suggestion = input.required<AiItemSuggestionDto | null>();
    readonly itemSaved = output<void>();

    protected readonly selectedInventoryId = signal<string | null>(null);
    protected readonly saving = signal(false);
    protected readonly isPlatformAdmin = AuthUtils.hasRole(UserRole.PLATFORM_ADMIN);
    protected readonly isCompanyAdmin = AuthUtils.hasRole(UserRole.COMPANY_ADMIN);
    protected readonly isCompanyUser = AuthUtils.hasRole(UserRole.COMPANY_USER);
    protected readonly canSaveItems = this.isPlatformAdmin || this.isCompanyAdmin || this.isCompanyUser;

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
        quantity: [1, [Validators.required, Validators.min(0)]],
        description: [''],
        notes: [''],
    });

    protected readonly selectedInventory = computed<InventoryDto | null>(() => {
        const inventoryId = this.selectedInventoryId();
        const option = this.suggestion()?.inventories.find(currentInventory => currentInventory.id === inventoryId);
        if (!option) {
            return null;
        }

        return {
            id: option.id,
            name: option.name,
            code: '',
            description: null,
            enabled: true,
            createdOn: '',
            companyId: option.companyId,
            companyName: option.companyName,
            locationId: option.locationId,
            locationName: option.locationName,
        };
    });

    constructor() {
        effect(() => {
            const suggestion = this.suggestion();
            if (!suggestion) {
                this.selectedInventoryId.set(null);
                this.resetForm();
                return;
            }

            this.selectedInventoryId.set(null);
            this.itemForm.patchValue({
                name: suggestion.item.name ?? '',
                sku: suggestion.item.sku ?? '',
                category: suggestion.item.category ?? '',
                brand: suggestion.item.brand ?? '',
                manufacturer: suggestion.item.manufacturer ?? '',
                model: suggestion.item.model ?? '',
                partNumber: suggestion.item.partNumber ?? '',
                serialNumber: suggestion.item.serialNumber ?? '',
                color: suggestion.item.color ?? '',
                dimensions: suggestion.item.dimensions ?? '',
                weight: suggestion.item.weight ?? '',
                unit: suggestion.item.unit ?? '',
                quantity: suggestion.item.quantity ?? 1,
                description: suggestion.item.description ?? '',
                notes: suggestion.item.notes ?? '',
            }, { emitEvent: false });
        });
    }

    protected selectInventory(inventoryId: string): void {
        this.selectedInventoryId.set(inventoryId || null);
    }

    protected saveItem(): void {
        if (!this.canSaveItems) {
            this.toast.error('You do not have permission to create items.');
            return;
        }

        const inventoryId = this.selectedInventoryId();
        if (!inventoryId) {
            this.toast.error('Select an inventory before saving the suggested item.');
            return;
        }

        if (this.itemForm.invalid) {
            this.itemForm.markAllAsTouched();
            this.toast.error('Item information was invalid or incomplete. Please check the form and try again.');
            return;
        }

        this.saving.set(true);
        const raw = this.itemForm.getRawValue();
        this.inventoryService.saveItem({
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
        }).subscribe({
            next: () => {
                this.toast.success('Item created from AI suggestion.');
                this.saving.set(false);
                this.itemSaved.emit();
            },
            error: () => {
                this.toast.error('Unable to save item from AI suggestion.');
                this.saving.set(false);
            },
        });
    }

    private resetForm(): void {
        this.itemForm.reset({
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
            quantity: 1,
            description: '',
            notes: '',
        }, { emitEvent: false });
    }

    private normalize(value: string): string | null {
        const trimmedValue = value.trim();
        return trimmedValue ? trimmedValue : null;
    }

    protected readonly inventoryOptions = computed<AiInventoryOptionDto[]>(() => this.suggestion()?.inventories ?? []);
}
