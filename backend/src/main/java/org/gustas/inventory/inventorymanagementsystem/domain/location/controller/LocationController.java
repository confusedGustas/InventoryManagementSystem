package org.gustas.inventory.inventorymanagementsystem.domain.location.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gustas.inventory.inventorymanagementsystem.common.dto.CompanyOptionDto;
import org.gustas.inventory.inventorymanagementsystem.common.dto.PagedResponseWithCompaniesDto;
import org.gustas.inventory.inventorymanagementsystem.domain.location.dto.LocationDto;
import org.gustas.inventory.inventorymanagementsystem.domain.location.dto.SaveLocationDto;
import org.gustas.inventory.inventorymanagementsystem.domain.location.service.LocationService;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'COMPANY_ADMIN')")
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    public PagedResponseWithCompaniesDto<LocationDto, CompanyOptionDto> getLocations(
            Authentication authentication,
            @RequestParam(name = "page", defaultValue = "1") Integer page
    ) {
        return locationService.getLocations(page, authentication.getName());
    }

    @PostMapping
    public LocationDto saveLocation(Authentication authentication, @Valid @RequestBody SaveLocationDto saveLocationDto) {
        return locationService.saveLocation(saveLocationDto, authentication.getName());
    }

    @PutMapping("/{locationId}")
    public LocationDto updateLocation(Authentication authentication, @PathVariable UUID locationId, @Valid @RequestBody SaveLocationDto saveLocationDto) {
        return locationService.updateLocation(locationId, saveLocationDto, authentication.getName());
    }

    @PatchMapping("/{locationId}/disable")
    public LocationDto disableLocation(Authentication authentication, @PathVariable UUID locationId) {
        return locationService.disableLocation(locationId, authentication.getName());
    }

    @PatchMapping("/{locationId}/enable")
    public LocationDto enableLocation(Authentication authentication, @PathVariable UUID locationId) {
        return locationService.enableLocation(locationId, authentication.getName());
    }

}
