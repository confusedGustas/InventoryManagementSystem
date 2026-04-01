package org.gustas.inventory.inventorymanagementsystem.domain.location.service;

import lombok.RequiredArgsConstructor;
import org.gustas.inventory.inventorymanagementsystem.common.Constants;
import org.gustas.inventory.inventorymanagementsystem.common.dto.CompanyOptionDto;
import org.gustas.inventory.inventorymanagementsystem.common.dto.PagedResponseDto;
import org.gustas.inventory.inventorymanagementsystem.common.dto.PagedResponseWithCompaniesDto;
import org.gustas.inventory.inventorymanagementsystem.common.mapper.PagedResponseMapper;
import org.gustas.inventory.inventorymanagementsystem.common.service.CurrentUserContext;
import org.gustas.inventory.inventorymanagementsystem.domain.auth.utils.AuthUtils;
import org.gustas.inventory.inventorymanagementsystem.domain.company.mapper.CompanyMapper;
import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.Company;
import org.gustas.inventory.inventorymanagementsystem.domain.company.integrity.CompanyIntegrity;
import org.gustas.inventory.inventorymanagementsystem.domain.location.dto.LocationDto;
import org.gustas.inventory.inventorymanagementsystem.domain.location.dto.SaveLocationDto;
import org.gustas.inventory.inventorymanagementsystem.domain.location.entity.Location;
import org.gustas.inventory.inventorymanagementsystem.domain.location.integrity.LocationIntegrity;
import org.gustas.inventory.inventorymanagementsystem.domain.location.mapper.LocationMapper;
import org.gustas.inventory.inventorymanagementsystem.domain.location.repository.LocationRepository;
import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final CompanyIntegrity companyIntegrity;
    private final LocationIntegrity locationIntegrity;
    private final LocationMapper locationMapper;
    private final PagedResponseMapper pagedResponseMapper;
    private final CurrentUserContext currentUserContext;
    private final AuthUtils authUtils;
    private final CompanyMapper companyMapper;

    @Transactional(readOnly = true)
    public PagedResponseWithCompaniesDto<LocationDto, CompanyOptionDto> getLocations(Integer page, String username) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), Constants.DEFAULT_PAGE_SIZE);
        User currentUser = currentUserContext.getCurrentUser(username);

        Page<LocationDto> locationPage = (authUtils.isPlatformAdmin(currentUser)
                ? locationRepository.findAll(pageable)
                : locationRepository.findAllByCompanyId(currentUserContext.getManagedCompanyId(currentUser), pageable))
                .map(locationMapper::toDto);
        PagedResponseDto<LocationDto> pagedLocations = pagedResponseMapper.toDto(locationPage);

        List<CompanyOptionDto> companies = currentUserContext.resolveManageableCompanies(currentUser, companyMapper::toCompanyOptionDto);

        return pagedResponseMapper.toPagedWithCompanies(pagedLocations, companies);
    }

    @Transactional
    public LocationDto saveLocation(SaveLocationDto saveLocationDto, String username) {
        User currentUser = currentUserContext.getCurrentUser(username);
        Company company = currentUserContext.resolveCompany(saveLocationDto.getCompanyId(), currentUser);

        companyIntegrity.checkCompanyNotNull(company);
        companyIntegrity.checkCompanyCanManageLocations(company);
        locationIntegrity.checkLocationNameAvailable(locationRepository, company.getId(), saveLocationDto.getName(), null);

        Location location = locationMapper.toLocation(saveLocationDto, company);
        Location savedLocation = locationRepository.save(location);

        return locationMapper.toDto(savedLocation);
    }

    @Transactional
    public LocationDto updateLocation(UUID locationId, SaveLocationDto saveLocationDto, String username) {
        User currentUser = currentUserContext.getCurrentUser(username);
        Location location = currentUserContext.findManagedEntity(
                currentUser,
                locationId,
                locationRepository::findLocationById,
                locationRepository::findLocationByIdAndCompanyId
        );
        Company company = currentUserContext.resolveCompany(saveLocationDto.getCompanyId(), currentUser);

        locationIntegrity.checkLocationNotNull(location);
        companyIntegrity.checkCompanyNotNull(company);
        companyIntegrity.checkCompanyCanManageLocations(company);
        locationIntegrity.checkLocationNameAvailable(locationRepository, company.getId(), saveLocationDto.getName(), location.getId());

        locationMapper.updateLocation(location, saveLocationDto, company);

        return locationMapper.toDto(locationRepository.save(location));
    }

    @Transactional
    public LocationDto disableLocation(UUID locationId, String username) {
        User currentUser = currentUserContext.getCurrentUser(username);
        Location location = currentUserContext.findManagedEntity(
                currentUser,
                locationId,
                locationRepository::findLocationById,
                locationRepository::findLocationByIdAndCompanyId
        );

        locationIntegrity.checkLocationNotNull(location);
        locationIntegrity.checkLocationCanDisable(location);
        location.setEnabled(false);

        return locationMapper.toDto(locationRepository.save(location));
    }

    @Transactional
    public LocationDto enableLocation(UUID locationId, String username) {
        User currentUser = currentUserContext.getCurrentUser(username);
        Location location = currentUserContext.findManagedEntity(
                currentUser,
                locationId,
                locationRepository::findLocationById,
                locationRepository::findLocationByIdAndCompanyId
        );

        locationIntegrity.checkLocationNotNull(location);
        locationIntegrity.checkLocationCanEnable(location);
        companyIntegrity.checkCompanyCanManageLocations(location.getCompany());
        location.setEnabled(true);

        return locationMapper.toDto(locationRepository.save(location));
    }
}
