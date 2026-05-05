package org.gustas.inventory.inventorymanagementsystem.domain.inventory.service;

import org.gustas.inventory.inventorymanagementsystem.common.mapper.PagedResponseMapper;
import org.gustas.inventory.inventorymanagementsystem.common.service.CurrentUserContext;
import org.gustas.inventory.inventorymanagementsystem.domain.auth.utils.AuthUtils;
import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.Company;
import org.gustas.inventory.inventorymanagementsystem.domain.company.integrity.CompanyIntegrity;
import org.gustas.inventory.inventorymanagementsystem.domain.company.mapper.CompanyMapper;
import org.gustas.inventory.inventorymanagementsystem.domain.inventory.dto.InventoryDto;
import org.gustas.inventory.inventorymanagementsystem.domain.inventory.dto.SaveInventoryDto;
import org.gustas.inventory.inventorymanagementsystem.domain.inventory.entity.Inventory;
import org.gustas.inventory.inventorymanagementsystem.domain.inventory.integrity.InventoryIntegrity;
import org.gustas.inventory.inventorymanagementsystem.domain.inventory.mapper.InventoryMapper;
import org.gustas.inventory.inventorymanagementsystem.domain.inventory.repository.InventoryRepository;
import org.gustas.inventory.inventorymanagementsystem.domain.location.entity.Location;
import org.gustas.inventory.inventorymanagementsystem.domain.location.integrity.LocationIntegrity;
import org.gustas.inventory.inventorymanagementsystem.domain.location.mapper.LocationMapper;
import org.gustas.inventory.inventorymanagementsystem.domain.location.repository.LocationRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private InventoryIntegrity inventoryIntegrity;
    @Mock
    private InventoryMapper inventoryMapper;
    @Mock
    private PagedResponseMapper pagedResponseMapper;
    @Mock
    private CurrentUserContext currentUserContext;
    @Mock
    private AuthUtils authUtils;
    @Mock
    private CompanyMapper companyMapper;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private LocationMapper locationMapper;
    @Mock
    private CompanyIntegrity companyIntegrity;
    @Mock
    private LocationIntegrity locationIntegrity;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void shouldReturnAllInventoriesForPlatformAdmin() {
        Company company = TestDataFactory.company();
        Location location = TestDataFactory.location(company);
        Inventory inventory = TestDataFactory.inventory(company, location);
        User admin = TestDataFactory.platformAdmin();
        InventoryDto dto = InventoryDto.builder().name("Main Inventory").build();
        when(currentUserContext.getCurrentUser("admin")).thenReturn(admin);
        when(authUtils.isPlatformAdmin(admin)).thenReturn(true);
        when(inventoryRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(inventory)));
        when(inventoryMapper.toDto(inventory)).thenReturn(dto);
        when(pagedResponseMapper.toDto(any())).thenCallRealMethod();
        when(currentUserContext.resolveManageableCompanies(any(), any())).thenReturn(List.of());
        when(locationRepository.findAll()).thenReturn(List.of(location));
        when(locationMapper.toLocationOptionDto(location)).thenReturn(null);
        when(pagedResponseMapper.toPagedWithCompaniesAndLocations(any(), any(), any())).thenCallRealMethod();

        var result = inventoryService.getInventories(1, "admin");

        assertThat(result.getContent()).containsExactly(dto);
        verify(inventoryRepository).findAll(any(Pageable.class));
    }

    @Test
    void shouldSaveInventory() {
        Company company = TestDataFactory.company();
        Location location = TestDataFactory.location(company);
        User user = TestDataFactory.companyAdmin(company);
        SaveInventoryDto saveInventoryDto = TestDataFactory.saveInventoryDto(company.getId(), location.getId());
        Inventory inventory = TestDataFactory.inventory(company, location);
        InventoryDto dto = InventoryDto.builder().name("Main Inventory").build();
        when(currentUserContext.getCurrentUser("manager")).thenReturn(user);
        when(currentUserContext.resolveCompany(company.getId(), user)).thenReturn(company);
        when(currentUserContext.findManagedEntity(any(), eq(location.getId()), any(), any())).thenReturn(location);
        when(inventoryMapper.toInventory(saveInventoryDto, company, location)).thenReturn(inventory);
        when(inventoryRepository.save(inventory)).thenReturn(inventory);
        when(inventoryMapper.toDto(inventory)).thenReturn(dto);

        InventoryDto result = inventoryService.saveInventory(saveInventoryDto, "manager");

        assertThat(result).isSameAs(dto);
        verify(companyIntegrity).checkCompanyCanManageInventories(company);
        verify(inventoryIntegrity).checkLocationBelongsToCompany(location, company);
        verify(inventoryIntegrity).checkInventoryNameAvailable(inventoryRepository, company.getId(), saveInventoryDto.getName(), null);
        verify(inventoryIntegrity).checkInventoryCodeAvailable(inventoryRepository, company.getId(), saveInventoryDto.getCode(), null);
    }

    @Test
    void shouldUpdateInventory() {
        Company company = TestDataFactory.company();
        Location location = TestDataFactory.location(company);
        User user = TestDataFactory.companyAdmin(company);
        SaveInventoryDto saveInventoryDto = TestDataFactory.saveInventoryDto(company.getId(), location.getId());
        Inventory inventory = TestDataFactory.inventory(company, location);
        InventoryDto dto = InventoryDto.builder().name("Main Inventory").build();
        when(currentUserContext.getCurrentUser("manager")).thenReturn(user);
        when(currentUserContext.findManagedEntity(any(), eq(inventory.getId()), any(), any())).thenReturn(inventory);
        when(currentUserContext.resolveCompany(company.getId(), user)).thenReturn(company);
        when(currentUserContext.findManagedEntity(any(), eq(location.getId()), any(), any())).thenReturn(location);
        when(inventoryRepository.save(inventory)).thenReturn(inventory);
        when(inventoryMapper.toDto(inventory)).thenReturn(dto);

        InventoryDto result = inventoryService.updateInventory(inventory.getId(), saveInventoryDto, "manager");

        assertThat(result).isSameAs(dto);
        verify(inventoryIntegrity).checkInventoryNotNull(inventory);
        verify(inventoryMapper).updateInventory(inventory, saveInventoryDto, company, location);
    }

    @Test
    void shouldDisableInventory() {
        Company company = TestDataFactory.company();
        User user = TestDataFactory.companyAdmin(company);
        Inventory inventory = TestDataFactory.inventory(company, null);
        when(currentUserContext.getCurrentUser("manager")).thenReturn(user);
        when(currentUserContext.findManagedEntity(any(), eq(inventory.getId()), any(), any())).thenReturn(inventory);
        when(inventoryRepository.save(inventory)).thenReturn(inventory);
        when(inventoryMapper.toDto(inventory)).thenReturn(InventoryDto.builder().enabled(false).build());

        InventoryDto result = inventoryService.disableInventory(inventory.getId(), "manager");

        assertThat(inventory.isEnabled()).isFalse();
        assertThat(result.isEnabled()).isFalse();
        verify(inventoryIntegrity).checkInventoryCanDisable(inventory);
    }

    @Test
    void shouldEnableInventory() {
        Company company = TestDataFactory.company();
        User user = TestDataFactory.companyAdmin(company);
        Inventory inventory = TestDataFactory.inventory(company, null);
        inventory.setEnabled(false);
        when(currentUserContext.getCurrentUser("manager")).thenReturn(user);
        when(currentUserContext.findManagedEntity(any(), eq(inventory.getId()), any(), any())).thenReturn(inventory);
        when(inventoryRepository.save(inventory)).thenReturn(inventory);
        when(inventoryMapper.toDto(inventory)).thenReturn(InventoryDto.builder().enabled(true).build());

        InventoryDto result = inventoryService.enableInventory(inventory.getId(), "manager");

        assertThat(inventory.isEnabled()).isTrue();
        assertThat(result.isEnabled()).isTrue();
        verify(inventoryIntegrity).checkInventoryCanEnable(inventory);
        verify(companyIntegrity).checkCompanyCanManageInventories(company);
    }
}
