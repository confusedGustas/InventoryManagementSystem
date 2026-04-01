package org.gustas.inventory.inventorymanagementsystem.domain.inventory.service;

import lombok.RequiredArgsConstructor;
import org.gustas.inventory.inventorymanagementsystem.common.Constants;
import org.gustas.inventory.inventorymanagementsystem.common.dto.CompanyOptionDto;
import org.gustas.inventory.inventorymanagementsystem.common.dto.LocationOptionDto;
import org.gustas.inventory.inventorymanagementsystem.common.dto.PagedResponseDto;
import org.gustas.inventory.inventorymanagementsystem.common.dto.PagedResponseWithCompaniesAndLocationsDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryIntegrity inventoryIntegrity;
    private final InventoryMapper inventoryMapper;
    private final PagedResponseMapper pagedResponseMapper;
    private final CurrentUserContext currentUserContext;
    private final AuthUtils authUtils;
    private final CompanyMapper companyMapper;
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private final CompanyIntegrity companyIntegrity;
    private final LocationIntegrity locationIntegrity;

    @Transactional(readOnly = true)
    public PagedResponseWithCompaniesAndLocationsDto<InventoryDto, CompanyOptionDto, LocationOptionDto> getInventories(Integer page, String username) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), Constants.DEFAULT_PAGE_SIZE);
        User currentUser = currentUserContext.getCurrentUser(username);

        Page<InventoryDto> inventoryPage = (authUtils.isPlatformAdmin(currentUser)
                ? inventoryRepository.findAll(pageable)
                : inventoryRepository.findAllByCompanyId(currentUserContext.getManagedCompanyId(currentUser), pageable))
                .map(inventoryMapper::toDto);
        PagedResponseDto<InventoryDto> pagedInventories = pagedResponseMapper.toDto(inventoryPage);

        List<CompanyOptionDto> companies = currentUserContext.resolveManageableCompanies(currentUser, companyMapper::toCompanyOptionDto);
        List<LocationOptionDto> locations = resolveManageableLocations(currentUser);

        return pagedResponseMapper.toPagedWithCompaniesAndLocations(pagedInventories, companies, locations);
    }

    @Transactional
    public InventoryDto saveInventory(SaveInventoryDto saveInventoryDto, String username) {
        User currentUser = currentUserContext.getCurrentUser(username);
        Company company = currentUserContext.resolveCompany(saveInventoryDto.getCompanyId(), currentUser);
        Location location = resolveLocation(saveInventoryDto.getLocationId(), currentUser);

        companyIntegrity.checkCompanyCanManageInventories(company);
        inventoryIntegrity.checkLocationBelongsToCompany(location, company);
        inventoryIntegrity.checkInventoryNameAvailable(inventoryRepository, company.getId(), saveInventoryDto.getName(), null);
        inventoryIntegrity.checkInventoryCodeAvailable(inventoryRepository, company.getId(), saveInventoryDto.getCode(), null);

        Inventory inventory = inventoryMapper.toInventory(saveInventoryDto, company, location);
        Inventory savedInventory = inventoryRepository.save(inventory);

        return inventoryMapper.toDto(savedInventory);
    }

    @Transactional
    public InventoryDto updateInventory(UUID inventoryId, SaveInventoryDto saveInventoryDto, String username) {
        User currentUser = currentUserContext.getCurrentUser(username);
        Inventory inventory = currentUserContext.findManagedEntity(
                currentUser,
                inventoryId,
                inventoryRepository::findInventoryById,
                inventoryRepository::findInventoryByIdAndCompanyId
        );
        Company company = currentUserContext.resolveCompany(saveInventoryDto.getCompanyId(), currentUser);
        Location location = resolveLocation(saveInventoryDto.getLocationId(), currentUser);

        inventoryIntegrity.checkInventoryNotNull(inventory);
        companyIntegrity.checkCompanyCanManageInventories(company);
        inventoryIntegrity.checkLocationBelongsToCompany(location, company);
        inventoryIntegrity.checkInventoryNameAvailable(inventoryRepository, company.getId(), saveInventoryDto.getName(), inventory.getId());
        inventoryIntegrity.checkInventoryCodeAvailable(inventoryRepository, company.getId(), saveInventoryDto.getCode(), inventory.getId());

        inventoryMapper.updateInventory(inventory, saveInventoryDto, company, location);

        return inventoryMapper.toDto(inventoryRepository.save(inventory));
    }

    @Transactional
    public InventoryDto disableInventory(UUID inventoryId, String username) {
        User currentUser = currentUserContext.getCurrentUser(username);
        Inventory inventory = currentUserContext.findManagedEntity(
                currentUser,
                inventoryId,
                inventoryRepository::findInventoryById,
                inventoryRepository::findInventoryByIdAndCompanyId
        );

        inventoryIntegrity.checkInventoryNotNull(inventory);
        inventoryIntegrity.checkInventoryCanDisable(inventory);
        inventory.setEnabled(false);

        return inventoryMapper.toDto(inventoryRepository.save(inventory));
    }

    @Transactional
    public InventoryDto enableInventory(UUID inventoryId, String username) {
        User currentUser = currentUserContext.getCurrentUser(username);
        Inventory inventory = currentUserContext.findManagedEntity(
                currentUser,
                inventoryId,
                inventoryRepository::findInventoryById,
                inventoryRepository::findInventoryByIdAndCompanyId
        );

        inventoryIntegrity.checkInventoryNotNull(inventory);
        inventoryIntegrity.checkInventoryCanEnable(inventory);
        companyIntegrity.checkCompanyCanManageInventories(inventory.getCompany());
        inventory.setEnabled(true);

        return inventoryMapper.toDto(inventoryRepository.save(inventory));
    }

    private Location resolveLocation(UUID locationId, User currentUser) {
        if (locationId == null) {
            return null;
        }

        Location location = currentUserContext.findManagedEntity(
                currentUser,
                locationId,
                locationRepository::findLocationById,
                locationRepository::findLocationByIdAndCompanyId
        );
        locationIntegrity.checkLocationNotNull(location);

        return location;
    }

    private List<LocationOptionDto> resolveManageableLocations(User currentUser) {
        List<Location> locations = authUtils.isPlatformAdmin(currentUser)
                ? locationRepository.findAll().stream()
                .filter(Location::isEnabled)
                .sorted(Comparator.comparing(Location::getName, String.CASE_INSENSITIVE_ORDER))
                .toList()
                : locationRepository.findAllByCompanyId(currentUserContext.getManagedCompanyId(currentUser), Pageable.unpaged()).getContent().stream()
                .filter(Location::isEnabled)
                .sorted(Comparator.comparing(Location::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();

        return locations.stream()
                .map(locationMapper::toLocationOptionDto)
                .toList();
    }

}
