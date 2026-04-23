import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {CompanyOptionDto} from '../../shared/models/common.models';

export interface AnalyticsSummaryDto {
    totalItems: number;
    totalQuantity: number;
    lowStockItems: number;
    outOfStockItems: number;
    uncategorizedItems: number;
    inventoriesWithoutItems: number;
    averageQuantityPerItem: number;
    averageItemsPerInventory: number;
}

export interface AnalyticsStockHealthDto {
    status: string;
    itemCount: number;
    totalQuantity: number;
}

export interface AnalyticsCategoryInsightDto {
    category: string;
    itemCount: number;
    totalQuantity: number;
    averageQuantity: number;
}

export interface AnalyticsBrandInsightDto {
    brand: string;
    itemCount: number;
    totalQuantity: number;
    averageQuantity: number;
}

export interface AnalyticsLocationInsightDto {
    locationId: string;
    locationName: string;
    companyName: string;
    enabled: boolean;
    inventoryCount: number;
    itemCount: number;
    totalQuantity: number;
    averageQuantityPerItem: number;
}

export interface AnalyticsDataQualityDto {
    label: string;
    filledCount: number;
    totalCount: number;
    completionRate: number;
}

export interface AnalyticsRecentActivityDto {
    period: string;
    itemsAdded: number;
    quantityAdded: number;
}

export interface AnalyticsDto {
    selectedCompany: CompanyOptionDto | null;
    companies: CompanyOptionDto[];
    summary: AnalyticsSummaryDto;
    stockHealth: AnalyticsStockHealthDto[];
    categoryInsights: AnalyticsCategoryInsightDto[];
    brandInsights: AnalyticsBrandInsightDto[];
    locationInsights: AnalyticsLocationInsightDto[];
    dataQuality: AnalyticsDataQualityDto[];
    recentActivity: AnalyticsRecentActivityDto[];
}

@Injectable({ providedIn: 'root' })
export class AnalyticService {

    private readonly http = inject(HttpClient);
    private readonly apiUrl = environment.apiUrl;

    getAnalytics(companyId: string | null) {
        let params = new HttpParams();
        if (companyId) {
            params = params.set('companyId', companyId);
        }

        return this.http.get<AnalyticsDto>(`${this.apiUrl}/analytics`, { params });
    }

}
