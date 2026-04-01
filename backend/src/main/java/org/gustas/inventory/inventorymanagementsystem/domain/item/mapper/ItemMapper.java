package org.gustas.inventory.inventorymanagementsystem.domain.item.mapper;

import org.gustas.inventory.inventorymanagementsystem.domain.inventory.entity.Inventory;
import org.gustas.inventory.inventorymanagementsystem.domain.item.dto.ItemDto;
import org.gustas.inventory.inventorymanagementsystem.domain.item.dto.SaveItemDto;
import org.gustas.inventory.inventorymanagementsystem.domain.item.entity.Item;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper {

    public Item toItem(SaveItemDto saveItemDto, Inventory inventory) {
        return Item.builder()
                .name(saveItemDto.getName().trim())
                .sku(normalize(saveItemDto.getSku()))
                .category(normalize(saveItemDto.getCategory()))
                .brand(normalize(saveItemDto.getBrand()))
                .manufacturer(normalize(saveItemDto.getManufacturer()))
                .model(normalize(saveItemDto.getModel()))
                .partNumber(normalize(saveItemDto.getPartNumber()))
                .serialNumber(normalize(saveItemDto.getSerialNumber()))
                .color(normalize(saveItemDto.getColor()))
                .dimensions(normalize(saveItemDto.getDimensions()))
                .weight(normalize(saveItemDto.getWeight()))
                .unit(normalize(saveItemDto.getUnit()))
                .quantity(saveItemDto.getQuantity())
                .description(normalize(saveItemDto.getDescription()))
                .notes(normalize(saveItemDto.getNotes()))
                .enabled(true)
                .inventory(inventory)
                .build();
    }

    public ItemDto toDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .inventoryId(item.getInventory().getId())
                .inventoryName(item.getInventory().getName())
                .companyId(item.getInventory().getCompany().getId())
                .companyName(item.getInventory().getCompany().getName())
                .name(item.getName())
                .sku(item.getSku())
                .category(item.getCategory())
                .brand(item.getBrand())
                .manufacturer(item.getManufacturer())
                .model(item.getModel())
                .partNumber(item.getPartNumber())
                .serialNumber(item.getSerialNumber())
                .color(item.getColor())
                .dimensions(item.getDimensions())
                .weight(item.getWeight())
                .unit(item.getUnit())
                .quantity(item.getQuantity())
                .description(item.getDescription())
                .notes(item.getNotes())
                .enabled(item.isEnabled())
                .createdOn(item.getCreatedOn())
                .build();
    }

    public void updateItem(Item item, SaveItemDto saveItemDto, Inventory inventory) {
        item.setName(saveItemDto.getName().trim());
        item.setSku(normalize(saveItemDto.getSku()));
        item.setCategory(normalize(saveItemDto.getCategory()));
        item.setBrand(normalize(saveItemDto.getBrand()));
        item.setManufacturer(normalize(saveItemDto.getManufacturer()));
        item.setModel(normalize(saveItemDto.getModel()));
        item.setPartNumber(normalize(saveItemDto.getPartNumber()));
        item.setSerialNumber(normalize(saveItemDto.getSerialNumber()));
        item.setColor(normalize(saveItemDto.getColor()));
        item.setDimensions(normalize(saveItemDto.getDimensions()));
        item.setWeight(normalize(saveItemDto.getWeight()));
        item.setUnit(normalize(saveItemDto.getUnit()));
        item.setQuantity(saveItemDto.getQuantity());
        item.setDescription(normalize(saveItemDto.getDescription()));
        item.setNotes(normalize(saveItemDto.getNotes()));
        item.setInventory(inventory);
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

}
