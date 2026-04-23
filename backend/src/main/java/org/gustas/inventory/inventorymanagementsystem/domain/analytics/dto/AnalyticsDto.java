package org.gustas.inventory.inventorymanagementsystem.domain.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.gustas.inventory.inventorymanagementsystem.common.dto.CompanyOptionDto;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDto {

    private CompanyOptionDto selectedCompany;
    private List<CompanyOptionDto> companies;
    private AnalyticsSummaryDto summary;
    private List<AnalyticsStockHealthDto> stockHealth;
    private List<AnalyticsCategoryInsightDto> categoryInsights;
    private List<AnalyticsBrandInsightDto> brandInsights;
    private List<AnalyticsLocationInsightDto> locationInsights;
    private List<AnalyticsDataQualityDto> dataQuality;
    private List<AnalyticsRecentActivityDto> recentActivity;

}
