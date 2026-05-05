package org.gustas.inventory.inventorymanagementsystem.domain.location.mapper;

import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.Company;
import org.gustas.inventory.inventorymanagementsystem.domain.location.entity.Location;
import org.gustas.inventory.inventorymanagementsystem.support.TestDataFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LocationMapperTest {

    private final LocationMapper locationMapper = new LocationMapper();

    @Test
    void shouldNormalizeLocationOnCreate() {
        Company company = TestDataFactory.company();

        Location result = locationMapper.toLocation(TestDataFactory.saveLocationDto(company.getId()), company);

        assertThat(result.getName()).isEqualTo("Warehouse");
        assertThat(result.getDomain()).isEqualTo("warehouse.acme.test");
        assertThat(result.isEnabled()).isTrue();
    }

    @Test
    void shouldMapLocationToDtoAndOptionDto() {
        Company company = TestDataFactory.company();
        Location location = TestDataFactory.location(company);

        var dto = locationMapper.toDto(location);
        var optionDto = locationMapper.toLocationOptionDto(location);

        assertThat(dto.getCompanyName()).isEqualTo("Acme");
        assertThat(dto.getCompanyStatus()).isEqualTo(company.getStatus().name());
        assertThat(optionDto.getId()).isEqualTo(location.getId());
        assertThat(optionDto.getCompanyId()).isEqualTo(company.getId());
    }

    @Test
    void shouldUpdateLocationAndNormalizeDomain() {
        Company company = TestDataFactory.company();
        Location location = TestDataFactory.location(company);
        Company updatedCompany = TestDataFactory.company();
        updatedCompany.setId(java.util.UUID.randomUUID());
        updatedCompany.setName("Northwind");
        var dto = TestDataFactory.saveLocationDto(updatedCompany.getId());
        dto.setName("  Outlet  ");
        dto.setDomain("  Outlet.NORTHWIND.Test  ");

        locationMapper.updateLocation(location, dto, updatedCompany);

        assertThat(location.getName()).isEqualTo("Outlet");
        assertThat(location.getDomain()).isEqualTo("outlet.northwind.test");
        assertThat(location.getCompany()).isSameAs(updatedCompany);
    }
}
