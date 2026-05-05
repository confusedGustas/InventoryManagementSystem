import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {PagedResponseWithCompanies} from '../../shared/models/common.models';
import {environment} from '../../../environments/environment';

export interface UserDto {
    id: string;
    userRole: string;
    firstName: string;
    lastName: string;
    username: string;
    email: string;
    locale: string;
    enabled: boolean;
    companyId: string | null;
    companyName: string | null;
    createdOn: string;
}

export interface ChangeEmailDto {
    email: string;
}

export interface ChangePasswordDto {
    currentPassword: string;
    newPassword: string;
}

export interface ApiKeyDto {
    id: string | null;
    companyId: string;
    companyName: string;
    apiKey: string;
    updatedOn: string | null;
}

export interface SaveApiKeyDto {
    apiKey: string;
}

export interface CreateUserDto {
    firstName: string;
    lastName: string;
    username: string;
    email: string;
    password: string;
    userRole: string;
    companyId: string | null;
}

export interface UpdateUserDto {
    firstName: string;
    lastName: string;
    username: string;
    email: string;
    userRole: string;
    companyId: string | null;
}

@Injectable({ providedIn: 'root' })
export class SettingsService {

    private readonly http = inject(HttpClient);
    private readonly apiUrl = environment.apiUrl;

    getProfile() {
        return this.http.get<UserDto>(`${this.apiUrl}/users/profile`);
    }

    getCurrentApiKey() {
        return this.http.get<ApiKeyDto>(`${this.apiUrl}/api-keys/current`);
    }

    getUsers(page = 1) {
        const params = new HttpParams().set('page', page);
        return this.http.get<PagedResponseWithCompanies<UserDto>>(`${this.apiUrl}/users`, { params });
    }

    changeEmail(payload: ChangeEmailDto) {
        return this.http.patch<UserDto>(`${this.apiUrl}/users/email`, payload);
    }

    changePassword(payload: ChangePasswordDto) {
        return this.http.patch<UserDto>(`${this.apiUrl}/users/change-password`, payload);
    }

    saveCurrentApiKey(payload: SaveApiKeyDto) {
        return this.http.put<ApiKeyDto>(`${this.apiUrl}/api-keys/current`, payload);
    }

    createUser(payload: CreateUserDto) {
        return this.http.post<UserDto>(`${this.apiUrl}/users`, payload);
    }

    updateUser(userId: string, payload: UpdateUserDto) {
        return this.http.put<UserDto>(`${this.apiUrl}/users/${userId}`, payload);
    }

    disableUser(userId: string) {
        return this.http.patch<UserDto>(`${this.apiUrl}/users/${userId}/disable`, {});
    }

    enableUser(userId: string) {
        return this.http.patch<UserDto>(`${this.apiUrl}/users/${userId}/enable`, {});
    }

}
