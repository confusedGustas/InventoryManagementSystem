package org.gustas.inventory.inventorymanagementsystem.domain.inventory.integrity;

import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.Company;
import org.gustas.inventory.inventorymanagementsystem.domain.inventory.entity.Inventory;
import org.gustas.inventory.inventorymanagementsystem.domain.inventory.repository.InventoryRepository;
import org.gustas.inventory.inventorymanagementsystem.domain.location.entity.Location;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class InventoryIntegrity {

    public void checkInventoryNotNull(Inventory inventory) {
        if (inventory == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventory not found");
        }
    }

    public void checkInventoryNameAvailable(InventoryRepository inventoryRepository, UUID companyId, String name, UUID excludedInventoryId) {
        boolean inventoryNameTaken = excludedInventoryId == null
                ? inventoryRepository.existsByCompanyIdAndNameIgnoreCase(companyId, name.trim())
                : inventoryRepository.existsByCompanyIdAndNameIgnoreCaseAndIdNot(companyId, name.trim(), excludedInventoryId);

        if (inventoryNameTaken) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Inventory name is already taken for this company");
        }
    }

    public void checkInventoryCodeAvailable(InventoryRepository inventoryRepository, UUID companyId, String code, UUID excludedInventoryId) {
        boolean inventoryCodeTaken = excludedInventoryId == null
                ? inventoryRepository.existsByCompanyIdAndCodeIgnoreCase(companyId, code.trim())
                : inventoryRepository.existsByCompanyIdAndCodeIgnoreCaseAndIdNot(companyId, code.trim(), excludedInventoryId);

        if (inventoryCodeTaken) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Inventory code is already taken for this company");
        }
    }

    public void checkLocationBelongsToCompany(Location location, Company company) {
        if (location != null && !location.getCompany().getId().equals(company.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Location must belong to the selected company");
        }
    }

    public void checkInventoryCanDisable(Inventory inventory) {
        if (!inventory.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Inventory is already disabled");
        }
    }

    public void checkInventoryCanEnable(Inventory inventory) {
        if (inventory.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Inventory is already enabled");
        }
    }

    public void checkInventoryCanManageItems(Inventory inventory) {
        if (!inventory.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Items cannot be managed for a disabled inventory");
        }
    }

}
