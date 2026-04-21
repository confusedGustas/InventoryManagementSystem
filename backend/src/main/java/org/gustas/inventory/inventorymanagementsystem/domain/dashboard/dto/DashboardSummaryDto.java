package org.gustas.inventory.inventorymanagementsystem.domain.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDto {

    private int totalCompanies;
    private int activeCompanies;
    private int totalLocations;
    private int enabledLocations;
    private int totalInventories;
    private int enabledInventories;
    private int totalItems;
    private int totalQuantity;
    private int lowStockItems;

}
