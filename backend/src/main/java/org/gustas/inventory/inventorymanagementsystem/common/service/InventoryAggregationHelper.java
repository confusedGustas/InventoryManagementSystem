package org.gustas.inventory.inventorymanagementsystem.common.service;

import org.gustas.inventory.inventorymanagementsystem.domain.inventory.entity.Inventory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InventoryAggregationHelper {

    public Map<UUID, List<Inventory>> groupInventoriesByLocationId(List<Inventory> inventories) {
        return inventories.stream()
                .filter(inventory -> inventory.getLocation() != null)
                .collect(Collectors.groupingBy(inventory -> inventory.getLocation().getId()));
    }

}
