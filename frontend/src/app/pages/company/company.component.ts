import {Component, inject} from '@angular/core';
import {DatePipe} from '@angular/common';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {CompanyStatus, formatEnumLabel} from '../../shared/enums';
import {CompanyDto, CompanyService} from './company.service';
import {ToastService} from '../../shared/toast/toast.service';
import {CrudListBase} from '../../shared/crud/crud-list.base';
import {getToggleButtonClass} from '../../shared/utils/ui.utils';

@Component({
    selector: 'app-company',
    standalone: true,
    imports: [ReactiveFormsModule, DatePipe],
    templateUrl: './company.component.html'
})
export class CompanyComponent extends CrudListBase<CompanyDto> {

    private readonly companyService = inject(CompanyService);
    private readonly formBuilder = inject(FormBuilder);
    private readonly toast = inject(ToastService);

    protected readonly companies = this.items;
    protected readonly editingCompanyId = this.editingItemId;
    protected readonly statusOptions = Object.values(CompanyStatus);
    protected readonly formatEnumLabel = formatEnumLabel;
    protected readonly getToggleButtonClass = getToggleButtonClass;

    protected readonly companyForm = this.formBuilder.nonNullable.group({
        name: ['', Validators.required],
        contactEmail: ['', [Validators.required, Validators.email]],
        contactPhone: ['', Validators.required],
        city: ['', Validators.required],
        postalCode: ['', Validators.required],
        country: ['', Validators.required],
        status: [CompanyStatus.ONBOARDING, Validators.required],
    });

    constructor() {
        super(inject(ToastService));
        this.loadCompanies();
    }

    protected toggleCreateForm(): void {
        this.toggleCreateFormState(() => this.resetForm());
    }

    protected override goToPage(page: number): void {
        super.goToPage(page, () => this.loadCompanies());
    }

    protected submit(): void {
        const editingId = this.editingCompanyId();
        this.submitCrud(this.companyForm, 'Company information was invalid or incomplete. Please check the form and try again.', {
            editingId,
            createRequest: this.companyService.saveCompany(this.companyForm.getRawValue()),
            updateRequest: this.companyService.updateCompany(editingId ?? '', this.companyForm.getRawValue()),
            createErrorMessage: 'Unable to create company. Please try again.',
            updateErrorMessage: 'Unable to update company. Please try again.',
            resetForm: () => this.resetForm(),
            onCreateSuccess: () => this.loadCompanies(),
            onUpdateSuccess: (company) => this.replaceItem(company),
        });
    }

    protected editCompany(company: CompanyDto): void {
        this.startEditing(company, (selectedCompany) => {
            this.companyForm.setValue({
                name: selectedCompany.name,
                contactEmail: selectedCompany.contactEmail,
                contactPhone: selectedCompany.contactPhone,
                city: selectedCompany.city,
                postalCode: selectedCompany.postalCode,
                country: selectedCompany.country,
                status: selectedCompany.status as CompanyStatus,
            });
        });
    }

    protected toggleCompanyStatus(company: CompanyDto): void {
        const isSuspended = company.status === CompanyStatus.SUSPENDED;
        this.toggleItemState({
            item: company,
            request: isSuspended
                ? this.companyService.enableCompany(company.id)
                : this.companyService.disableCompany(company.id),
            activeErrorMessage: 'Unable to disable company. Please try again.',
            inactiveErrorMessage: 'Unable to enable company. Please try again.',
            resetForm: () => this.resetForm(),
        }, !isSuspended);
    }

    private loadCompanies(): void {
        this.companyService.getCompanies(this.pagination().page).subscribe({
            next: (response) => {
                this.applyPage(response);
            },
            error: () => this.toast.error('Unable to load companies.'),
        });
    }

    private resetForm(): void {
        this.companyForm.reset();
        this.companyForm.get('status')?.setValue(CompanyStatus.ONBOARDING);
        this.resetCrudState();
    }
}
