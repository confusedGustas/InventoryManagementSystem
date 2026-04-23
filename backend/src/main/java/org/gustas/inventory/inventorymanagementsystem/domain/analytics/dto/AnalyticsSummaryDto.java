package org.gustas.inventory.inventorymanagementsystem.domain.analytics.dto;

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
public class AnalyticsSummaryDto {

    private int totalItems;
    private int totalQuantity;
    private int lowStockItems;
    private int outOfStockItems;
    private int uncategorizedItems;
    private int inventoriesWithoutItems;
    private double averageQuantityPerItem;
    private double averageItemsPerInventory;

}
