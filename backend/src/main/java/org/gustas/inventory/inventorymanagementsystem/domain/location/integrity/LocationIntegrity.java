package org.gustas.inventory.inventorymanagementsystem.domain.location.integrity;

import org.gustas.inventory.inventorymanagementsystem.domain.location.entity.Location;
import org.gustas.inventory.inventorymanagementsystem.domain.location.repository.LocationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class LocationIntegrity {

    public void checkLocationNotNull(Location location) {
        if (location == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found");
        }
    }

    public void checkLocationNameAvailable(LocationRepository locationRepository, UUID companyId, String name, UUID excludedLocationId) {
        boolean locationNameTaken = excludedLocationId == null
                ? locationRepository.existsByCompanyIdAndNameIgnoreCase(companyId, name.trim())
                : locationRepository.existsByCompanyIdAndNameIgnoreCaseAndIdNot(companyId, name.trim(), excludedLocationId);

        if (locationNameTaken) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Location name is already taken for this company");
        }
    }

    public void checkLocationCanDisable(Location location) {
        if (!location.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Location is already disabled");
        }
    }

    public void checkLocationCanEnable(Location location) {
        if (location.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Location is already enabled");
        }
    }

}
