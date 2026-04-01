import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {PagedResponseWithCompanies} from '../../shared/models/common.models';
import {environment} from '../../../environments/environment';

export interface LocationDto {
    id: string;
    name: string;
    address: string;
    phone: string;
    domain: string;
    enabled: boolean;
    createdOn: string;
    companyId: string;
    companyName: string;
    companyCity: string;
    companyCountry: string;
    companyStatus: string;
}

export interface SaveLocationDto {
    name: string;
    address: string;
    phone: string;
    domain: string;
    companyId: string;
}

@Injectable({ providedIn: 'root' })
export class LocationService {

    private readonly http = inject(HttpClient);
    private readonly apiUrl = environment.apiUrl;

    getLocations(page = 1) {
        const params = new HttpParams().set('page', page);

        return this.http.get<PagedResponseWithCompanies<LocationDto>>(`${this.apiUrl}/locations`, { params });
    }

    saveLocation(payload: SaveLocationDto) {
        return this.http.post<LocationDto>(`${this.apiUrl}/locations`, payload);
    }

    updateLocation(locationId: string, payload: SaveLocationDto) {
        return this.http.put<LocationDto>(`${this.apiUrl}/locations/${locationId}`, payload);
    }

    disableLocation(locationId: string) {
        return this.http.patch<LocationDto>(`${this.apiUrl}/locations/${locationId}/disable`, {});
    }

    enableLocation(locationId: string) {
        return this.http.patch<LocationDto>(`${this.apiUrl}/locations/${locationId}/enable`, {});
    }

}
