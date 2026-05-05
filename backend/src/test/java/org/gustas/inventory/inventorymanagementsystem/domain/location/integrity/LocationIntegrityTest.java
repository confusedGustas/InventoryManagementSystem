package org.gustas.inventory.inventorymanagementsystem.domain.location.integrity;

import org.gustas.inventory.inventorymanagementsystem.domain.location.entity.Location;
import org.gustas.inventory.inventorymanagementsystem.domain.location.repository.LocationRepository;
import org.gustas.inventory.inventorymanagementsystem.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class LocationIntegrityTest {

    private final LocationIntegrity locationIntegrity = new LocationIntegrity();

    @Test
    void shouldRejectDuplicateLocationName() {
        LocationRepository repository = Mockito.mock(LocationRepository.class);
        when(repository.existsByCompanyIdAndNameIgnoreCase(TestDataFactory.company().getId(), "Warehouse")).thenReturn(true);

        assertThatThrownBy(() -> locationIntegrity.checkLocationNameAvailable(repository, TestDataFactory.company().getId(), " Warehouse ", null))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Location name is already taken");
    }

    @Test
    void shouldRejectDisablingAlreadyDisabledLocation() {
        Location location = TestDataFactory.location(TestDataFactory.company());
        location.setEnabled(false);

        assertThatThrownBy(() -> locationIntegrity.checkLocationCanDisable(location))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Location is already disabled");
    }
}
