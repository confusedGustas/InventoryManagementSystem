import {Component, effect, inject, signal} from '@angular/core';
import {Router} from '@angular/router';
import {ApiKeyDto, UserDto, SettingsService} from './settings.service';
import {UserBannerComponent} from './user-banner/user-banner.component';
import {ProfileDetailsComponent} from './profile-details/profile-details.component';
import {ChangeEmailComponent} from './change-email/change-email.component';
import {ChangePasswordComponent} from './change-password/change-password.component';
import {ToastService} from '../../shared/toast/toast.service';
import {AuthService} from '../../auth/auth.service';
import {ApiKeyComponent} from './api-key/api-key.component';
import {UserRole} from '../../shared/enums';

@Component({
    selector: 'app-user',
    standalone: true,
    imports: [
        UserBannerComponent,
        ProfileDetailsComponent,
        ChangeEmailComponent,
        ChangePasswordComponent,
        ApiKeyComponent
    ],
    templateUrl: './settings.component.html'
})
export class SettingsComponent {

    private readonly userService = inject(SettingsService);
    private readonly toast = inject(ToastService);
    private readonly authService = inject(AuthService);
    private readonly router = inject(Router);

    protected readonly profile = signal<UserDto | null>(null);
    protected readonly companyApiKey = signal<ApiKeyDto | null>(null);
    protected readonly showApiKey = signal(false);

    constructor() {
        this.loadProfile();

        effect(() => {
            const profile = this.profile();
            const shouldShowApiKey = profile?.userRole === UserRole.COMPANY_ADMIN;
            this.showApiKey.set(shouldShowApiKey);

            if (shouldShowApiKey && !this.companyApiKey()) {
                this.loadCompanyApiKey();
            }
        });
    }

    protected updateProfile(profile: UserDto): void {
        this.profile.set(profile);
    }

    protected updateCompanyApiKey(companyApiKey: ApiKeyDto): void {
        this.companyApiKey.set(companyApiKey);
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

    private loadCompanyApiKey(): void {
        this.userService.getCompanyApiKey().subscribe({
            next: (companyApiKey) => {
                this.companyApiKey.set(companyApiKey);
            },
            error: () => {
                this.toast.error('Failed to load company API key. Please try again.');
            }
        });
    }

}
