package org.gustas.inventory.inventorymanagementsystem.domain.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaveInventoryDto {

    @NotBlank(message = "Inventory name is required")
    private String name;

    @NotBlank(message = "Inventory code is required")
    private String code;

    private String description;
    private UUID companyId;
    private UUID locationId;

}
