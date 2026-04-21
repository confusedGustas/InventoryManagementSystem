package org.gustas.inventory.inventorymanagementsystem.domain.dashboard.dto;

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
public class DashboardInventoryStatDto {

    private UUID inventoryId;
    private String inventoryName;
    private String companyName;
    private String locationName;
    private boolean enabled;
    private int itemCount;
    private int totalQuantity;

}
