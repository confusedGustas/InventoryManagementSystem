import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {
    PagedResponse,
    PagedResponseWithCompaniesAndLocations
} from '../../shared/models/common.models';
import {environment} from '../../../environments/environment';

export interface InventoryDto {
    id: string;
    name: string;
    code: string;
    description: string | null;
    enabled: boolean;
    createdOn: string;
    companyId: string;
    companyName: string;
    locationId: string | null;
    locationName: string | null;
}

export interface SaveInventoryDto {
    name: string;
    code: string;
    description: string | null;
    companyId: string | null;
    locationId: string | null;
}

export interface ItemDto {
    id: string;
    inventoryId: string;
    inventoryName: string;
    companyId: string;
    companyName: string;
    name: string;
    sku: string | null;
    category: string | null;
    brand: string | null;
    manufacturer: string | null;
    model: string | null;
    partNumber: string | null;
    serialNumber: string | null;
    color: string | null;
    dimensions: string | null;
    weight: string | null;
    unit: string | null;
    quantity: number;
    description: string | null;
    notes: string | null;
    createdOn: string;
}

export interface SaveItemDto {
    inventoryId: string;
    name: string;
    sku: string | null;
    category: string | null;
    brand: string | null;
    manufacturer: string | null;
    model: string | null;
    partNumber: string | null;
    serialNumber: string | null;
    color: string | null;
    dimensions: string | null;
    weight: string | null;
    unit: string | null;
    quantity: number;
    description: string | null;
    notes: string | null;
}

@Injectable({ providedIn: 'root' })
export class InventoryService {

    private readonly http = inject(HttpClient);
    private readonly apiUrl = environment.apiUrl;

    getInventories(page = 1) {
        const params = new HttpParams().set('page', page);
        return this.http.get<PagedResponseWithCompaniesAndLocations<InventoryDto>>(`${this.apiUrl}/inventories`, { params });
    }

    saveInventory(payload: SaveInventoryDto) {
        return this.http.post<InventoryDto>(`${this.apiUrl}/inventories`, payload);
    }

    updateInventory(inventoryId: string, payload: SaveInventoryDto) {
        return this.http.put<InventoryDto>(`${this.apiUrl}/inventories/${inventoryId}`, payload);
    }

    disableInventory(inventoryId: string) {
        return this.http.patch<InventoryDto>(`${this.apiUrl}/inventories/${inventoryId}/disable`, {});
    }

    enableInventory(inventoryId: string) {
        return this.http.patch<InventoryDto>(`${this.apiUrl}/inventories/${inventoryId}/enable`, {});
    }

    getItems(inventoryId: string, page = 1) {
        const params = new HttpParams()
            .set('inventoryId', inventoryId)
            .set('page', page);

        return this.http.get<PagedResponse<ItemDto>>(`${this.apiUrl}/items`, { params });
    }

    saveItem(payload: SaveItemDto) {
        return this.http.post<ItemDto>(`${this.apiUrl}/items`, payload);
    }

    updateItem(itemId: string, payload: SaveItemDto) {
        return this.http.put<ItemDto>(`${this.apiUrl}/items/${itemId}`, payload);
    }

    deleteItems(itemIds: string[]) {
        let params = new HttpParams();
        itemIds.forEach(itemId => {
            params = params.append('itemIds', itemId);
        });

        return this.http.delete<void>(`${this.apiUrl}/items`, { params });
    }

}
