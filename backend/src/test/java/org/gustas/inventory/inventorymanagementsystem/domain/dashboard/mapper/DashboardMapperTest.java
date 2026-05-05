package org.gustas.inventory.inventorymanagementsystem.domain.dashboard.mapper;

import org.gustas.inventory.inventorymanagementsystem.domain.dashboard.dto.DashboardInventoryStatDto;
import org.gustas.inventory.inventorymanagementsystem.support.TestDataFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DashboardMapperTest {

    private final DashboardMapper dashboardMapper = new DashboardMapper();

    @Test
    void shouldMapInventoryStatsWithUnassignedLocation() {
        DashboardInventoryStatDto result = dashboardMapper.toInventoryStatDto(
                TestDataFactory.inventory(TestDataFactory.company(), null),
                3,
                11
        );

        assertThat(result.getInventoryName()).isEqualTo("Main Inventory");
        assertThat(result.getLocationName()).isEqualTo("Unassigned");
        assertThat(result.getTotalQuantity()).isEqualTo(11);
    }
}
