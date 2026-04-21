package org.gustas.inventory.inventorymanagementsystem.domain.inventory.repository;

import org.gustas.inventory.inventorymanagementsystem.domain.inventory.entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    boolean existsByCompanyIdAndNameIgnoreCase(UUID companyId, String name);
    boolean existsByCompanyIdAndNameIgnoreCaseAndIdNot(UUID companyId, String name, UUID id);
    boolean existsByCompanyIdAndCodeIgnoreCase(UUID companyId, String code);
    boolean existsByCompanyIdAndCodeIgnoreCaseAndIdNot(UUID companyId, String code, UUID id);
    Inventory findInventoryById(UUID id);
    Inventory findInventoryByIdAndCompanyId(UUID id, UUID companyId);
    List<Inventory> findAllByCompanyId(UUID companyId);
    Page<Inventory> findAllByCompanyId(UUID companyId, Pageable pageable);

}
