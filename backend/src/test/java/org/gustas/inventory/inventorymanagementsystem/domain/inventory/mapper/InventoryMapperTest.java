package org.gustas.inventory.inventorymanagementsystem.domain.inventory.mapper;

import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.Company;
import org.gustas.inventory.inventorymanagementsystem.domain.inventory.entity.Inventory;
import org.gustas.inventory.inventorymanagementsystem.domain.location.entity.Location;
import org.gustas.inventory.inventorymanagementsystem.support.TestDataFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryMapperTest {

    private final InventoryMapper inventoryMapper = new InventoryMapper();

    @Test
    void shouldNormalizeBlankDescriptionToNull() {
        Company company = TestDataFactory.company();
        Location location = TestDataFactory.location(company);
        var dto = TestDataFactory.saveInventoryDto(company.getId(), location.getId());
        dto.setDescription("   ");

        Inventory result = inventoryMapper.toInventory(dto, company, location);

        assertThat(result.getName()).isEqualTo("Main Inventory");
        assertThat(result.getCode()).isEqualTo("INV-001");
        assertThat(result.getDescription()).isNull();
    }
}
