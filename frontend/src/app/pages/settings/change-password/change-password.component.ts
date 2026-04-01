import {Component, inject} from '@angular/core';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {SettingsService} from '../settings.service';
import {ToastService} from '../../../shared/toast/toast.service';

@Component({
    selector: 'app-change-password',
    standalone: true,
    imports: [ReactiveFormsModule],
    templateUrl: './change-password.component.html'
})
export class ChangePasswordComponent {

    private readonly userService = inject(SettingsService);
    private readonly formBuilder = inject(FormBuilder);
    private readonly toast = inject(ToastService);

    protected readonly form = this.formBuilder.nonNullable.group({
        currentPassword: ['', Validators.required],
        newPassword: ['', [Validators.required]],
    });

    protected submit(): void {
        if (this.form.invalid) {
            this.form.markAllAsTouched();
            return;
        }

        this.userService.changePassword(this.form.getRawValue()).subscribe({
            next: () => {
                this.form.reset({
                    currentPassword: '',
                    newPassword: '',
                });
            },
            error: () => {
                this.toast.error('Failed to change password. Please try again.');
            }
        });
    }

}
