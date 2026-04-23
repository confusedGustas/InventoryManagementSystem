import {Component, computed, inject, signal} from '@angular/core';
import {AuthUtils} from '../../auth/auth.utils';
import {UserRole} from '../../shared/enums';
import {ToastService} from '../../shared/toast/toast.service';
import {AnalyticService, AnalyticsDto} from './analytic.service';
import {AnalyticCompanyFilterComponent} from './company-filter/analytic-company-filter.component';
import {AnalyticSummaryCardsComponent} from './summary-cards/analytic-summary-cards.component';
import {AnalyticStockHealthComponent} from './stock-health/analytic-stock-health.component';
import {AnalyticCategoryInsightsComponent} from './category-insights/analytic-category-insights.component';
import {AnalyticBrandInsightsComponent} from './brand-insights/analytic-brand-insights.component';
import {AnalyticLocationInsightsComponent} from './location-insights/analytic-location-insights.component';
import {AnalyticDataQualityComponent} from './data-quality/analytic-data-quality.component';
import {AnalyticRecentActivityComponent} from './recent-activity/analytic-recent-activity.component';

@Component({
    selector: 'app-analytic',
    standalone: true,
    imports: [
        AnalyticCompanyFilterComponent,
        AnalyticSummaryCardsComponent,
        AnalyticStockHealthComponent,
        AnalyticCategoryInsightsComponent,
        AnalyticBrandInsightsComponent,
        AnalyticLocationInsightsComponent,
        AnalyticDataQualityComponent,
        AnalyticRecentActivityComponent,
    ],
    templateUrl: './analytic.component.html'
})
export class AnalyticComponent {

    private readonly analyticService = inject(AnalyticService);
    private readonly toast = inject(ToastService);

    protected readonly analytics = signal<AnalyticsDto | null>(null);
    protected readonly loading = signal(true);
    protected readonly selectedCompanyId = signal<string | null>(null);
    protected readonly isPlatformAdmin = AuthUtils.hasRole(UserRole.PLATFORM_ADMIN);
    protected readonly pageTitle = computed(() => {
        const selectedCompany = this.analytics()?.selectedCompany;
        if (selectedCompany) {
            return `${selectedCompany.name} Analytics`;
        }

        return this.isPlatformAdmin ? 'Platform Analytics' : 'Company Analytics';
    });

    constructor() {
        this.loadAnalytics();
    }

    protected selectCompany(companyId: string | null): void {
        this.selectedCompanyId.set(companyId);
        this.loadAnalytics();
    }

    private loadAnalytics(): void {
        this.loading.set(true);

        this.analyticService.getAnalytics(this.selectedCompanyId()).subscribe({
            next: (analytics) => {
                this.analytics.set(analytics);
                this.selectedCompanyId.set(analytics.selectedCompany?.id ?? null);
                this.loading.set(false);
            },
            error: () => {
                this.toast.error('Unable to load analytics.');
                this.loading.set(false);
            },
        });
    }

}
