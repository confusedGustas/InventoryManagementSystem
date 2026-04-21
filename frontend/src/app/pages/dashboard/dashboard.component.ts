import {Component, computed, inject, signal} from '@angular/core';
import {AuthUtils} from '../../auth/auth.utils';
import {UserRole} from '../../shared/enums';
import {DashboardService, DashboardDto} from './dashboard.service';
import {ToastService} from '../../shared/toast/toast.service';
import {DashboardCompanyFilterComponent} from './company-filter/dashboard-company-filter.component';
import {DashboardSummaryCardsComponent} from './summary-cards/dashboard-summary-cards.component';
import {DashboardCompanyBreakdownComponent} from './company-breakdown/dashboard-company-breakdown.component';
import {DashboardLocationBreakdownComponent} from './location-breakdown/dashboard-location-breakdown.component';
import {DashboardInventoryBreakdownComponent} from './inventory-breakdown/dashboard-inventory-breakdown.component';
import {DashboardCategoryBreakdownComponent} from './category-breakdown/dashboard-category-breakdown.component';
import {DashboardRecentItemsComponent} from './recent-items/dashboard-recent-items.component';

@Component({
    selector: 'app-dashboard',
    standalone: true,
    imports: [
        DashboardCompanyFilterComponent,
        DashboardSummaryCardsComponent,
        DashboardCompanyBreakdownComponent,
        DashboardLocationBreakdownComponent,
        DashboardInventoryBreakdownComponent,
        DashboardCategoryBreakdownComponent,
        DashboardRecentItemsComponent,
    ],
    templateUrl: './dashboard.component.html'
})
export class DashboardComponent {

    private readonly dashboardService = inject(DashboardService);
    private readonly toast = inject(ToastService);

    protected readonly dashboard = signal<DashboardDto | null>(null);
    protected readonly loading = signal(true);
    protected readonly selectedCompanyId = signal<string | null>(null);
    protected readonly isPlatformAdmin = AuthUtils.hasRole(UserRole.PLATFORM_ADMIN);
    protected readonly pageTitle = computed(() => {
        const selectedCompany = this.dashboard()?.selectedCompany;
        if (selectedCompany) {
            return `${selectedCompany.name} Dashboard`;
        }

        return this.isPlatformAdmin ? 'Platform Dashboard' : 'Company Dashboard';
    });

    constructor() {
        this.loadDashboard();
    }

    protected selectCompany(companyId: string | null): void {
        this.selectedCompanyId.set(companyId);
        this.loadDashboard();
    }

    private loadDashboard(): void {
        this.loading.set(true);

        this.dashboardService.getDashboard(this.selectedCompanyId()).subscribe({
            next: (dashboard) => {
                this.dashboard.set(dashboard);
                this.selectedCompanyId.set(dashboard.selectedCompany?.id ?? null);
                this.loading.set(false);
            },
            error: () => {
                this.toast.error('Unable to load dashboard.');
                this.loading.set(false);
            },
        });
    }

}
