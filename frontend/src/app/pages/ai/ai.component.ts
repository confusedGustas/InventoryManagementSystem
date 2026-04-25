import {Component, computed, inject, signal} from '@angular/core';
import {AuthUtils} from '../../auth/auth.utils';
import {UserRole} from '../../shared/enums';
import {ToastService} from '../../shared/toast/toast.service';
import {AiService, AiAnalysisDto, AiItemSuggestionDto, AiReportDto, AiScopeDto} from './ai.service';
import {AiCompanyFilterComponent} from './company-filter/ai-company-filter.component';
import {AiAnalysisRequestComponent} from './analysis-request/ai-analysis-request.component';
import {AiAnalysisResultComponent} from './analysis-result/ai-analysis-result.component';
import {AiReportsComponent} from './reports/ai-reports.component';
import {AiImageSuggestionComponent} from './image-suggestion/ai-image-suggestion.component';
import {AiItemCreationComponent} from './item-creation/ai-item-creation.component';

@Component({
    selector: 'app-ai',
    standalone: true,
    imports: [
        AiCompanyFilterComponent,
        AiAnalysisRequestComponent,
        AiAnalysisResultComponent,
        AiReportsComponent,
        AiImageSuggestionComponent,
        AiItemCreationComponent,
    ],
    templateUrl: './ai.component.html'
})
export class AiComponent {

    private readonly aiService = inject(AiService);
    private readonly toast = inject(ToastService);

    protected readonly scope = signal<AiScopeDto | null>(null);
    protected readonly analysis = signal<AiAnalysisDto | null>(null);
    protected readonly itemSuggestion = signal<AiItemSuggestionDto | null>(null);
    protected readonly reports = signal<AiReportDto[]>([]);
    protected readonly loading = signal(false);
    protected readonly itemSuggestionLoading = signal(false);
    protected readonly selectedCompanyId = signal<string | null>(null);
    protected readonly isPlatformAdmin = AuthUtils.hasRole(UserRole.PLATFORM_ADMIN);
    protected readonly scopeLabel = computed(() => {
        const selectedCompany = this.scope()?.selectedCompany ?? this.analysis()?.selectedCompany;
        if (selectedCompany) {
            return selectedCompany.name;
        }

        return this.isPlatformAdmin ? 'all companies' : 'your company';
    });

    constructor() {
        this.loadScope();
        this.loadReports();
    }

    protected selectCompany(companyId: string | null): void {
        this.selectedCompanyId.set(companyId);
        this.analysis.set(null);
        this.itemSuggestion.set(null);
        this.loadScope();
        this.loadReports();
    }

    protected runAnalysis(): void {
        this.loading.set(true);

        this.aiService.getAnalysis(this.selectedCompanyId()).subscribe({
            next: (analysis) => {
                this.scope.set({
                    selectedCompany: analysis.selectedCompany,
                    companies: analysis.companies,
                });
                this.analysis.set(analysis);
                this.selectedCompanyId.set(analysis.selectedCompany?.id ?? null);
                this.loadReports();
                this.loading.set(false);
            },
            error: () => {
                this.toast.error('Unable to generate AI analysis. Make sure the company API key is configured.');
                this.loading.set(false);
            },
        });
    }

    protected suggestItemFromImage(file: File): void {
        this.itemSuggestionLoading.set(true);

        this.aiService.suggestItemFromImage(file, this.selectedCompanyId()).subscribe({
            next: (suggestion) => {
                this.itemSuggestion.set(suggestion);
                this.itemSuggestionLoading.set(false);
            },
            error: () => {
                this.toast.error('Unable to analyze the image and suggest an item.');
                this.itemSuggestionLoading.set(false);
            }
        });
    }

    protected clearItemSuggestion(): void {
        this.itemSuggestion.set(null);
    }

    private loadScope(): void {
        this.aiService.getScope(this.selectedCompanyId()).subscribe({
            next: (scope) => {
                this.scope.set(scope);
                this.selectedCompanyId.set(scope.selectedCompany?.id ?? null);
            },
            error: () => {
                this.toast.error('Unable to load AI scope.');
            },
        });
    }

    private loadReports(): void {
        this.aiService.getReports(this.selectedCompanyId()).subscribe({
            next: (reports) => {
                this.reports.set(reports);
            },
            error: () => {
                this.toast.error('Unable to load AI reports.');
            },
        });
    }

    protected downloadReportPdf(reportId: string): void {
        this.aiService.downloadReportPdf(reportId).subscribe({
            next: (blob) => {
                const url = URL.createObjectURL(blob);
                const anchor = document.createElement('a');
                anchor.href = url;
                anchor.download = `ai-report-${reportId}.pdf`;
                anchor.click();
                URL.revokeObjectURL(url);
            },
            error: () => {
                this.toast.error('Unable to download AI report PDF.');
            }
        });
    }

}
