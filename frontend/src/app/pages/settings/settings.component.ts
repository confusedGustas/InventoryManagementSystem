import {Component, inject, signal} from '@angular/core';
import {Router} from '@angular/router';
import {UserDto, SettingsService} from './settings.service';
import {UserBannerComponent} from './user-banner/user-banner.component';
import {ProfileDetailsComponent} from './profile-details/profile-details.component';
import {ChangeEmailComponent} from './change-email/change-email.component';
import {ChangePasswordComponent} from './change-password/change-password.component';
import {ToastService} from '../../shared/toast/toast.service';
import {AuthService} from '../../auth/auth.service';

@Component({
    selector: 'app-user',
    standalone: true,
    imports: [
        UserBannerComponent,
        ProfileDetailsComponent,
        ChangeEmailComponent,
        ChangePasswordComponent
    ],
    templateUrl: './settings.component.html'
})
export class SettingsComponent {

    private readonly userService = inject(SettingsService);
    private readonly toast = inject(ToastService);
    private readonly authService = inject(AuthService);
    private readonly router = inject(Router);

    protected readonly profile = signal<UserDto | null>(null);

    constructor() {
        this.loadProfile();
    }

    protected updateProfile(profile: UserDto): void {
        this.profile.set(profile);
    }

    protected async logout(): Promise<void> {
        this.authService.logout();

        try {
            await this.router.navigate(['/login']);
        } catch (err) {
            console.error(err);
        }
    }

    private loadProfile(): void {
        this.userService.getProfile().subscribe({
            next: (profile) => {
                this.profile.set(profile);
            },
            error: () => {
                this.toast.error('Failed to load user profile. Please try again.');
            }
        });
    }

}
