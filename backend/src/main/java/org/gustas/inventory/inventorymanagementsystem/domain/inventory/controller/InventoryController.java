package org.gustas.inventory.inventorymanagementsystem.domain.inventory.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gustas.inventory.inventorymanagementsystem.common.dto.CompanyOptionDto;
import org.gustas.inventory.inventorymanagementsystem.common.dto.LocationOptionDto;
import org.gustas.inventory.inventorymanagementsystem.common.dto.PagedResponseWithCompaniesAndLocationsDto;
import org.gustas.inventory.inventorymanagementsystem.domain.inventory.dto.InventoryDto;
import org.gustas.inventory.inventorymanagementsystem.domain.inventory.dto.SaveInventoryDto;
import org.gustas.inventory.inventorymanagementsystem.domain.inventory.service.InventoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequestMapping("/inventories")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'COMPANY_ADMIN', 'COMPANY_USER')")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public PagedResponseWithCompaniesAndLocationsDto<InventoryDto, CompanyOptionDto, LocationOptionDto> getInventories(
            Authentication authentication,
            @RequestParam(name = "page", defaultValue = "1") Integer page
    ) {
        return inventoryService.getInventories(page, authentication.getName());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'COMPANY_ADMIN')")
    public InventoryDto saveInventory(Authentication authentication, @Valid @RequestBody SaveInventoryDto saveInventoryDto) {
        return inventoryService.saveInventory(saveInventoryDto, authentication.getName());
    }

    @PutMapping("/{inventoryId}")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'COMPANY_ADMIN')")
    public InventoryDto updateInventory(
            Authentication authentication,
            @PathVariable UUID inventoryId,
            @Valid @RequestBody SaveInventoryDto saveInventoryDto
    ) {
        return inventoryService.updateInventory(inventoryId, saveInventoryDto, authentication.getName());
    }

    @PatchMapping("/{inventoryId}/disable")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'COMPANY_ADMIN')")
    public InventoryDto disableInventory(Authentication authentication, @PathVariable UUID inventoryId) {
        return inventoryService.disableInventory(inventoryId, authentication.getName());
    }

    @PatchMapping("/{inventoryId}/enable")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'COMPANY_ADMIN')")
    public InventoryDto enableInventory(Authentication authentication, @PathVariable UUID inventoryId) {
        return inventoryService.enableInventory(inventoryId, authentication.getName());
    }

}
