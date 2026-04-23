package org.gustas.inventory.inventorymanagementsystem.domain.analytics.service;

import lombok.RequiredArgsConstructor;
import org.gustas.inventory.inventorymanagementsystem.common.dto.CompanyOptionDto;
import org.gustas.inventory.inventorymanagementsystem.common.service.CurrentUserContext;
import org.gustas.inventory.inventorymanagementsystem.common.service.InventoryAggregationHelper;
import org.gustas.inventory.inventorymanagementsystem.domain.analytics.dto.AnalyticsBrandInsightDto;
import org.gustas.inventory.inventorymanagementsystem.domain.analytics.dto.AnalyticsCategoryInsightDto;
import org.gustas.inventory.inventorymanagementsystem.domain.analytics.dto.AnalyticsDataQualityDto;
import org.gustas.inventory.inventorymanagementsystem.domain.analytics.dto.AnalyticsDto;
import org.gustas.inventory.inventorymanagementsystem.domain.analytics.dto.AnalyticsLocationInsightDto;
import org.gustas.inventory.inventorymanagementsystem.domain.analytics.dto.AnalyticsRecentActivityDto;
import org.gustas.inventory.inventorymanagementsystem.domain.analytics.dto.AnalyticsStockHealthDto;
import org.gustas.inventory.inventorymanagementsystem.domain.analytics.dto.AnalyticsSummaryDto;
import org.gustas.inventory.inventorymanagementsystem.domain.analytics.mapper.AnalyticsMapper;
import org.gustas.inventory.inventorymanagementsystem.domain.auth.utils.AuthUtils;
import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.Company;
import org.gustas.inventory.inventorymanagementsystem.domain.company.mapper.CompanyMapper;
import org.gustas.inventory.inventorymanagementsystem.domain.company.repository.CompanyRepository;
import org.gustas.inventory.inventorymanagementsystem.domain.inventory.entity.Inventory;
import org.gustas.inventory.inventorymanagementsystem.domain.inventory.repository.InventoryRepository;
import org.gustas.inventory.inventorymanagementsystem.domain.item.entity.Item;
import org.gustas.inventory.inventorymanagementsystem.domain.item.repository.ItemRepository;
import org.gustas.inventory.inventorymanagementsystem.domain.location.entity.Location;
import org.gustas.inventory.inventorymanagementsystem.domain.location.repository.LocationRepository;
import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private static final int LOW_STOCK_THRESHOLD = 10;
    private static final int RECENT_ACTIVITY_LIMIT = 6;
    private static final DateTimeFormatter PERIOD_FORMATTER = DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH);

    private final CurrentUserContext currentUserContext;
    private final AuthUtils authUtils;
    private final CompanyRepository companyRepository;
    private final LocationRepository locationRepository;
    private final InventoryRepository inventoryRepository;
    private final ItemRepository itemRepository;
    private final CompanyMapper companyMapper;
    private final AnalyticsMapper analyticsMapper;
    private final InventoryAggregationHelper inventoryAggregationHelper;

    @Transactional(readOnly = true)
    public AnalyticsDto getAnalytics(UUID companyId, String username) {
        User currentUser = currentUserContext.getCurrentUser(username);
        List<CompanyOptionDto> companies = currentUserContext.resolveManageableCompanies(currentUser, companyMapper::toCompanyOptionDto);
        Company selectedCompany = resolveSelectedCompany(currentUser, companyId);

        List<Location> scopedLocations = resolveScopedLocations(selectedCompany, currentUser);
        List<Inventory> scopedInventories = resolveScopedInventories(selectedCompany, currentUser);
        List<Item> scopedItems = resolveScopedItems(selectedCompany, currentUser);

        Map<UUID, List<Inventory>> inventoriesByLocationId = inventoryAggregationHelper.groupInventoriesByLocationId(scopedInventories);
        Map<UUID, List<Item>> itemsByInventoryId = scopedItems.stream()
                .collect(Collectors.groupingBy(item -> item.getInventory().getId()));
        Map<String, List<Item>> itemsByCategory = scopedItems.stream()
                .collect(Collectors.groupingBy(item -> normalize(item.getCategory(), "Uncategorized")));
        Map<String, List<Item>> itemsByBrand = scopedItems.stream()
                .collect(Collectors.groupingBy(item -> normalize(item.getBrand(), "Unspecified")));

        List<AnalyticsStockHealthDto> stockHealth = List.of(
                analyticsMapper.toStockHealthDto(
                        "Out of stock",
                        (int) scopedItems.stream().filter(item -> quantity(item) == 0).count(),
                        0
                ),
                analyticsMapper.toStockHealthDto(
                        "Low stock",
                        (int) scopedItems.stream().filter(item -> quantity(item) > 0 && quantity(item) <= LOW_STOCK_THRESHOLD).count(),
                        scopedItems.stream().filter(item -> quantity(item) > 0 && quantity(item) <= LOW_STOCK_THRESHOLD).mapToInt(this::quantity).sum()
                ),
                analyticsMapper.toStockHealthDto(
                        "Healthy stock",
                        (int) scopedItems.stream().filter(item -> quantity(item) > LOW_STOCK_THRESHOLD).count(),
                        scopedItems.stream().filter(item -> quantity(item) > LOW_STOCK_THRESHOLD).mapToInt(this::quantity).sum()
                )
        );

        List<AnalyticsCategoryInsightDto> categoryInsights = itemsByCategory.entrySet().stream()
                .map(entry -> analyticsMapper.toCategoryInsightDto(
                        entry.getKey(),
                        entry.getValue().size(),
                        sumQuantity(entry.getValue()),
                        averageQuantity(entry.getValue())
                ))
                .sorted(Comparator.comparing(AnalyticsCategoryInsightDto::getTotalQuantity).reversed()
                        .thenComparing(AnalyticsCategoryInsightDto::getCategory, String.CASE_INSENSITIVE_ORDER))
                .toList();

        List<AnalyticsBrandInsightDto> brandInsights = itemsByBrand.entrySet().stream()
                .map(entry -> analyticsMapper.toBrandInsightDto(
                        entry.getKey(),
                        entry.getValue().size(),
                        sumQuantity(entry.getValue()),
                        averageQuantity(entry.getValue())
                ))
                .sorted(Comparator.comparing(AnalyticsBrandInsightDto::getTotalQuantity).reversed()
                        .thenComparing(AnalyticsBrandInsightDto::getBrand, String.CASE_INSENSITIVE_ORDER))
                .limit(10)
                .toList();

        List<AnalyticsLocationInsightDto> locationInsights = scopedLocations.stream()
                .map(location -> {
                    List<Inventory> locationInventories = inventoriesByLocationId.getOrDefault(location.getId(), List.of());
                    List<Item> locationItems = locationInventories.stream()
                            .flatMap(inventory -> itemsByInventoryId.getOrDefault(inventory.getId(), List.of()).stream())
                            .toList();

                    return analyticsMapper.toLocationInsightDto(
                            location,
                            locationInventories.size(),
                            locationItems.size(),
                            sumQuantity(locationItems),
                            averageQuantity(locationItems)
                    );
                })
                .sorted(Comparator.comparing(AnalyticsLocationInsightDto::getTotalQuantity).reversed()
                        .thenComparing(AnalyticsLocationInsightDto::getLocationName, String.CASE_INSENSITIVE_ORDER))
                .toList();

        List<AnalyticsDataQualityDto> dataQuality = List.of(
                toDataQualityDto("SKU coverage", scopedItems, Item::getSku),
                toDataQualityDto("Category coverage", scopedItems, Item::getCategory),
                toDataQualityDto("Brand coverage", scopedItems, Item::getBrand),
                toDataQualityDto("Manufacturer coverage", scopedItems, Item::getManufacturer),
                toDataQualityDto("Model coverage", scopedItems, Item::getModel),
                toDataQualityDto("Part number coverage", scopedItems, Item::getPartNumber),
                toDataQualityDto("Serial number coverage", scopedItems, Item::getSerialNumber),
                toDataQualityDto("Description coverage", scopedItems, Item::getDescription)
        );

        List<AnalyticsRecentActivityDto> recentActivity = scopedItems.stream()
                .filter(item -> item.getCreatedOn() != null)
                .collect(Collectors.groupingBy(item -> YearMonth.from(item.getCreatedOn()), LinkedHashMap::new, Collectors.toList()))
                .entrySet().stream()
                .sorted(Map.Entry.<YearMonth, List<Item>>comparingByKey().reversed())
                .limit(RECENT_ACTIVITY_LIMIT)
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> analyticsMapper.toRecentActivityDto(
                        entry.getKey().format(PERIOD_FORMATTER),
                        entry.getValue().size(),
                        sumQuantity(entry.getValue())
                ))
                .toList();

        AnalyticsSummaryDto summary = AnalyticsSummaryDto.builder()
                .totalItems(scopedItems.size())
                .totalQuantity(sumQuantity(scopedItems))
                .lowStockItems((int) scopedItems.stream().filter(item -> quantity(item) > 0 && quantity(item) <= LOW_STOCK_THRESHOLD).count())
                .outOfStockItems((int) scopedItems.stream().filter(item -> quantity(item) == 0).count())
                .uncategorizedItems((int) scopedItems.stream().filter(item -> normalize(item.getCategory(), "Uncategorized").equals("Uncategorized")).count())
                .inventoriesWithoutItems((int) scopedInventories.stream().filter(inventory -> itemsByInventoryId.getOrDefault(inventory.getId(), List.of()).isEmpty()).count())
                .averageQuantityPerItem(averageQuantity(scopedItems))
                .averageItemsPerInventory(scopedInventories.isEmpty() ? 0 : (double) scopedItems.size() / scopedInventories.size())
                .build();

        return AnalyticsDto.builder()
                .selectedCompany(selectedCompany != null ? companyMapper.toCompanyOptionDto(selectedCompany) : null)
                .companies(companies)
                .summary(summary)
                .stockHealth(stockHealth)
                .categoryInsights(categoryInsights)
                .brandInsights(brandInsights)
                .locationInsights(locationInsights)
                .dataQuality(dataQuality)
                .recentActivity(recentActivity)
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

    private AnalyticsDataQualityDto toDataQualityDto(String label, List<Item> items, Function<Item, String> getter) {
        int filledCount = (int) items.stream()
                .map(getter)
                .filter(Objects::nonNull)
                .filter(value -> !value.isBlank())
                .count();

        int totalCount = items.size();
        double completionRate = totalCount == 0 ? 0 : (double) filledCount / totalCount;

        return analyticsMapper.toDataQualityDto(label, filledCount, totalCount, completionRate);
    }

    private int sumQuantity(List<Item> items) {
        return items.stream()
                .mapToInt(this::quantity)
                .sum();
    }

    private int quantity(Item item) {
        return item.getQuantity() != null ? item.getQuantity() : 0;
    }

    private double averageQuantity(List<Item> items) {
        if (items.isEmpty()) {
            return 0;
        }

        return (double) sumQuantity(items) / items.size();
    }

    private String normalize(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

}
