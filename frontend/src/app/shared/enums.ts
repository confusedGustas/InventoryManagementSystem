export enum CompanyStatus {
    ONBOARDING = 'ONBOARDING',
    ACTIVE = 'ACTIVE',
    SUSPENDED = 'SUSPENDED',
    CHURNED = 'CHURNED',
}

export enum UserRole {
    PLATFORM_ADMIN = 'PLATFORM_ADMIN',
    COMPANY_ADMIN = 'COMPANY_ADMIN',
    COMPANY_USER = 'COMPANY_USER',
    COMPANY_FINANCE = 'COMPANY_FINANCE',
}

export function formatEnumLabel(value: string): string {
    return value
        .toLowerCase()
        .split('_')
        .map(part => part.charAt(0).toUpperCase() + part.slice(1))
        .join(' ');
}
