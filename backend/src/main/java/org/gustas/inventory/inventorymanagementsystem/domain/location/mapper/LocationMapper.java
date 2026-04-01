package org.gustas.inventory.inventorymanagementsystem.domain.location.mapper;

import org.gustas.inventory.inventorymanagementsystem.common.dto.LocationOptionDto;
import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.Company;
import org.gustas.inventory.inventorymanagementsystem.domain.location.dto.LocationDto;
import org.gustas.inventory.inventorymanagementsystem.domain.location.dto.SaveLocationDto;
import org.gustas.inventory.inventorymanagementsystem.domain.location.entity.Location;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class LocationMapper {

    public Location toLocation(SaveLocationDto saveLocationDto, Company company) {
        return Location.builder()
                .name(saveLocationDto.getName().trim())
                .address(saveLocationDto.getAddress().trim())
                .phone(saveLocationDto.getPhone().trim())
                .domain(saveLocationDto.getDomain().trim().toLowerCase(Locale.ROOT))
                .enabled(true)
                .company(company)
                .build();
    }

    public LocationDto toDto(Location location) {
        return LocationDto.builder()
                .id(location.getId())
                .name(location.getName())
                .address(location.getAddress())
                .phone(location.getPhone())
                .domain(location.getDomain())
                .enabled(location.isEnabled())
                .createdOn(location.getCreatedOn())
                .companyId(location.getCompany().getId())
                .companyName(location.getCompany().getName())
                .companyCity(location.getCompany().getCity())
                .companyCountry(location.getCompany().getCountry())
                .companyStatus(location.getCompany().getStatus().name())
                .build();
    }

    public LocationOptionDto toLocationOptionDto(Location location) {
        return LocationOptionDto.builder()
                .id(location.getId())
                .name(location.getName())
                .companyId(location.getCompany().getId())
                .companyName(location.getCompany().getName())
                .build();
    }

    public void updateLocation(Location location, SaveLocationDto saveLocationDto, Company company) {
        location.setName(saveLocationDto.getName().trim());
        location.setAddress(saveLocationDto.getAddress().trim());
        location.setPhone(saveLocationDto.getPhone().trim());
        location.setDomain(saveLocationDto.getDomain().trim().toLowerCase(Locale.ROOT));
        location.setCompany(company);
    }

}
