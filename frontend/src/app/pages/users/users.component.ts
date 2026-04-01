import {Component, inject, signal} from '@angular/core';
import {DatePipe} from '@angular/common';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {UserRole, formatEnumLabel} from '../../shared/enums';
import {UserDto, SettingsService} from '../settings/settings.service';
import {ToastService} from '../../shared/toast/toast.service';
import {AuthUtils} from '../../auth/auth.utils';
import {CompanyOptionDto} from '../../shared/models/common.models';
import {CrudListBase} from '../../shared/crud/crud-list.base';
import {getToggleButtonClass} from '../../shared/utils/ui.utils';

@Component({
    selector: 'app-users',
    standalone: true,
    imports: [ReactiveFormsModule, DatePipe],
    templateUrl: './users.component.html'
})
export class UsersComponent extends CrudListBase<UserDto> {

    private readonly userService = inject(SettingsService);
    private readonly formBuilder = inject(FormBuilder);
    private readonly toast = inject(ToastService);

    protected readonly users = this.items;
    protected readonly companies = signal<CompanyOptionDto[]>([]);
    protected readonly editingUserId = this.editingItemId;
    protected readonly roleOptions = this.resolveRoleOptions();
    protected readonly formatEnumLabel = formatEnumLabel;
    protected readonly isCompanyAdmin = AuthUtils.hasRole(UserRole.COMPANY_ADMIN);
    protected readonly getToggleButtonClass = getToggleButtonClass;

    protected readonly userForm = this.formBuilder.nonNullable.group({
        firstName: ['', Validators.required],
        lastName: ['', Validators.required],
        username: ['', Validators.required],
        email: ['', [Validators.required, Validators.email]],
        password: ['', Validators.required],
        userRole: [UserRole.COMPANY_USER, Validators.required],
        companyId: [''],
    });

    constructor() {
        super(inject(ToastService));
        this.loadUsers();
    }

    protected toggleCreateForm(): void {
        this.toggleCreateFormState(() => this.resetForm());
    }

    protected override goToPage(page: number): void {
        super.goToPage(page, () => this.loadUsers());
    }

    protected submit(): void {
        const editingUserId = this.editingUserId();
        this.submitCrud(this.userForm, 'User information was invalid or incomplete. Please check the form and try again.', {
            editingId: editingUserId,
            createRequest: this.userService.createUser(this.toCreatePayload()),
            updateRequest: this.userService.updateUser(editingUserId ?? '', this.toUpdatePayload()),
            createErrorMessage: 'Unable to create user. Please try again.',
            updateErrorMessage: 'Unable to update user. Please try again.',
            resetForm: () => this.resetForm(),
            onCreateSuccess: () => this.loadUsers(),
            onUpdateSuccess: (user) => this.replaceItem(user),
        });
    }

    protected editUser(user: UserDto): void {
        this.startEditing(user, (selectedUser) => {
            this.userForm.setValue({
                firstName: selectedUser.firstName,
                lastName: selectedUser.lastName,
                username: selectedUser.username,
                email: selectedUser.email,
                password: '',
                userRole: selectedUser.userRole as UserRole,
                companyId: selectedUser.companyId ?? '',
            });
            this.userForm.get('password')?.clearValidators();
            this.userForm.get('password')?.updateValueAndValidity();
        });
    }

    protected toggleUserEnabled(user: UserDto): void {
        this.toggleItemState({
            item: user,
            request: user.enabled
                ? this.userService.disableUser(user.id)
                : this.userService.enableUser(user.id),
            activeErrorMessage: 'Unable to disable user. Please try again.',
            inactiveErrorMessage: 'Unable to enable user. Please try again.',
            resetForm: () => this.resetForm(),
        }, user.enabled);
    }

    protected isEditing(): boolean {
        return this.editingUserId() !== null;
    }

    private loadUsers(): void {
        this.userService.getUsers(this.pagination().page).subscribe({
            next: (response) => {
                this.applyPage(response);
                this.companies.set(response.companies);
            },
            error: () => this.toast.error('Unable to load users.'),
        });
    }

    private resetForm(): void {
        this.userForm.reset();
        this.userForm.patchValue({
            firstName: '',
            lastName: '',
            username: '',
            email: '',
            password: '',
            userRole: UserRole.COMPANY_USER,
            companyId: '',
        });
        this.userForm.get('password')?.setValidators([Validators.required]);
        this.userForm.get('password')?.updateValueAndValidity();
        this.resetCrudState();
    }

    private toCreatePayload() {
        const raw = this.userForm.getRawValue();
        return {
            ...raw,
            companyId: this.isCompanyAdmin ? null : raw.companyId || null,
        };
    }

    private toUpdatePayload() {
        const { password, ...raw } = this.userForm.getRawValue();
        return {
            ...raw,
            companyId: this.isCompanyAdmin ? null : raw.companyId || null,
        };
    }

    private resolveRoleOptions(): UserRole[] {
        if (AuthUtils.hasRole(UserRole.COMPANY_ADMIN)) {
            return [
                UserRole.COMPANY_ADMIN,
                UserRole.COMPANY_USER,
                UserRole.COMPANY_FINANCE,
            ];
        }

        return Object.values(UserRole);
    }

}
