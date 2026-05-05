package org.gustas.inventory.inventorymanagementsystem.domain.dashboard.service;

import org.gustas.inventory.inventorymanagementsystem.common.dto.CompanyOptionDto;
import org.gustas.inventory.inventorymanagementsystem.common.service.CurrentUserContext;
import org.gustas.inventory.inventorymanagementsystem.common.service.InventoryAggregationHelper;
import org.gustas.inventory.inventorymanagementsystem.domain.auth.utils.AuthUtils;
import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.Company;
import org.gustas.inventory.inventorymanagementsystem.domain.company.mapper.CompanyMapper;
import org.gustas.inventory.inventorymanagementsystem.domain.dashboard.dto.DashboardDto;
import org.gustas.inventory.inventorymanagementsystem.domain.dashboard.mapper.DashboardMapper;
import org.gustas.inventory.inventorymanagementsystem.domain.inventory.entity.Inventory;
import org.gustas.inventory.inventorymanagementsystem.domain.inventory.repository.InventoryRepository;
import org.gustas.inventory.inventorymanagementsystem.domain.item.entity.Item;
import org.gustas.inventory.inventorymanagementsystem.domain.item.repository.ItemRepository;
import org.gustas.inventory.inventorymanagementsystem.domain.location.entity.Location;
import org.gustas.inventory.inventorymanagementsystem.domain.location.repository.LocationRepository;
import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.User;
import org.gustas.inventory.inventorymanagementsystem.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private CurrentUserContext currentUserContext;
    @Mock
    private AuthUtils authUtils;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CompanyMapper companyMapper;

    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        dashboardService = new DashboardService(
                currentUserContext,
                authUtils,
                locationRepository,
                inventoryRepository,
                itemRepository,
                companyMapper,
                new DashboardMapper(),
                new InventoryAggregationHelper()
        );
    }

    @Test
    void shouldBuildDashboardForCompanyAdmin() {
        Company company = TestDataFactory.company();
        User user = TestDataFactory.companyAdmin(company);
        Location location = TestDataFactory.location(company);
        Inventory inventory = TestDataFactory.inventory(company, location);
        Item item = TestDataFactory.item(inventory);
        item.setCreatedOn(LocalDateTime.of(2026, 3, 1, 10, 0));
        when(currentUserContext.getCurrentUser("manager")).thenReturn(user);
        when(currentUserContext.resolveManageableCompanies(eq(user), org.mockito.ArgumentMatchers.<Function<Company, CompanyOptionDto>>any()))
                .thenReturn(List.of(CompanyOptionDto.builder().id(company.getId()).name("Acme").build()));
        when(currentUserContext.resolveScopedCompanies(user, company)).thenReturn(List.of(company));
        when(authUtils.isPlatformAdmin(user)).thenReturn(false);
        when(locationRepository.findAllByCompanyId(company.getId())).thenReturn(List.of(location));
        when(inventoryRepository.findAllByCompanyId(company.getId())).thenReturn(List.of(inventory));
        when(itemRepository.findAllByInventoryCompanyId(company.getId())).thenReturn(List.of(item));
        when(companyMapper.toCompanyOptionDto(company)).thenReturn(CompanyOptionDto.builder().id(company.getId()).name("Acme").build());

        DashboardDto result = dashboardService.getDashboard(null, "manager");

        assertThat(result.getCompanies()).hasSize(1);
        assertThat(result.getSummary().getTotalCompanies()).isEqualTo(1);
        assertThat(result.getSummary().getTotalItems()).isEqualTo(1);
        assertThat(result.getCompanyBreakdown()).hasSize(1);
        assertThat(result.getLocationBreakdown()).hasSize(1);
        assertThat(result.getInventoryBreakdown()).hasSize(1);
        assertThat(result.getCategoryBreakdown()).hasSize(1);
        assertThat(result.getRecentItems()).hasSize(1);
    }
}
