package org.gustas.inventory.inventorymanagementsystem.domain.item.integrity;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemIntegrityTest {

    private final ItemIntegrity itemIntegrity = new ItemIntegrity();

    @Test
    void shouldRejectMissingItem() {
        assertThatThrownBy(() -> itemIntegrity.checkItemNotNull(null))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Item not found");
    }
}
