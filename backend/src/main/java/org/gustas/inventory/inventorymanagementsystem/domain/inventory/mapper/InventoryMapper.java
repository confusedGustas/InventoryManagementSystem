package org.gustas.inventory.inventorymanagementsystem.domain.inventory.mapper;

import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.Company;
import org.gustas.inventory.inventorymanagementsystem.domain.inventory.dto.InventoryDto;
import org.gustas.inventory.inventorymanagementsystem.domain.inventory.dto.SaveInventoryDto;
import org.gustas.inventory.inventorymanagementsystem.domain.inventory.entity.Inventory;
import org.gustas.inventory.inventorymanagementsystem.domain.location.entity.Location;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public Inventory toInventory(SaveInventoryDto saveInventoryDto, Company company, Location location) {
        return Inventory.builder()
                .name(saveInventoryDto.getName().trim())
                .code(saveInventoryDto.getCode().trim())
                .description(normalize(saveInventoryDto.getDescription()))
                .enabled(true)
                .company(company)
                .location(location)
                .build();
    }

    public InventoryDto toDto(Inventory inventory) {
        return InventoryDto.builder()
                .id(inventory.getId())
                .name(inventory.getName())
                .code(inventory.getCode())
                .description(inventory.getDescription())
                .enabled(inventory.isEnabled())
                .createdOn(inventory.getCreatedOn())
                .companyId(inventory.getCompany().getId())
                .companyName(inventory.getCompany().getName())
                .locationId(inventory.getLocation() != null ? inventory.getLocation().getId() : null)
                .locationName(inventory.getLocation() != null ? inventory.getLocation().getName() : null)
                .build();
    }

    public void updateInventory(Inventory inventory, SaveInventoryDto saveInventoryDto, Company company, Location location) {
        inventory.setName(saveInventoryDto.getName().trim());
        inventory.setCode(saveInventoryDto.getCode().trim());
        inventory.setDescription(normalize(saveInventoryDto.getDescription()));
        inventory.setCompany(company);
        inventory.setLocation(location);
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

}
