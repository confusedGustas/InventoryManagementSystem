package org.gustas.inventory.inventorymanagementsystem.domain.ai.dto;

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
public class AiAnalysisItemDto {

    private String itemName;
    private String companyName;
    private String inventoryName;
    private String locationName;
    private int quantity;
    private String reason;

}
