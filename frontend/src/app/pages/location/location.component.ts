import {Component, inject, signal} from '@angular/core';
import {DatePipe} from '@angular/common';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {ToastService} from '../../shared/toast/toast.service';
import {LocationDto, LocationService} from './location.service';
import {AuthUtils} from '../../auth/auth.utils';
import {UserRole} from '../../shared/enums';
import {CompanyOptionDto} from '../../shared/models/common.models';
import {CrudListBase} from '../../shared/crud/crud-list.base';
import {getToggleButtonClass} from '../../shared/utils/ui.utils';

@Component({
    selector: 'app-location',
    standalone: true,
    imports: [ReactiveFormsModule, DatePipe],
    templateUrl: './location.component.html'
})
export class LocationComponent extends CrudListBase<LocationDto> {

    private readonly locationService = inject(LocationService);
    private readonly formBuilder = inject(FormBuilder);
    private readonly toast = inject(ToastService);

    protected readonly companies = signal<CompanyOptionDto[]>([]);
    protected readonly locations = this.items;
    protected readonly editingLocationId = this.editingItemId;
    protected readonly isCompanyAdmin = AuthUtils.hasRole(UserRole.COMPANY_ADMIN);
    protected readonly getToggleButtonClass = getToggleButtonClass;

    protected readonly locationForm = this.formBuilder.nonNullable.group({
        name: ['', Validators.required],
        address: ['', Validators.required],
        phone: ['', Validators.required],
        domain: ['', Validators.required],
        companyId: ['', Validators.required],
    });

    constructor() {
        super(inject(ToastService));
        this.loadLocations();
    }

    protected toggleCreateForm(): void {
        this.toggleCreateFormState(() => this.resetForm());
    }

    protected override goToPage(page: number): void {
        super.goToPage(page, () => this.loadLocations());
    }

    protected submit(): void {
        const editingLocationId = this.editingLocationId();
        this.submitCrud(this.locationForm, 'Location information was invalid or incomplete. Please check the form and try again.', {
            editingId: editingLocationId,
            createRequest: this.locationService.saveLocation(this.locationForm.getRawValue()),
            updateRequest: this.locationService.updateLocation(editingLocationId ?? '', this.locationForm.getRawValue()),
            createErrorMessage: 'Unable to create location. Please try again.',
            updateErrorMessage: 'Unable to update location. Please try again.',
            resetForm: () => this.resetForm(),
            onCreateSuccess: () => this.loadLocations(),
            onUpdateSuccess: (location) => this.replaceItem(location),
        });
    }

    protected editLocation(location: LocationDto): void {
        this.startEditing(location, (selectedLocation) => {
            this.locationForm.setValue({
                name: selectedLocation.name,
                address: selectedLocation.address,
                phone: selectedLocation.phone,
                domain: selectedLocation.domain,
                companyId: selectedLocation.companyId,
            });
        });
    }

    protected toggleLocationEnabled(location: LocationDto): void {
        const isEnabled = location.enabled;
        this.toggleItemState({
            item: location,
            request: isEnabled
                ? this.locationService.disableLocation(location.id)
                : this.locationService.enableLocation(location.id),
            activeErrorMessage: 'Unable to disable location. Please try again.',
            inactiveErrorMessage: 'Unable to enable location. Please try again.',
            resetForm: () => this.resetForm(),
        }, isEnabled);
    }

    private loadLocations(): void {
        this.locationService.getLocations(this.pagination().page).subscribe({
            next: (response) => {
                this.applyPage(response);
                this.companies.set(response.companies);
            },
            error: () => this.toast.error('Unable to load locations.'),
        });
    }

    private resetForm(): void {
        this.locationForm.reset();
        this.resetCrudState();
    }

}
