package org.gustas.inventory.inventorymanagementsystem.domain.inventory.dto;

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
public class InventoryDto {

    private UUID id;
    private String name;
    private String code;
    private String description;
    private boolean enabled;
    private LocalDateTime createdOn;
    private UUID companyId;
    private String companyName;
    private UUID locationId;
    private String locationName;

}
