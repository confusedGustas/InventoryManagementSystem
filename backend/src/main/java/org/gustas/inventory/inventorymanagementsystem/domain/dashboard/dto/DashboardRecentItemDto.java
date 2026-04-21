package org.gustas.inventory.inventorymanagementsystem.domain.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardRecentItemDto {

    private UUID itemId;
    private String itemName;
    private String companyName;
    private String inventoryName;
    private String category;
    private int quantity;
    private LocalDateTime createdOn;

}
