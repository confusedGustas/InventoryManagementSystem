package org.gustas.inventory.inventorymanagementsystem.domain.analytics.service;

import org.gustas.inventory.inventorymanagementsystem.common.dto.CompanyOptionDto;
import org.gustas.inventory.inventorymanagementsystem.common.service.CurrentUserContext;
import org.gustas.inventory.inventorymanagementsystem.common.service.InventoryAggregationHelper;
import org.gustas.inventory.inventorymanagementsystem.domain.analytics.dto.AnalyticsDto;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private CurrentUserContext currentUserContext;
    @Mock
    private AuthUtils authUtils;
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CompanyMapper companyMapper;

    private AnalyticsService analyticsService;

    @BeforeEach
    void setUp() {
        analyticsService = new AnalyticsService(
                currentUserContext,
                authUtils,
                companyRepository,
                locationRepository,
                inventoryRepository,
                itemRepository,
                companyMapper,
                new AnalyticsMapper(),
                new InventoryAggregationHelper()
        );
    }

    @Test
    void shouldBuildAnalyticsForCompanyAdmin() {
        Company company = TestDataFactory.company();
        User user = TestDataFactory.companyAdmin(company);
        Location location = TestDataFactory.location(company);
        Inventory inventory = TestDataFactory.inventory(company, location);
        Item laptop = TestDataFactory.item(inventory);
        laptop.setQuantity(0);
        laptop.setCreatedOn(LocalDateTime.of(2026, 1, 5, 10, 0));
        Item monitor = TestDataFactory.item(inventory);
        monitor.setId(java.util.UUID.randomUUID());
        monitor.setName("Monitor");
        monitor.setQuantity(5);
        monitor.setCategory("Displays");
        monitor.setBrand("Dell");
        monitor.setCreatedOn(LocalDateTime.of(2026, 2, 5, 10, 0));
        when(currentUserContext.getCurrentUser("manager")).thenReturn(user);
        when(currentUserContext.resolveManageableCompanies(eq(user), org.mockito.ArgumentMatchers.<Function<Company, CompanyOptionDto>>any()))
                .thenReturn(List.of(CompanyOptionDto.builder().id(company.getId()).name("Acme").build()));
        when(authUtils.isPlatformAdmin(user)).thenReturn(false);
        when(locationRepository.findAllByCompanyId(company.getId())).thenReturn(List.of(location));
        when(inventoryRepository.findAllByCompanyId(company.getId())).thenReturn(List.of(inventory));
        when(itemRepository.findAllByInventoryCompanyId(company.getId())).thenReturn(List.of(laptop, monitor));
        when(companyMapper.toCompanyOptionDto(company)).thenReturn(CompanyOptionDto.builder().id(company.getId()).name("Acme").build());

        AnalyticsDto result = analyticsService.getAnalytics(null, "manager");

        assertThat(result.getSelectedCompany().getName()).isEqualTo("Acme");
        assertThat(result.getCompanies()).hasSize(1);
        assertThat(result.getSummary().getTotalItems()).isEqualTo(2);
        assertThat(result.getSummary().getOutOfStockItems()).isEqualTo(1);
        assertThat(result.getSummary().getLowStockItems()).isEqualTo(1);
        assertThat(result.getCategoryInsights()).extracting("category").contains("Displays", "Electronics");
        assertThat(result.getBrandInsights()).extracting("brand").contains("Dell", "Lenovo");
        assertThat(result.getLocationInsights()).hasSize(1);
        assertThat(result.getRecentActivity()).hasSize(2);
        assertThat(result.getDataQuality()).extracting("label").contains("SKU coverage", "Description coverage");
    }
}
