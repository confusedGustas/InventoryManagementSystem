package org.gustas.inventory.inventorymanagementsystem.domain.analytics.mapper;

import org.gustas.inventory.inventorymanagementsystem.domain.analytics.dto.AnalyticsLocationInsightDto;
import org.gustas.inventory.inventorymanagementsystem.support.TestDataFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AnalyticsMapperTest {

    private final AnalyticsMapper analyticsMapper = new AnalyticsMapper();

    @Test
    void shouldMapLocationInsight() {
        AnalyticsLocationInsightDto result = analyticsMapper.toLocationInsightDto(
                TestDataFactory.location(TestDataFactory.company()),
                2,
                5,
                12,
                2.4
        );

        assertThat(result.getLocationName()).isEqualTo("Warehouse");
        assertThat(result.getCompanyName()).isEqualTo("Acme");
        assertThat(result.getTotalQuantity()).isEqualTo(12);
    }
}
