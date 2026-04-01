export class AuthUtils {

    private static readonly TOKEN_CLAIM = 'token';
    private static readonly ROLE_CLAIM = 'role';
    private static readonly FIRST_NAME_CLAIM = 'firstName';
    private static readonly LAST_NAME_CLAIM = 'lastName';


    private static decodeTokenClaims(): Record<string, string> {
        const token = AuthUtils.getToken();

        if (!token) {
            return {};
        }

        try {
            return JSON.parse(atob(token.split('.')[1]));
        } catch {
            return {};
        }
    }

    static getToken(): string {
        return localStorage.getItem(this.TOKEN_CLAIM) ?? '';
    }

    static isLoggedIn(): boolean {
        return !!AuthUtils.getToken();
    }

    static getRole(): string {
        return AuthUtils.decodeTokenClaims()[this.ROLE_CLAIM] ?? '';
    }

    static hasRole(role: string): boolean {
        return AuthUtils.getRole() === role;
    }

    static hasAnyRole(roles: string[]): boolean {
        return roles.length === 0 || roles.includes(AuthUtils.getRole());
    }

    static firstName(): string {
        return AuthUtils.decodeTokenClaims()[this.FIRST_NAME_CLAIM] ?? '';
    }

    static lastName(): string {
        return AuthUtils.decodeTokenClaims()[this.LAST_NAME_CLAIM] ?? '';
    }

}
