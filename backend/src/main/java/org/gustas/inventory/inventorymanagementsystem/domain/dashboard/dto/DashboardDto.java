package org.gustas.inventory.inventorymanagementsystem.domain.dashboard.dto;

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
public class DashboardDto {

    private CompanyOptionDto selectedCompany;
    private List<CompanyOptionDto> companies;
    private DashboardSummaryDto summary;
    private List<DashboardCompanyStatDto> companyBreakdown;
    private List<DashboardLocationStatDto> locationBreakdown;
    private List<DashboardInventoryStatDto> inventoryBreakdown;
    private List<DashboardCategoryStatDto> categoryBreakdown;
    private List<DashboardRecentItemDto> recentItems;

}
