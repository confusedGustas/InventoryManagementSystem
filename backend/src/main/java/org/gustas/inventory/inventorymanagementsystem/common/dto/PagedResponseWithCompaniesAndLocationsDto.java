package org.gustas.inventory.inventorymanagementsystem.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponseWithCompaniesAndLocationsDto<T, C, L> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private List<C> companies;
    private List<L> locations;

}
