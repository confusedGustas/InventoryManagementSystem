export interface Identifiable {
    id: string;
}

export interface CompanyOptionDto {
    id: string;
    name: string;
}

export interface LocationOptionDto {
    id: string;
    name: string;
    companyId: string;
    companyName: string;
}

export interface PagedResponse<T> {
    content: T[];
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
}

export interface PagedResponseWithCompanies<T> extends PagedResponse<T> {
    companies: CompanyOptionDto[];
}

export interface PagedResponseWithCompaniesAndLocations<T> extends PagedResponse<T> {
    companies: CompanyOptionDto[];
    locations: LocationOptionDto[];
}
