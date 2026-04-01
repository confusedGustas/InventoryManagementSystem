package org.gustas.inventory.inventorymanagementsystem.domain.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gustas.inventory.inventorymanagementsystem.common.dto.PagedResponseDto;
import org.gustas.inventory.inventorymanagementsystem.domain.item.dto.ItemDto;
import org.gustas.inventory.inventorymanagementsystem.domain.item.dto.SaveItemDto;
import org.gustas.inventory.inventorymanagementsystem.domain.item.service.ItemService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'COMPANY_ADMIN', 'COMPANY_USER')")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public PagedResponseDto<ItemDto> getItems(
            Authentication authentication,
            @RequestParam UUID inventoryId,
            @RequestParam(name = "page", defaultValue = "1") Integer page
    ) {
        return itemService.getItems(inventoryId, page, authentication.getName());
    }

    @PostMapping
    public ItemDto saveItem(Authentication authentication, @Valid @RequestBody SaveItemDto saveItemDto) {
        return itemService.saveItem(saveItemDto, authentication.getName());
    }

    @PutMapping("/{itemId}")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'COMPANY_ADMIN')")
    public ItemDto updateItem(Authentication authentication, @PathVariable UUID itemId, @Valid @RequestBody SaveItemDto saveItemDto) {
        return itemService.updateItem(itemId, saveItemDto, authentication.getName());
    }

    @org.springframework.web.bind.annotation.DeleteMapping
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'COMPANY_ADMIN')")
    public void deleteItems(Authentication authentication, @RequestParam List<UUID> itemIds) {
        itemService.deleteItems(itemIds, authentication.getName());
    }

}
