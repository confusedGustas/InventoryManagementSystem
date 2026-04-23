import {Routes} from '@angular/router';
import {LayoutComponent} from './layout/layout.component';
import {LoginComponent} from './pages/login/login.component';
import {RegisterComponent} from './pages/register/register.component';
import {DashboardComponent} from './pages/dashboard/dashboard.component';
import {CompanyComponent} from './pages/company/company.component';
import {LocationComponent} from './pages/location/location.component';
import {InventoryComponent} from './pages/inventory/inventory.component';
import {AnalyticComponent} from './pages/analytic/analytic.component';
import {AiComponent} from './pages/ai/ai.component';
import {authGuard} from './auth/auth.guard';
import {guestGuard} from './auth/guest.guard';
import {SettingsComponent} from './pages/settings/settings.component';
import {UsersComponent} from './pages/users/users.component';
import {UserRole} from './shared/enums';

export const routes: Routes = [
    { path: '', component: LoginComponent, canActivate: [guestGuard] },
    { path: 'login', component: LoginComponent, canActivate: [guestGuard] },
    { path: 'register', component: RegisterComponent, canActivate: [guestGuard] },
    {
        path: 'app',
        component: LayoutComponent,
        canActivate: [authGuard],
        children: [
            {
                path: '',
                redirectTo: 'dashboard',
                pathMatch: 'full'
            },
            {
                path: 'dashboard',
                component: DashboardComponent,
                data: { label: 'Dashboard', icon: 'dashboard' }
            },
            {
                path: 'companies',
                component: CompanyComponent,
                canActivate: [authGuard],
                data: { label: 'Companies', icon: 'business', roles: [UserRole.PLATFORM_ADMIN] }
            },
            {
                path: 'locations',
                component: LocationComponent,
                canActivate: [authGuard],
                data: { label: 'Locations', icon: 'location_on', roles: [UserRole.PLATFORM_ADMIN, UserRole.COMPANY_ADMIN] }
            },
            {
                path: 'users',
                component: UsersComponent,
                canActivate: [authGuard],
                data: { label: 'Users', icon: 'group', roles: [UserRole.PLATFORM_ADMIN, UserRole.COMPANY_ADMIN] }
            },
            {
                path: 'settings',
                component: SettingsComponent,
                data: { label: 'Settings', icon: 'settings' }
            },
            {
                path: 'user',
                redirectTo: 'settings',
                pathMatch: 'full'
            },
            {
                path: 'inventory',
                component: InventoryComponent,
                data: { label: 'Inventory', icon: 'inventory_2', roles: [UserRole.PLATFORM_ADMIN, UserRole.COMPANY_ADMIN, UserRole.COMPANY_USER] }
            },
            {
                path: 'scanner',
                redirectTo: 'inventory',
                pathMatch: 'full'
            },
            {
                path: 'analytics',
                component: AnalyticComponent,
                data: { label: 'Analytics', icon: 'analytics', roles: [UserRole.PLATFORM_ADMIN, UserRole.COMPANY_ADMIN, UserRole.COMPANY_USER, UserRole.COMPANY_FINANCE] }
            },
            {
                path: 'ai',
                component: AiComponent,
                data: { label: 'AI', icon: 'robot', roles: [UserRole.COMPANY_USER] }
            },
        ]
    },
    { path: '**', redirectTo: '' }
];
