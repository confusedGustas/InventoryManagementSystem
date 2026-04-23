package org.gustas.inventory.inventorymanagementsystem.domain.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsLocationInsightDto {

    private UUID locationId;
    private String locationName;
    private String companyName;
    private boolean enabled;
    private int inventoryCount;
    private int itemCount;
    private int totalQuantity;
    private double averageQuantityPerItem;

}
