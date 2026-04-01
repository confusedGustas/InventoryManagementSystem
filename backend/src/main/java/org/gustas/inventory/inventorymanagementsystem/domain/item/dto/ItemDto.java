package org.gustas.inventory.inventorymanagementsystem.domain.item.dto;

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
public class ItemDto {

    private UUID id;
    private UUID inventoryId;
    private String inventoryName;
    private UUID companyId;
    private String companyName;
    private String name;
    private String sku;
    private String category;
    private String brand;
    private String manufacturer;
    private String model;
    private String partNumber;
    private String serialNumber;
    private String color;
    private String dimensions;
    private String weight;
    private String unit;
    private Integer quantity;
    private String description;
    private String notes;
    private boolean enabled;
    private LocalDateTime createdOn;

}
