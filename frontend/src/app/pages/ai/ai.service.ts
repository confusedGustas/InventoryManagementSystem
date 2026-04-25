import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {CompanyOptionDto} from '../../shared/models/common.models';

export interface AiScopeDto {
    selectedCompany: CompanyOptionDto | null;
    companies: CompanyOptionDto[];
}

export interface AiAnalysisItemDto {
    itemName: string;
    companyName: string;
    inventoryName: string;
    locationName: string;
    quantity: number;
    reason: string;
}

export interface AiAnalysisDto {
    reportId: string | null;
    selectedCompany: CompanyOptionDto | null;
    companies: CompanyOptionDto[];
    scopeLabel: string;
    overview: string;
    depletionRisks: AiAnalysisItemDto[];
    oversupplyRisks: AiAnalysisItemDto[];
    suggestedPurchases: AiAnalysisItemDto[];
    actions: string[];
    generatedOn: string;
}

export interface AiReportDto {
    id: string;
    companyId: string | null;
    companyName: string | null;
    scopeLabel: string;
    overview: string;
    generatedOn: string;
}

export interface AiInventoryOptionDto {
    id: string;
    name: string;
    companyId: string;
    companyName: string;
    locationId: string | null;
    locationName: string | null;
}

export interface AiSuggestedItemDto {
    name: string;
    sku: string;
    category: string;
    brand: string;
    manufacturer: string;
    model: string;
    partNumber: string;
    serialNumber: string;
    color: string;
    dimensions: string;
    weight: string;
    unit: string;
    quantity: number;
    description: string;
    notes: string;
}

export interface AiItemSuggestionDto {
    scopeLabel: string;
    imageSummary: string;
    inventories: AiInventoryOptionDto[];
    item: AiSuggestedItemDto;
}

@Injectable({ providedIn: 'root' })
export class AiService {

    private readonly http = inject(HttpClient);
    private readonly apiUrl = environment.apiUrl;

    getScope(companyId: string | null) {
        let params = new HttpParams();
        if (companyId) {
            params = params.set('companyId', companyId);
        }

        return this.http.get<AiScopeDto>(`${this.apiUrl}/ai/scope`, { params });
    }

    getAnalysis(companyId: string | null) {
        let params = new HttpParams();
        if (companyId) {
            params = params.set('companyId', companyId);
        }

        return this.http.get<AiAnalysisDto>(`${this.apiUrl}/ai/analysis`, { params });
    }

    getReports(companyId: string | null) {
        let params = new HttpParams();
        if (companyId) {
            params = params.set('companyId', companyId);
        }

        return this.http.get<AiReportDto[]>(`${this.apiUrl}/ai/reports`, { params });
    }

    downloadReportPdf(reportId: string) {
        return this.http.get(`${this.apiUrl}/ai/reports/${reportId}/pdf`, {
            responseType: 'blob'
        });
    }

    suggestItemFromImage(file: File, companyId: string | null) {
        const formData = new FormData();
        formData.append('image', file);

        let params = new HttpParams();
        if (companyId) {
            params = params.set('companyId', companyId);
        }

        return this.http.post<AiItemSuggestionDto>(`${this.apiUrl}/ai/item-suggestion`, formData, { params });
    }

}
