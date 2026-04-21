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
public class DashboardLocationStatDto {

    private UUID locationId;
    private String locationName;
    private String companyName;
    private boolean enabled;
    private int inventoryCount;
    private int itemCount;
    private int totalQuantity;

}
