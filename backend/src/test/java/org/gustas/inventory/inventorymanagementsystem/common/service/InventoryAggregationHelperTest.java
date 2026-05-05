package org.gustas.inventory.inventorymanagementsystem.common.service;

import org.gustas.inventory.inventorymanagementsystem.domain.inventory.entity.Inventory;
import org.gustas.inventory.inventorymanagementsystem.support.TestDataFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryAggregationHelperTest {

    private final InventoryAggregationHelper inventoryAggregationHelper = new InventoryAggregationHelper();

    @Test
    void shouldGroupOnlyInventoriesWithLocation() {
        Inventory assigned = TestDataFactory.inventory(TestDataFactory.company(), TestDataFactory.location(TestDataFactory.company()));
        Inventory unassigned = TestDataFactory.inventory(TestDataFactory.company(), null);
        unassigned.setId(java.util.UUID.randomUUID());

        var result = inventoryAggregationHelper.groupInventoriesByLocationId(List.of(assigned, unassigned));

        assertThat(result).hasSize(1);
        assertThat(result.values().iterator().next()).containsExactly(assigned);
    }
}
