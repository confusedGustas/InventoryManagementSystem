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
public class AnalyticsStockHealthDto {

    private String status;
    private int itemCount;
    private int totalQuantity;

}
