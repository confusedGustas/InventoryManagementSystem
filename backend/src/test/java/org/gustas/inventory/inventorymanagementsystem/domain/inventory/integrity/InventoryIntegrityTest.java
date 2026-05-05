package org.gustas.inventory.inventorymanagementsystem.domain.inventory.integrity;

import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.Company;
import org.gustas.inventory.inventorymanagementsystem.domain.inventory.entity.Inventory;
import org.gustas.inventory.inventorymanagementsystem.domain.inventory.repository.InventoryRepository;
import org.gustas.inventory.inventorymanagementsystem.domain.location.entity.Location;
import org.gustas.inventory.inventorymanagementsystem.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class InventoryIntegrityTest {

    private final InventoryIntegrity inventoryIntegrity = new InventoryIntegrity();

    @Test
    void shouldRejectDuplicateInventoryCode() {
        InventoryRepository repository = Mockito.mock(InventoryRepository.class);
        UUID companyId = TestDataFactory.company().getId();
        when(repository.existsByCompanyIdAndCodeIgnoreCase(companyId, "INV-001")).thenReturn(true);

        assertThatThrownBy(() -> inventoryIntegrity.checkInventoryCodeAvailable(repository, companyId, " INV-001 ", null))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Inventory code is already taken");
    }

    @Test
    void shouldRejectLocationOutsideCompany() {
        Company company = TestDataFactory.company();
        Company otherCompany = TestDataFactory.company();
        otherCompany.setId(UUID.randomUUID());
        Location location = TestDataFactory.location(otherCompany);

        assertThatThrownBy(() -> inventoryIntegrity.checkLocationBelongsToCompany(location, company))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Location must belong");
    }

    @Test
    void shouldRejectManagingItemsOnDisabledInventory() {
        Inventory inventory = TestDataFactory.inventory(TestDataFactory.company(), null);
        inventory.setEnabled(false);

        assertThatThrownBy(() -> inventoryIntegrity.checkInventoryCanManageItems(inventory))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Items cannot be managed");
    }
}
