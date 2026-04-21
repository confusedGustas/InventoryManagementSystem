package org.gustas.inventory.inventorymanagementsystem.domain.dashboard.service;

import lombok.RequiredArgsConstructor;
import org.gustas.inventory.inventorymanagementsystem.common.dto.CompanyOptionDto;
import org.gustas.inventory.inventorymanagementsystem.common.service.CurrentUserContext;
import org.gustas.inventory.inventorymanagementsystem.domain.auth.utils.AuthUtils;
import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.Company;
import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.CompanyStatus;
import org.gustas.inventory.inventorymanagementsystem.domain.company.mapper.CompanyMapper;
import org.gustas.inventory.inventorymanagementsystem.domain.company.repository.CompanyRepository;
import org.gustas.inventory.inventorymanagementsystem.domain.dashboard.dto.DashboardCategoryStatDto;
import org.gustas.inventory.inventorymanagementsystem.domain.dashboard.dto.DashboardCompanyStatDto;
import org.gustas.inventory.inventorymanagementsystem.domain.dashboard.dto.DashboardDto;
import org.gustas.inventory.inventorymanagementsystem.domain.dashboard.dto.DashboardInventoryStatDto;
import org.gustas.inventory.inventorymanagementsystem.domain.dashboard.dto.DashboardLocationStatDto;
import org.gustas.inventory.inventorymanagementsystem.domain.dashboard.dto.DashboardRecentItemDto;
import org.gustas.inventory.inventorymanagementsystem.domain.dashboard.dto.DashboardSummaryDto;
import org.gustas.inventory.inventorymanagementsystem.domain.dashboard.mapper.DashboardMapper;
import org.gustas.inventory.inventorymanagementsystem.domain.inventory.entity.Inventory;
import org.gustas.inventory.inventorymanagementsystem.domain.inventory.repository.InventoryRepository;
import org.gustas.inventory.inventorymanagementsystem.domain.item.entity.Item;
import org.gustas.inventory.inventorymanagementsystem.domain.item.repository.ItemRepository;
import org.gustas.inventory.inventorymanagementsystem.domain.location.entity.Location;
import org.gustas.inventory.inventorymanagementsystem.domain.location.repository.LocationRepository;
import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final int LOW_STOCK_THRESHOLD = 10;
    private static final int RECENT_ITEMS_LIMIT = 8;

    private final CurrentUserContext currentUserContext;
    private final AuthUtils authUtils;
    private final CompanyRepository companyRepository;
    private final LocationRepository locationRepository;
    private final InventoryRepository inventoryRepository;
    private final ItemRepository itemRepository;
    private final CompanyMapper companyMapper;
    private final DashboardMapper dashboardMapper;

    @Transactional(readOnly = true)
    public DashboardDto getDashboard(UUID companyId, String username) {
        User currentUser = currentUserContext.getCurrentUser(username);
        List<CompanyOptionDto> companies = currentUserContext.resolveManageableCompanies(currentUser, companyMapper::toCompanyOptionDto);
        Company selectedCompany = resolveSelectedCompany(currentUser, companyId);

        List<Company> scopedCompanies = resolveScopedCompanies(selectedCompany, currentUser);
        List<Location> scopedLocations = resolveScopedLocations(selectedCompany, currentUser);
        List<Inventory> scopedInventories = resolveScopedInventories(selectedCompany, currentUser);
        List<Item> scopedItems = resolveScopedItems(selectedCompany, currentUser);

        Map<UUID, List<Location>> locationsByCompanyId = scopedLocations.stream()
                .collect(Collectors.groupingBy(location -> location.getCompany().getId()));
        Map<UUID, List<Inventory>> inventoriesByCompanyId = scopedInventories.stream()
                .collect(Collectors.groupingBy(inventory -> inventory.getCompany().getId()));
        Map<UUID, List<Item>> itemsByCompanyId = scopedItems.stream()
                .collect(Collectors.groupingBy(item -> item.getInventory().getCompany().getId()));
        Map<UUID, List<Inventory>> inventoriesByLocationId = scopedInventories.stream()
                .filter(inventory -> inventory.getLocation() != null)
                .collect(Collectors.groupingBy(inventory -> inventory.getLocation().getId()));
        Map<UUID, List<Item>> itemsByInventoryId = scopedItems.stream()
                .collect(Collectors.groupingBy(item -> item.getInventory().getId()));
        Map<String, List<Item>> itemsByCategory = scopedItems.stream()
                .collect(Collectors.groupingBy(item -> normalizeCategory(item.getCategory())));

        List<DashboardCompanyStatDto> companyBreakdown = scopedCompanies.stream()
                .sorted(Comparator.comparing(Company::getName, String.CASE_INSENSITIVE_ORDER))
                .map(company -> dashboardMapper.toCompanyStatDto(
                        company,
                        locationsByCompanyId.getOrDefault(company.getId(), List.of()).size(),
                        inventoriesByCompanyId.getOrDefault(company.getId(), List.of()).size(),
                        itemsByCompanyId.getOrDefault(company.getId(), List.of()).size(),
                        sumQuantity(itemsByCompanyId.getOrDefault(company.getId(), List.of()))
                ))
                .toList();

        List<DashboardLocationStatDto> locationBreakdown = scopedLocations.stream()
                .sorted(Comparator.comparing(Location::getName, String.CASE_INSENSITIVE_ORDER))
                .map(location -> {
                    List<Inventory> locationInventories = inventoriesByLocationId.getOrDefault(location.getId(), List.of());
                    List<Item> locationItems = locationInventories.stream()
                            .flatMap(inventory -> itemsByInventoryId.getOrDefault(inventory.getId(), List.of()).stream())
                            .toList();

                    return dashboardMapper.toLocationStatDto(
                            location,
                            locationInventories.size(),
                            locationItems.size(),
                            sumQuantity(locationItems)
                    );
                })
                .toList();

        List<DashboardInventoryStatDto> inventoryBreakdown = scopedInventories.stream()
                .sorted(Comparator.comparing(Inventory::getName, String.CASE_INSENSITIVE_ORDER))
                .map(inventory -> dashboardMapper.toInventoryStatDto(
                        inventory,
                        itemsByInventoryId.getOrDefault(inventory.getId(), List.of()).size(),
                        sumQuantity(itemsByInventoryId.getOrDefault(inventory.getId(), List.of()))
                ))
                .toList();

        List<DashboardCategoryStatDto> categoryBreakdown = itemsByCategory.entrySet().stream()
                .map(entry -> dashboardMapper.toCategoryStatDto(
                        entry.getKey(),
                        entry.getValue().size(),
                        sumQuantity(entry.getValue())
                ))
                .sorted(Comparator.comparing(DashboardCategoryStatDto::getTotalQuantity).reversed()
                        .thenComparing(DashboardCategoryStatDto::getCategory, String.CASE_INSENSITIVE_ORDER))
                .toList();

        List<DashboardRecentItemDto> recentItems = scopedItems.stream()
                .sorted(Comparator.comparing(Item::getCreatedOn, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(RECENT_ITEMS_LIMIT)
                .map(dashboardMapper::toRecentItemDto)
                .toList();

        DashboardSummaryDto summary = DashboardSummaryDto.builder()
                .totalCompanies(scopedCompanies.size())
                .activeCompanies((int) scopedCompanies.stream().filter(company -> company.getStatus() == CompanyStatus.ACTIVE).count())
                .totalLocations(scopedLocations.size())
                .enabledLocations((int) scopedLocations.stream().filter(Location::isEnabled).count())
                .totalInventories(scopedInventories.size())
                .enabledInventories((int) scopedInventories.stream().filter(Inventory::isEnabled).count())
                .totalItems(scopedItems.size())
                .totalQuantity(sumQuantity(scopedItems))
                .lowStockItems((int) scopedItems.stream().filter(item -> (item.getQuantity() != null ? item.getQuantity() : 0) <= LOW_STOCK_THRESHOLD).count())
                .build();

        return DashboardDto.builder()
                .selectedCompany(selectedCompany != null ? companyMapper.toCompanyOptionDto(selectedCompany) : null)
                .companies(companies)
                .summary(summary)
                .companyBreakdown(companyBreakdown)
                .locationBreakdown(locationBreakdown)
                .inventoryBreakdown(inventoryBreakdown)
                .categoryBreakdown(categoryBreakdown)
                .recentItems(recentItems)
                .build();
    }

    private Company resolveSelectedCompany(User currentUser, UUID companyId) {
        if (!authUtils.isPlatformAdmin(currentUser)) {
            return currentUser.getCompany();
        }

        if (companyId == null) {
            return null;
        }

        return currentUserContext.resolveCompany(companyId, currentUser);
    }

    private List<Company> resolveScopedCompanies(Company selectedCompany, User currentUser) {
        if (selectedCompany != null) {
            return List.of(selectedCompany);
        }

        if (authUtils.isPlatformAdmin(currentUser)) {
            return companyRepository.findAll().stream()
                    .sorted(Comparator.comparing(Company::getName, String.CASE_INSENSITIVE_ORDER))
                    .toList();
        }

        return List.of(currentUser.getCompany());
    }

    private List<Location> resolveScopedLocations(Company selectedCompany, User currentUser) {
        if (selectedCompany != null) {
            return locationRepository.findAllByCompanyId(selectedCompany.getId());
        }

        if (authUtils.isPlatformAdmin(currentUser)) {
            return locationRepository.findAll();
        }

        return locationRepository.findAllByCompanyId(currentUserContext.getManagedCompanyId(currentUser));
    }

    private List<Inventory> resolveScopedInventories(Company selectedCompany, User currentUser) {
        if (selectedCompany != null) {
            return inventoryRepository.findAllByCompanyId(selectedCompany.getId());
        }

        if (authUtils.isPlatformAdmin(currentUser)) {
            return inventoryRepository.findAll();
        }

        return inventoryRepository.findAllByCompanyId(currentUserContext.getManagedCompanyId(currentUser));
    }

    private List<Item> resolveScopedItems(Company selectedCompany, User currentUser) {
        if (selectedCompany != null) {
            return itemRepository.findAllByInventoryCompanyId(selectedCompany.getId());
        }

        if (authUtils.isPlatformAdmin(currentUser)) {
            return itemRepository.findAll();
        }

        return itemRepository.findAllByInventoryCompanyId(currentUserContext.getManagedCompanyId(currentUser));
    }

    private int sumQuantity(List<Item> items) {
        return items.stream()
                .map(Item::getQuantity)
                .filter(Objects::nonNull)
                .reduce(0, Integer::sum);
    }

    private String normalizeCategory(String category) {
        return category == null || category.isBlank() ? "Uncategorized" : category;
    }

}
