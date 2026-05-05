package org.gustas.inventory.inventorymanagementsystem.domain.item.service;

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
import org.gustas.inventory.inventorymanagementsystem.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private ItemIntegrity itemIntegrity;
    @Mock
    private InventoryIntegrity inventoryIntegrity;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private CurrentUserContext currentUserContext;
    @Mock
    private PagedResponseMapper pagedResponseMapper;

    @InjectMocks
    private ItemService itemService;

    @Test
    void shouldFetchItemsForManagedInventory() {
        Inventory inventory = TestDataFactory.inventory(TestDataFactory.company(), null);
        User user = TestDataFactory.companyUser(TestDataFactory.company());
        Item item = TestDataFactory.item(inventory);
        ItemDto dto = ItemDto.builder().name("Laptop").build();
        when(currentUserContext.getCurrentUser("employee")).thenReturn(user);
        when(currentUserContext.findManagedEntity(
                any(User.class),
                any(UUID.class),
                org.mockito.ArgumentMatchers.<Function<UUID, Inventory>>any(),
                org.mockito.ArgumentMatchers.<BiFunction<UUID, UUID, Inventory>>any()
        )).thenReturn(inventory);
        when(itemRepository.findAllByInventoryId(org.mockito.ArgumentMatchers.eq(inventory.getId()), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(item)));
        when(itemMapper.toDto(item)).thenReturn(dto);
        when(pagedResponseMapper.toDto(any())).thenCallRealMethod();

        var result = itemService.getItems(inventory.getId(), 1, "employee");

        assertThat(result.getContent()).containsExactly(dto);
        verify(inventoryIntegrity).checkInventoryNotNull(inventory);
    }

    @Test
    void shouldSaveItem() {
        Inventory inventory = TestDataFactory.inventory(TestDataFactory.company(), null);
        User user = TestDataFactory.companyAdmin(TestDataFactory.company());
        SaveItemDto saveItemDto = TestDataFactory.saveItemDto(inventory.getId());
        Item item = TestDataFactory.item(inventory);
        ItemDto dto = ItemDto.builder().name("Laptop").build();
        when(currentUserContext.getCurrentUser("manager")).thenReturn(user);
        when(currentUserContext.findManagedEntity(
                any(User.class),
                eq(inventory.getId()),
                org.mockito.ArgumentMatchers.<Function<UUID, Inventory>>any(),
                org.mockito.ArgumentMatchers.<BiFunction<UUID, UUID, Inventory>>any()
        )).thenReturn(inventory);
        when(itemMapper.toItem(saveItemDto, inventory)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toDto(item)).thenReturn(dto);

        ItemDto result = itemService.saveItem(saveItemDto, "manager");

        assertThat(result).isSameAs(dto);
        verify(inventoryIntegrity).checkInventoryCanManageItems(inventory);
    }

    @Test
    void shouldUpdateItem() {
        Inventory inventory = TestDataFactory.inventory(TestDataFactory.company(), null);
        User user = TestDataFactory.companyAdmin(TestDataFactory.company());
        Item item = TestDataFactory.item(inventory);
        SaveItemDto saveItemDto = TestDataFactory.saveItemDto(inventory.getId());
        ItemDto dto = ItemDto.builder().name("Laptop").build();
        when(currentUserContext.getCurrentUser("manager")).thenReturn(user);
        when(currentUserContext.findManagedEntity(
                any(User.class),
                eq(item.getId()),
                org.mockito.ArgumentMatchers.<Function<UUID, Item>>any(),
                org.mockito.ArgumentMatchers.<BiFunction<UUID, UUID, Item>>any()
        )).thenReturn(item);
        when(currentUserContext.findManagedEntity(
                any(User.class),
                eq(inventory.getId()),
                org.mockito.ArgumentMatchers.<Function<UUID, Inventory>>any(),
                org.mockito.ArgumentMatchers.<BiFunction<UUID, UUID, Inventory>>any()
        )).thenReturn(inventory);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toDto(item)).thenReturn(dto);

        ItemDto result = itemService.updateItem(item.getId(), saveItemDto, "manager");

        assertThat(result).isSameAs(dto);
        verify(itemIntegrity).checkItemNotNull(item);
        verify(itemMapper).updateItem(item, saveItemDto, inventory);
    }

    @Test
    void shouldDeleteItems() {
        Inventory inventory = TestDataFactory.inventory(TestDataFactory.company(), null);
        User user = TestDataFactory.companyAdmin(TestDataFactory.company());
        Item item = TestDataFactory.item(inventory);
        when(currentUserContext.getCurrentUser("manager")).thenReturn(user);
        when(currentUserContext.findManagedEntity(
                any(User.class),
                eq(item.getId()),
                org.mockito.ArgumentMatchers.<Function<UUID, Item>>any(),
                org.mockito.ArgumentMatchers.<BiFunction<UUID, UUID, Item>>any()
        )).thenReturn(item);

        itemService.deleteItems(List.of(item.getId()), "manager");

        verify(itemIntegrity).checkItemNotNull(item);
        verify(inventoryIntegrity).checkInventoryCanManageItems(inventory);
        verify(itemRepository).deleteAll(List.of(item));
    }
}
