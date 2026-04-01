import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {tap} from 'rxjs';
import {environment} from '../../environments/environment';

export interface LoginDto {
    username: string;
    password: string;
}

export interface RegisterDto {
    firstName: string;
    lastName: string;
    username: string;
    email: string;
    password: string;
}

export interface TokenDto {
    token: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {

    private readonly apiUrl = environment.apiUrl;

    constructor(private http: HttpClient) {}

    login(credentials: LoginDto) {
        return this.http.post<TokenDto>(`${this.apiUrl}/auth/login`, credentials)
            .pipe(tap(res => localStorage.setItem('token', res.token)));
    }

    register(payload: RegisterDto) {
        return this.http.post<TokenDto>(`${this.apiUrl}/auth/register`, payload)
            .pipe(tap(res => localStorage.setItem('token', res.token)));
    }

    logout() {
        localStorage.removeItem('token');
    }

}
