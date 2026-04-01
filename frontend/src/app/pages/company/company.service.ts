import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {PagedResponse} from '../../shared/models/common.models';
import {environment} from '../../../environments/environment';

export interface CompanyDto {
    id: string;
    name: string;
    contactEmail: string;
    contactPhone: string;
    city: string;
    postalCode: string;
    country: string;
    status: string;
    createdOn: string;
}

export interface SaveCompanyDto {
    name: string;
    contactEmail: string;
    contactPhone: string;
    city: string;
    postalCode: string;
    country: string;
    status: string;
}

@Injectable({ providedIn: 'root' })
export class CompanyService {

    private readonly http = inject(HttpClient);
    private readonly apiUrl = environment.apiUrl;

    getCompanies(page = 1) {
        const params = new HttpParams().set('page', page);

        return this.http.get<PagedResponse<CompanyDto>>(`${this.apiUrl}/companies`, { params });
    }

    saveCompany(payload: SaveCompanyDto) {
        return this.http.post<CompanyDto>(`${this.apiUrl}/companies`, payload);
    }

    updateCompany(companyId: string, payload: SaveCompanyDto) {
        return this.http.put<CompanyDto>(`${this.apiUrl}/companies/${companyId}`, payload);
    }

    disableCompany(companyId: string) {
        return this.http.patch<CompanyDto>(`${this.apiUrl}/companies/${companyId}/disable`, {});
    }

    enableCompany(companyId: string) {
        return this.http.patch<CompanyDto>(`${this.apiUrl}/companies/${companyId}/enable`, {});
    }

}
