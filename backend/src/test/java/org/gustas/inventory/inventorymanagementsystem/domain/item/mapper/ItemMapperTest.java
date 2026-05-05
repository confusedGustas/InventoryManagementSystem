package org.gustas.inventory.inventorymanagementsystem.domain.item.mapper;

import org.gustas.inventory.inventorymanagementsystem.domain.item.entity.Item;
import org.gustas.inventory.inventorymanagementsystem.support.TestDataFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {

    private final ItemMapper itemMapper = new ItemMapper();

    @Test
    void shouldNormalizeBlankOptionalFields() {
        var dto = TestDataFactory.saveItemDto(TestDataFactory.inventory(TestDataFactory.company(), null).getId());
        dto.setSku("   ");
        dto.setNotes("   ");

        Item result = itemMapper.toItem(dto, TestDataFactory.inventory(TestDataFactory.company(), null));

        assertThat(result.getName()).isEqualTo("Laptop");
        assertThat(result.getSku()).isNull();
        assertThat(result.getNotes()).isNull();
    }

    @Test
    void shouldMapItemToDto() {
        Item item = TestDataFactory.item(TestDataFactory.inventory(TestDataFactory.company(), TestDataFactory.location(TestDataFactory.company())));

        var result = itemMapper.toDto(item);

        assertThat(result.getId()).isEqualTo(item.getId());
        assertThat(result.getInventoryId()).isEqualTo(item.getInventory().getId());
        assertThat(result.getCompanyName()).isEqualTo("Acme");
        assertThat(result.getQuantity()).isEqualTo(5);
        assertThat(result.isEnabled()).isTrue();
    }

    @Test
    void shouldUpdateItemAndTrimValues() {
        var originalInventory = TestDataFactory.inventory(TestDataFactory.company(), null);
        Item item = TestDataFactory.item(originalInventory);
        var updatedInventory = TestDataFactory.inventory(TestDataFactory.company(), TestDataFactory.location(TestDataFactory.company()));
        updatedInventory.setId(java.util.UUID.randomUUID());
        var dto = TestDataFactory.saveItemDto(updatedInventory.getId());
        dto.setName("  Monitor  ");
        dto.setBrand("   ");

        itemMapper.updateItem(item, dto, updatedInventory);

        assertThat(item.getName()).isEqualTo("Monitor");
        assertThat(item.getBrand()).isNull();
        assertThat(item.getInventory()).isSameAs(updatedInventory);
        assertThat(item.getDescription()).isEqualTo("Work laptop");
    }
}
