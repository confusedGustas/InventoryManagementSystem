package org.gustas.inventory.inventorymanagementsystem.domain.item.service;

import lombok.RequiredArgsConstructor;
import org.gustas.inventory.inventorymanagementsystem.common.Constants;
import org.gustas.inventory.inventorymanagementsystem.common.dto.PagedResponseDto;
import org.gustas.inventory.inventorymanagementsystem.common.mapper.PagedResponseMapper;
import org.gustas.inventory.inventorymanagementsystem.common.service.CurrentUserContext;
import org.gustas.inventory.inventorymanagementsystem.domain.inventory.entity.Inventory;
import org.gustas.inventory.inventorymanagementsystem.domain.inventory.integrity.InventoryIntegrity;
import org.gustas.inventory.inventorymanagementsystem.domain.inventory.repository.InventoryRepository;
import org.gustas.inventory.inventorymanagementsystem.domain.item.dto.ItemDto;
import org.gustas.inventory.inventorymanagementsystem.domain.item.dto.SaveItemDto;
import org.gustas.inventory.inventorymanagementsystem.domain.item.entity.Item;
import org.gustas.inventory.inventorymanagementsystem.domain.item.integrity.ItemIntegrity;
import org.gustas.inventory.inventorymanagementsystem.domain.item.mapper.ItemMapper;
import org.gustas.inventory.inventorymanagementsystem.domain.item.repository.ItemRepository;
import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;
    private final ItemIntegrity itemIntegrity;
    private final InventoryIntegrity inventoryIntegrity;
    private final ItemMapper itemMapper;
    private final CurrentUserContext currentUserContext;
    private final PagedResponseMapper pagedResponseMapper;

    @Transactional(readOnly = true)
    public PagedResponseDto<ItemDto> getItems(UUID inventoryId, Integer page, String username) {
        User currentUser = currentUserContext.getCurrentUser(username);
        Inventory inventory = currentUserContext.findManagedEntity(
                currentUser,
                inventoryId,
                inventoryRepository::findInventoryById,
                inventoryRepository::findInventoryByIdAndCompanyId
        );
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), Constants.DEFAULT_PAGE_SIZE);

        inventoryIntegrity.checkInventoryNotNull(inventory);

        Page<ItemDto> itemPage = itemRepository.findAllByInventoryId(inventory.getId(), pageable)
                .map(itemMapper::toDto);

        return pagedResponseMapper.toDto(itemPage);
    }

    @Transactional
    public ItemDto saveItem(SaveItemDto saveItemDto, String username) {
        User currentUser = currentUserContext.getCurrentUser(username);
        Inventory inventory = currentUserContext.findManagedEntity(
                currentUser,
                saveItemDto.getInventoryId(),
                inventoryRepository::findInventoryById,
                inventoryRepository::findInventoryByIdAndCompanyId
        );

        inventoryIntegrity.checkInventoryNotNull(inventory);
        inventoryIntegrity.checkInventoryCanManageItems(inventory);

        Item item = itemMapper.toItem(saveItemDto, inventory);
        Item savedItem = itemRepository.save(item);

        return itemMapper.toDto(savedItem);
    }

    @Transactional
    public ItemDto updateItem(UUID itemId, SaveItemDto saveItemDto, String username) {
        User currentUser = currentUserContext.getCurrentUser(username);
        Item item = currentUserContext.findManagedEntity(
                currentUser,
                itemId,
                itemRepository::findItemById,
                itemRepository::findItemByIdAndInventoryCompanyId
        );
        Inventory inventory = currentUserContext.findManagedEntity(
                currentUser,
                saveItemDto.getInventoryId(),
                inventoryRepository::findInventoryById,
                inventoryRepository::findInventoryByIdAndCompanyId
        );

        itemIntegrity.checkItemNotNull(item);
        inventoryIntegrity.checkInventoryNotNull(inventory);
        inventoryIntegrity.checkInventoryCanManageItems(inventory);

        itemMapper.updateItem(item, saveItemDto, inventory);

        return itemMapper.toDto(itemRepository.save(item));
    }

    @Transactional
    public void deleteItems(List<UUID> itemIds, String username) {
        User currentUser = currentUserContext.getCurrentUser(username);
        List<Item> itemsToDelete = itemIds.stream()
                .map(itemId -> currentUserContext.findManagedEntity(
                        currentUser,
                        itemId,
                        itemRepository::findItemById,
                        itemRepository::findItemByIdAndInventoryCompanyId
                ))
                .toList();

        itemsToDelete.forEach(item -> {
            itemIntegrity.checkItemNotNull(item);
            inventoryIntegrity.checkInventoryCanManageItems(item.getInventory());
        });

        itemRepository.deleteAll(itemsToDelete);
    }

}
