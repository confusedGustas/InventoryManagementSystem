import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {CompanyOptionDto} from '../../shared/models/common.models';

export interface DashboardSummaryDto {
    totalCompanies: number;
    activeCompanies: number;
    totalLocations: number;
    enabledLocations: number;
    totalInventories: number;
    enabledInventories: number;
    totalItems: number;
    totalQuantity: number;
    lowStockItems: number;
}

export interface DashboardCompanyStatDto {
    companyId: string;
    companyName: string;
    status: string;
    locationCount: number;
    inventoryCount: number;
    itemCount: number;
    totalQuantity: number;
}

export interface DashboardLocationStatDto {
    locationId: string;
    locationName: string;
    companyName: string;
    enabled: boolean;
    inventoryCount: number;
    itemCount: number;
    totalQuantity: number;
}

export interface DashboardInventoryStatDto {
    inventoryId: string;
    inventoryName: string;
    companyName: string;
    locationName: string;
    enabled: boolean;
    itemCount: number;
    totalQuantity: number;
}

export interface DashboardCategoryStatDto {
    category: string;
    itemCount: number;
    totalQuantity: number;
}

export interface DashboardRecentItemDto {
    itemId: string;
    itemName: string;
    companyName: string;
    inventoryName: string;
    category: string;
    quantity: number;
    createdOn: string;
}

export interface DashboardDto {
    selectedCompany: CompanyOptionDto | null;
    companies: CompanyOptionDto[];
    summary: DashboardSummaryDto;
    companyBreakdown: DashboardCompanyStatDto[];
    locationBreakdown: DashboardLocationStatDto[];
    inventoryBreakdown: DashboardInventoryStatDto[];
    categoryBreakdown: DashboardCategoryStatDto[];
    recentItems: DashboardRecentItemDto[];
}

@Injectable({ providedIn: 'root' })
export class DashboardService {

    private readonly http = inject(HttpClient);
    private readonly apiUrl = environment.apiUrl;

    getDashboard(companyId: string | null) {
        let params = new HttpParams();
        if (companyId) {
            params = params.set('companyId', companyId);
        }

        return this.http.get<DashboardDto>(`${this.apiUrl}/dashboard`, { params });
    }

}
