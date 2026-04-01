import {Component, effect, inject, input, output} from '@angular/core';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {UserDto, SettingsService} from '../settings.service';
import {ToastService} from '../../../shared/toast/toast.service';

@Component({
    selector: 'app-change-email',
    standalone: true,
    imports: [ReactiveFormsModule],
    templateUrl: './change-email.component.html'
})
export class ChangeEmailComponent {

    private readonly userService = inject(SettingsService);
    private readonly formBuilder = inject(FormBuilder);
    private readonly toast = inject(ToastService);

    email = input.required<string>();

    profileUpdated = output<UserDto>();

    protected readonly form = this.formBuilder.nonNullable.group({
        email: ['', [Validators.required, Validators.email]],
    });

    constructor() {
        effect(() => {
            this.form.patchValue({ email: this.email() }, { emitEvent: false });
        });
    }

    protected submit(): void {
        if (this.form.invalid) {
            this.form.markAllAsTouched();
            this.toast.error("Invalid Email");

            return;
        }

        this.userService.changeEmail(this.form.getRawValue()).subscribe({
            next: (profile) => {
                this.form.patchValue({ email: profile.email }, { emitEvent: false });
                this.profileUpdated.emit(profile);
            },
            error: () => {
                this.toast.error('Failed to change email. Please try again.');
            }
        });
    }

}
