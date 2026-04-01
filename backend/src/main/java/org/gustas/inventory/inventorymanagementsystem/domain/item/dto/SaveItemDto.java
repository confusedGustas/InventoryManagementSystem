package org.gustas.inventory.inventorymanagementsystem.domain.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaveItemDto {

    @NotBlank(message = "Item name is required")
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

    @NotNull(message = "Quantity is required")
    @PositiveOrZero(message = "Quantity must be zero or greater")
    private Integer quantity;

    private String description;
    private String notes;

    @NotNull(message = "Inventory is required")
    private UUID inventoryId;

}
