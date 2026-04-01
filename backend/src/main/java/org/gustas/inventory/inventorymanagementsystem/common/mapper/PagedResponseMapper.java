package org.gustas.inventory.inventorymanagementsystem.common.mapper;

import org.gustas.inventory.inventorymanagementsystem.common.dto.PagedResponseDto;
import org.gustas.inventory.inventorymanagementsystem.common.dto.PagedResponseWithCompaniesDto;
import org.gustas.inventory.inventorymanagementsystem.common.dto.PagedResponseWithCompaniesAndLocationsDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class PagedResponseMapper {

    public <T> PagedResponseDto<T> toDto(Page<T> page) {
        return PagedResponseDto.<T>builder()
                .content(page.getContent())
                .page(page.getNumber() + 1)
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    public <T, C> PagedResponseWithCompaniesDto<T, C> toPagedWithCompanies(PagedResponseDto<T> pagedResponse, List<C> companies) {
        return PagedResponseWithCompaniesDto.<T, C>builder()
                .content(pagedResponse.getContent())
                .page(pagedResponse.getPage())
                .size(pagedResponse.getSize())
                .totalElements(pagedResponse.getTotalElements())
                .totalPages(pagedResponse.getTotalPages())
                .companies(companies)
                .build();
    }

    public <T, C, L> PagedResponseWithCompaniesAndLocationsDto<T, C, L> toPagedWithCompaniesAndLocations(
            PagedResponseDto<T> pagedResponse,
            List<C> companies,
            List<L> locations
    ) {
        return PagedResponseWithCompaniesAndLocationsDto.<T, C, L>builder()
                .content(pagedResponse.getContent())
                .page(pagedResponse.getPage())
                .size(pagedResponse.getSize())
                .totalElements(pagedResponse.getTotalElements())
                .totalPages(pagedResponse.getTotalPages())
                .companies(companies)
                .locations(locations)
                .build();
    }

}
