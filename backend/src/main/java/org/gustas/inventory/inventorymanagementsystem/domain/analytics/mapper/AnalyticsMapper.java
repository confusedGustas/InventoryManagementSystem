package org.gustas.inventory.inventorymanagementsystem.domain.analytics.mapper;

import org.gustas.inventory.inventorymanagementsystem.domain.analytics.dto.AnalyticsBrandInsightDto;
import org.gustas.inventory.inventorymanagementsystem.domain.analytics.dto.AnalyticsCategoryInsightDto;
import org.gustas.inventory.inventorymanagementsystem.domain.analytics.dto.AnalyticsDataQualityDto;
import org.gustas.inventory.inventorymanagementsystem.domain.analytics.dto.AnalyticsLocationInsightDto;
import org.gustas.inventory.inventorymanagementsystem.domain.analytics.dto.AnalyticsRecentActivityDto;
import org.gustas.inventory.inventorymanagementsystem.domain.analytics.dto.AnalyticsStockHealthDto;
import org.gustas.inventory.inventorymanagementsystem.domain.location.entity.Location;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsMapper {

    public AnalyticsStockHealthDto toStockHealthDto(String status, int itemCount, int totalQuantity) {
        return AnalyticsStockHealthDto.builder()
                .status(status)
                .itemCount(itemCount)
                .totalQuantity(totalQuantity)
                .build();
    }

    public AnalyticsCategoryInsightDto toCategoryInsightDto(String category, int itemCount, int totalQuantity, double averageQuantity) {
        return AnalyticsCategoryInsightDto.builder()
                .category(category)
                .itemCount(itemCount)
                .totalQuantity(totalQuantity)
                .averageQuantity(averageQuantity)
                .build();
    }

    public AnalyticsBrandInsightDto toBrandInsightDto(String brand, int itemCount, int totalQuantity, double averageQuantity) {
        return AnalyticsBrandInsightDto.builder()
                .brand(brand)
                .itemCount(itemCount)
                .totalQuantity(totalQuantity)
                .averageQuantity(averageQuantity)
                .build();
    }

    public AnalyticsLocationInsightDto toLocationInsightDto(
            Location location,
            int inventoryCount,
            int itemCount,
            int totalQuantity,
            double averageQuantityPerItem
    ) {
        return AnalyticsLocationInsightDto.builder()
                .locationId(location.getId())
                .locationName(location.getName())
                .companyName(location.getCompany().getName())
                .enabled(location.isEnabled())
                .inventoryCount(inventoryCount)
                .itemCount(itemCount)
                .totalQuantity(totalQuantity)
                .averageQuantityPerItem(averageQuantityPerItem)
                .build();
    }

    public AnalyticsDataQualityDto toDataQualityDto(String label, int filledCount, int totalCount, double completionRate) {
        return AnalyticsDataQualityDto.builder()
                .label(label)
                .filledCount(filledCount)
                .totalCount(totalCount)
                .completionRate(completionRate)
                .build();
    }

    public AnalyticsRecentActivityDto toRecentActivityDto(String period, int itemsAdded, int quantityAdded) {
        return AnalyticsRecentActivityDto.builder()
                .period(period)
                .itemsAdded(itemsAdded)
                .quantityAdded(quantityAdded)
                .build();
    }

}
