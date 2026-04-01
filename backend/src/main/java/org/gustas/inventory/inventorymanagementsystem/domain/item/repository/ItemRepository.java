package org.gustas.inventory.inventorymanagementsystem.domain.item.repository;

import org.gustas.inventory.inventorymanagementsystem.domain.item.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {

    Item findItemById(UUID id);
    Item findItemByIdAndInventoryCompanyId(UUID id, UUID companyId);
    Page<Item> findAllByInventoryId(UUID inventoryId, Pageable pageable);

}
