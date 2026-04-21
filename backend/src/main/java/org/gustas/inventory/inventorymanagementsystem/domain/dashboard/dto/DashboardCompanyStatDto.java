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
public class DashboardCompanyStatDto {

    private UUID companyId;
    private String companyName;
    private String status;
    private int locationCount;
    private int inventoryCount;
    private int itemCount;
    private int totalQuantity;

}
