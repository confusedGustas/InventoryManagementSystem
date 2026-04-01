package org.gustas.inventory.inventorymanagementsystem.domain.location.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {

    private UUID id;
    private String name;
    private String address;
    private String phone;
    private String domain;
    private boolean enabled;
    private LocalDateTime createdOn;
    private UUID companyId;
    private String companyName;
    private String companyCity;
    private String companyCountry;
    private String companyStatus;

}
