import {Component, input, output} from '@angular/core';
import {ReactiveFormsModule, FormGroup} from '@angular/forms';
import {CompanyOptionDto, LocationOptionDto} from '../../../shared/models/common.models';

@Component({
    selector: 'app-inventory-form-card',
    standalone: true,
    imports: [ReactiveFormsModule],
    templateUrl: './inventory-form-card.component.html'
})
export class InventoryFormCardComponent {

    readonly editingInventoryId = input.required<string | null>();
    readonly inventoryForm = input.required<FormGroup>();
    readonly companies = input.required<CompanyOptionDto[]>();
    readonly availableLocations = input.required<LocationOptionDto[]>();
    readonly isCompanyAdmin = input.required<boolean>();

    readonly submitted = output<void>();

}
