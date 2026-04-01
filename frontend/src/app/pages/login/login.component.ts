import {Component, inject, signal} from '@angular/core';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {AuthService} from '../../auth/auth.service';
import {ToastService} from '../../shared/toast/toast.service';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [ReactiveFormsModule, RouterLink],
    templateUrl: './login.component.html'
})
export class LoginComponent {

    protected readonly auth = inject(AuthService);
    private readonly formBuilder = inject(FormBuilder);
    private readonly router = inject(Router);
    private readonly toast = inject(ToastService);

    protected readonly loginForm = this.formBuilder.nonNullable.group({
        username: ['', Validators.required],
        password: ['', Validators.required],
    });

    submit(): void {
        if (this.loginForm.invalid) {
            this.loginForm.markAllAsTouched();
            this.toast.error("Information was invalid or incomplete. Please check the form and try again.");

            return;
        }

        this.auth.login(this.loginForm.getRawValue()).subscribe({
            next: async () => {
                try {
                    await this.router.navigate(['/app/dashboard']);
                } catch (err) {
                    console.error(err);
                }
            },
            error: () => {
                this.toast.error('Login failed. Please check your credentials and try again.');
            }
        });
    }
}
