package org.gustas.inventory.inventorymanagementsystem.domain.location.service;

import org.gustas.inventory.inventorymanagementsystem.common.mapper.PagedResponseMapper;
import org.gustas.inventory.inventorymanagementsystem.common.service.CurrentUserContext;
import org.gustas.inventory.inventorymanagementsystem.domain.auth.utils.AuthUtils;
import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.Company;
import org.gustas.inventory.inventorymanagementsystem.domain.company.integrity.CompanyIntegrity;
import org.gustas.inventory.inventorymanagementsystem.domain.company.mapper.CompanyMapper;
import org.gustas.inventory.inventorymanagementsystem.domain.location.dto.LocationDto;
import org.gustas.inventory.inventorymanagementsystem.domain.location.dto.SaveLocationDto;
import org.gustas.inventory.inventorymanagementsystem.domain.location.entity.Location;
import org.gustas.inventory.inventorymanagementsystem.domain.location.integrity.LocationIntegrity;
import org.gustas.inventory.inventorymanagementsystem.domain.location.mapper.LocationMapper;
import org.gustas.inventory.inventorymanagementsystem.domain.location.repository.LocationRepository;
import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.User;
import org.gustas.inventory.inventorymanagementsystem.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;
    @Mock
    private CompanyIntegrity companyIntegrity;
    @Mock
    private LocationIntegrity locationIntegrity;
    @Mock
    private LocationMapper locationMapper;
    @Mock
    private PagedResponseMapper pagedResponseMapper;
    @Mock
    private CurrentUserContext currentUserContext;
    @Mock
    private AuthUtils authUtils;
    @Mock
    private CompanyMapper companyMapper;

    @InjectMocks
    private LocationService locationService;

    @Test
    void shouldReturnScopedLocationsForCompanyAdmin() {
        Company company = TestDataFactory.company();
        User user = TestDataFactory.companyAdmin(company);
        Location location = TestDataFactory.location(company);
        LocationDto dto = LocationDto.builder().name("Warehouse").build();
        when(currentUserContext.getCurrentUser("manager")).thenReturn(user);
        when(authUtils.isPlatformAdmin(user)).thenReturn(false);
        when(currentUserContext.getManagedCompanyId(user)).thenReturn(company.getId());
        when(locationRepository.findAllByCompanyId(eq(company.getId()), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(location)));
        when(locationMapper.toDto(location)).thenReturn(dto);
        when(pagedResponseMapper.toDto(any())).thenCallRealMethod();
        when(currentUserContext.resolveManageableCompanies(eq(user), org.mockito.ArgumentMatchers.<Function<Company, ?>>any())).thenReturn(List.of());
        when(pagedResponseMapper.toPagedWithCompanies(any(), any())).thenCallRealMethod();

        var result = locationService.getLocations(1, "manager");

        assertThat(result.getContent()).containsExactly(dto);
        verify(locationRepository).findAllByCompanyId(eq(company.getId()), any(Pageable.class));
    }

    @Test
    void shouldSaveLocation() {
        Company company = TestDataFactory.company();
        User user = TestDataFactory.companyAdmin(company);
        SaveLocationDto saveLocationDto = TestDataFactory.saveLocationDto(company.getId());
        Location location = TestDataFactory.location(company);
        LocationDto dto = LocationDto.builder().name("Warehouse").build();
        when(currentUserContext.getCurrentUser("manager")).thenReturn(user);
        when(currentUserContext.resolveCompany(company.getId(), user)).thenReturn(company);
        when(locationMapper.toLocation(saveLocationDto, company)).thenReturn(location);
        when(locationRepository.save(location)).thenReturn(location);
        when(locationMapper.toDto(location)).thenReturn(dto);

        LocationDto result = locationService.saveLocation(saveLocationDto, "manager");

        assertThat(result).isSameAs(dto);
        verify(companyIntegrity).checkCompanyNotNull(company);
        verify(companyIntegrity).checkCompanyCanManageLocations(company);
        verify(locationIntegrity).checkLocationNameAvailable(locationRepository, company.getId(), saveLocationDto.getName(), null);
    }

    @Test
    void shouldUpdateLocation() {
        Company company = TestDataFactory.company();
        User user = TestDataFactory.companyAdmin(company);
        Location location = TestDataFactory.location(company);
        SaveLocationDto saveLocationDto = TestDataFactory.saveLocationDto(company.getId());
        LocationDto dto = LocationDto.builder().name("Warehouse").build();
        when(currentUserContext.getCurrentUser("manager")).thenReturn(user);
        when(currentUserContext.findManagedEntity(any(), eq(location.getId()), any(), any())).thenReturn(location);
        when(currentUserContext.resolveCompany(company.getId(), user)).thenReturn(company);
        when(locationRepository.save(location)).thenReturn(location);
        when(locationMapper.toDto(location)).thenReturn(dto);

        LocationDto result = locationService.updateLocation(location.getId(), saveLocationDto, "manager");

        assertThat(result).isSameAs(dto);
        verify(locationIntegrity).checkLocationNotNull(location);
        verify(locationIntegrity).checkLocationNameAvailable(locationRepository, company.getId(), saveLocationDto.getName(), location.getId());
        verify(locationMapper).updateLocation(location, saveLocationDto, company);
    }

    @Test
    void shouldDisableLocation() {
        Company company = TestDataFactory.company();
        User user = TestDataFactory.companyAdmin(company);
        Location location = TestDataFactory.location(company);
        when(currentUserContext.getCurrentUser("manager")).thenReturn(user);
        when(currentUserContext.findManagedEntity(any(), eq(location.getId()), any(), any())).thenReturn(location);
        when(locationRepository.save(location)).thenReturn(location);
        when(locationMapper.toDto(location)).thenReturn(LocationDto.builder().enabled(false).build());

        LocationDto result = locationService.disableLocation(location.getId(), "manager");

        assertThat(location.isEnabled()).isFalse();
        assertThat(result.isEnabled()).isFalse();
        verify(locationIntegrity).checkLocationCanDisable(location);
    }

    @Test
    void shouldEnableLocation() {
        Company company = TestDataFactory.company();
        User user = TestDataFactory.companyAdmin(company);
        Location location = TestDataFactory.location(company);
        location.setEnabled(false);
        when(currentUserContext.getCurrentUser("manager")).thenReturn(user);
        when(currentUserContext.findManagedEntity(any(), eq(location.getId()), any(), any())).thenReturn(location);
        when(locationRepository.save(location)).thenReturn(location);
        when(locationMapper.toDto(location)).thenReturn(LocationDto.builder().enabled(true).build());

        LocationDto result = locationService.enableLocation(location.getId(), "manager");

        assertThat(location.isEnabled()).isTrue();
        assertThat(result.isEnabled()).isTrue();
        verify(locationIntegrity).checkLocationCanEnable(location);
        verify(companyIntegrity).checkCompanyCanManageLocations(company);
    }
}
