package org.gustas.inventory.inventorymanagementsystem.domain.dashboard.mapper;

import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.Company;
import org.gustas.inventory.inventorymanagementsystem.domain.dashboard.dto.DashboardCategoryStatDto;
import org.gustas.inventory.inventorymanagementsystem.domain.dashboard.dto.DashboardCompanyStatDto;
import org.gustas.inventory.inventorymanagementsystem.domain.dashboard.dto.DashboardInventoryStatDto;
import org.gustas.inventory.inventorymanagementsystem.domain.dashboard.dto.DashboardLocationStatDto;
import org.gustas.inventory.inventorymanagementsystem.domain.dashboard.dto.DashboardRecentItemDto;
import org.gustas.inventory.inventorymanagementsystem.domain.inventory.entity.Inventory;
import org.gustas.inventory.inventorymanagementsystem.domain.item.entity.Item;
import org.gustas.inventory.inventorymanagementsystem.domain.location.entity.Location;
import org.springframework.stereotype.Component;

@Component
public class DashboardMapper {

    public DashboardCompanyStatDto toCompanyStatDto(Company company, int locationCount, int inventoryCount, int itemCount, int totalQuantity) {
        return DashboardCompanyStatDto.builder()
                .companyId(company.getId())
                .companyName(company.getName())
                .status(company.getStatus().name())
                .locationCount(locationCount)
                .inventoryCount(inventoryCount)
                .itemCount(itemCount)
                .totalQuantity(totalQuantity)
                .build();
    }

    public DashboardLocationStatDto toLocationStatDto(Location location, int inventoryCount, int itemCount, int totalQuantity) {
        return DashboardLocationStatDto.builder()
                .locationId(location.getId())
                .locationName(location.getName())
                .companyName(location.getCompany().getName())
                .enabled(location.isEnabled())
                .inventoryCount(inventoryCount)
                .itemCount(itemCount)
                .totalQuantity(totalQuantity)
                .build();
    }

    public DashboardInventoryStatDto toInventoryStatDto(Inventory inventory, int itemCount, int totalQuantity) {
        return DashboardInventoryStatDto.builder()
                .inventoryId(inventory.getId())
                .inventoryName(inventory.getName())
                .companyName(inventory.getCompany().getName())
                .locationName(inventory.getLocation() != null ? inventory.getLocation().getName() : "Unassigned")
                .enabled(inventory.isEnabled())
                .itemCount(itemCount)
                .totalQuantity(totalQuantity)
                .build();
    }

    public DashboardCategoryStatDto toCategoryStatDto(String category, int itemCount, int totalQuantity) {
        return DashboardCategoryStatDto.builder()
                .category(category)
                .itemCount(itemCount)
                .totalQuantity(totalQuantity)
                .build();
    }

    public DashboardRecentItemDto toRecentItemDto(Item item) {
        return DashboardRecentItemDto.builder()
                .itemId(item.getId())
                .itemName(item.getName())
                .companyName(item.getInventory().getCompany().getName())
                .inventoryName(item.getInventory().getName())
                .category(item.getCategory() == null || item.getCategory().isBlank() ? "Uncategorized" : item.getCategory())
                .quantity(item.getQuantity() != null ? item.getQuantity() : 0)
                .createdOn(item.getCreatedOn())
                .build();
    }

}
