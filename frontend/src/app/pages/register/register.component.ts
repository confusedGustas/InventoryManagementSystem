import {Component, inject} from '@angular/core';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {AuthService} from '../../auth/auth.service';
import {ToastService} from '../../shared/toast/toast.service';

@Component({
    selector: 'app-register',
    standalone: true,
    imports: [ReactiveFormsModule, RouterLink],
    templateUrl: './register.component.html'
})
export class RegisterComponent {

    private readonly auth = inject(AuthService);
    private readonly formBuilder = inject(FormBuilder);
    private readonly router = inject(Router);
    private readonly toast = inject(ToastService);

    protected readonly registerForm = this.formBuilder.nonNullable.group({
        firstName: ['', Validators.required],
        lastName: ['', Validators.required],
        username: ['', Validators.required],
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.minLength(6)]],
    });

    protected submit(): void {
        if (this.registerForm.invalid) {
            this.registerForm.markAllAsTouched();
            this.toast.error('Information was invalid or incomplete. Please check the form and try again.');
            return;
        }

        this.auth.register(this.registerForm.getRawValue()).subscribe({
            next: async () => {
                try {
                    await this.router.navigate(['/app/dashboard']);
                } catch (err) {
                    console.error(err);
                }
            },
            error: () => {
                this.toast.error('Registration failed. Please try again.');
            }
        });
    }

}
